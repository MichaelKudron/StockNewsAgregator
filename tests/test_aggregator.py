"""Tests for the NewsAggregator class."""

from datetime import datetime
from unittest.mock import MagicMock, patch

import pytest

from gpw_news_aggregator.aggregator import NewsAggregator, _strip_html
from gpw_news_aggregator.models import NewsArticle, NewsSource


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _make_entry(title="Test Title", link="https://example.com/1",
                published="Mon, 17 Mar 2026 10:00:00 +0000",
                summary="<p>Some <b>summary</b> text.</p>",
                tags=None):
    """Create a minimal feedparser-like entry object."""
    entry = MagicMock()
    entry.title = title
    entry.link = link
    entry.published = published
    entry.summary = summary
    entry.tags = tags or []
    # feedparser uses attribute access; remove 'updated'/'created' by default
    del entry.updated
    del entry.created
    return entry


def _make_feed(entries=None, bozo=False, bozo_exception=None):
    feed = MagicMock()
    feed.entries = entries or []
    feed.bozo = bozo
    feed.bozo_exception = bozo_exception
    return feed


# ---------------------------------------------------------------------------
# _strip_html
# ---------------------------------------------------------------------------

class TestStripHtml:
    def test_removes_tags(self):
        assert _strip_html("<p>Hello <b>world</b></p>") == "Hello world"

    def test_plain_text_unchanged(self):
        assert _strip_html("No tags here.") == "No tags here."

    def test_empty_string(self):
        assert _strip_html("") == ""

    def test_nested_tags(self):
        assert _strip_html("<div><span>Text</span></div>") == "Text"


# ---------------------------------------------------------------------------
# NewsAggregator._parse_entry
# ---------------------------------------------------------------------------

class TestParseEntry:
    def test_parses_valid_entry(self):
        entry = _make_entry()
        article = NewsAggregator._parse_entry(entry, "TestSource")
        assert article is not None
        assert article.title == "Test Title"
        assert article.url == "https://example.com/1"
        assert article.source_name == "TestSource"
        assert article.published is not None
        assert article.summary == "Some summary text."

    def test_returns_none_without_title(self):
        entry = _make_entry(title="")
        assert NewsAggregator._parse_entry(entry, "S") is None

    def test_returns_none_without_url(self):
        entry = _make_entry(link="")
        assert NewsAggregator._parse_entry(entry, "S") is None

    def test_parses_tags(self):
        tags = [MagicMock(get=lambda k, d="": "GPW" if k == "term" else d)]
        entry = _make_entry(tags=tags)
        article = NewsAggregator._parse_entry(entry, "S")
        assert "GPW" in article.tags

    def test_handles_invalid_date(self):
        entry = _make_entry(published="not-a-date")
        article = NewsAggregator._parse_entry(entry, "S")
        assert article is not None
        assert article.published is None


# ---------------------------------------------------------------------------
# NewsAggregator.fetch_from_source
# ---------------------------------------------------------------------------

class TestFetchFromSource:
    def test_returns_articles(self):
        source = NewsSource(name="Test", url="https://example.com/rss")
        feed = _make_feed(entries=[_make_entry(), _make_entry(title="Article 2", link="https://example.com/2")])

        with patch("gpw_news_aggregator.aggregator.feedparser.parse", return_value=feed):
            aggregator = NewsAggregator(sources=[source])
            articles = aggregator.fetch_from_source(source)

        assert len(articles) == 2
        assert articles[0].source_name == "Test"

    def test_handles_fetch_exception_gracefully(self):
        source = NewsSource(name="Broken", url="https://broken.example.com/rss")

        with patch("gpw_news_aggregator.aggregator.feedparser.parse", side_effect=Exception("network error")):
            aggregator = NewsAggregator(sources=[source])
            articles = aggregator.fetch_from_source(source)

        assert articles == []

    def test_logs_warning_for_bozo_feed(self, caplog):
        source = NewsSource(name="Bozo", url="https://example.com/rss")
        feed = _make_feed(bozo=True, bozo_exception=Exception("bozo error"), entries=[])

        with patch("gpw_news_aggregator.aggregator.feedparser.parse", return_value=feed):
            aggregator = NewsAggregator(sources=[source])
            import logging
            with caplog.at_level(logging.WARNING, logger="gpw_news_aggregator.aggregator"):
                aggregator.fetch_from_source(source)

        assert any("bozo error" in r.message or "Bozo" in r.message for r in caplog.records)


# ---------------------------------------------------------------------------
# NewsAggregator.fetch_all
# ---------------------------------------------------------------------------

class TestFetchAll:
    def test_combines_and_sorts_by_date(self):
        source1 = NewsSource(name="S1", url="https://s1.com/rss")
        source2 = NewsSource(name="S2", url="https://s2.com/rss")

        older = NewsArticle(title="Old", url="https://s1.com/1", source_name="S1",
                            published=datetime(2026, 3, 15))
        newer = NewsArticle(title="New", url="https://s2.com/1", source_name="S2",
                            published=datetime(2026, 3, 17))

        aggregator = NewsAggregator(sources=[source1, source2])
        with patch.object(aggregator, "fetch_from_source", side_effect=[[older], [newer]]):
            articles = aggregator.fetch_all()

        assert articles[0].title == "New"
        assert articles[1].title == "Old"

    def test_articles_without_date_appear_last(self):
        source = NewsSource(name="S", url="https://s.com/rss")
        dated = NewsArticle(title="Dated", url="https://s.com/1", source_name="S",
                            published=datetime(2026, 1, 1))
        no_date = NewsArticle(title="NoDate", url="https://s.com/2", source_name="S")

        aggregator = NewsAggregator(sources=[source])
        with patch.object(aggregator, "fetch_from_source", return_value=[no_date, dated]):
            articles = aggregator.fetch_all()

        assert articles[0].title == "Dated"
        assert articles[1].title == "NoDate"


# ---------------------------------------------------------------------------
# NewsAggregator.search
# ---------------------------------------------------------------------------

class TestSearch:
    def test_filters_by_title(self):
        source = NewsSource(name="S", url="https://s.com/rss")
        match = NewsArticle(title="GPW Index Rises", url="https://s.com/1", source_name="S")
        no_match = NewsArticle(title="Weather Report", url="https://s.com/2", source_name="S")

        aggregator = NewsAggregator(sources=[source])
        with patch.object(aggregator, "fetch_all", return_value=[match, no_match]):
            results = aggregator.search("gpw")

        assert len(results) == 1
        assert results[0].title == "GPW Index Rises"

    def test_filters_by_summary(self):
        source = NewsSource(name="S", url="https://s.com/rss")
        match = NewsArticle(title="Market Update", url="https://s.com/1", source_name="S",
                            summary="The WIG20 index closed higher today.")
        no_match = NewsArticle(title="Other News", url="https://s.com/2", source_name="S",
                               summary="Unrelated content.")

        aggregator = NewsAggregator(sources=[source])
        with patch.object(aggregator, "fetch_all", return_value=[match, no_match]):
            results = aggregator.search("WIG20")

        assert len(results) == 1

    def test_returns_empty_when_no_match(self):
        source = NewsSource(name="S", url="https://s.com/rss")
        article = NewsArticle(title="Tech News", url="https://s.com/1", source_name="S")

        aggregator = NewsAggregator(sources=[source])
        with patch.object(aggregator, "fetch_all", return_value=[article]):
            results = aggregator.search("blockchain")

        assert results == []


# ---------------------------------------------------------------------------
# NewsAggregator default sources
# ---------------------------------------------------------------------------

class TestDefaultSources:
    def test_uses_default_sources_when_none_provided(self):
        from gpw_news_aggregator.sources import DEFAULT_SOURCES
        aggregator = NewsAggregator()
        assert aggregator.sources == DEFAULT_SOURCES

    def test_accepts_custom_sources(self):
        custom = [NewsSource(name="Custom", url="https://custom.com/rss")]
        aggregator = NewsAggregator(sources=custom)
        assert aggregator.sources == custom
