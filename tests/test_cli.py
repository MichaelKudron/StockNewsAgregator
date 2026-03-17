"""Tests for the CLI module."""

from unittest.mock import patch

import pytest

from gpw_news_aggregator.cli import main
from gpw_news_aggregator.models import NewsArticle, NewsSource
from gpw_news_aggregator.sources import DEFAULT_SOURCES
from datetime import datetime


def _make_articles(n=3):
    return [
        NewsArticle(
            title=f"Article {i}",
            url=f"https://example.com/{i}",
            source_name="TestSource",
            published=datetime(2026, 3, 17, 10, i),
            summary=f"Summary of article {i}.",
        )
        for i in range(n)
    ]


class TestCLIListSources:
    def test_list_sources_exits_zero(self, capsys):
        rc = main(["--list-sources"])
        assert rc == 0

    def test_list_sources_shows_source_names(self, capsys):
        main(["--list-sources"])
        captured = capsys.readouterr()
        for src in DEFAULT_SOURCES:
            assert src.name in captured.out


class TestCLIFetch:
    def test_fetch_all_displays_articles(self, capsys):
        articles = _make_articles(3)
        with patch("gpw_news_aggregator.cli.NewsAggregator") as MockAgg:
            MockAgg.return_value.fetch_all.return_value = articles
            rc = main([])
        assert rc == 0
        captured = capsys.readouterr()
        for article in articles:
            assert article.title in captured.out

    def test_count_flag_limits_output(self, capsys):
        articles = _make_articles(10)
        with patch("gpw_news_aggregator.cli.NewsAggregator") as MockAgg:
            MockAgg.return_value.fetch_all.return_value = articles
            rc = main(["-n", "2"])
        assert rc == 0
        captured = capsys.readouterr()
        assert "Article 0" in captured.out
        assert "Article 1" in captured.out
        assert "Article 2" not in captured.out

    def test_no_articles_message(self, capsys):
        with patch("gpw_news_aggregator.cli.NewsAggregator") as MockAgg:
            MockAgg.return_value.fetch_all.return_value = []
            rc = main([])
        assert rc == 0
        captured = capsys.readouterr()
        assert "No articles found" in captured.out


class TestCLISearch:
    def test_search_calls_aggregator_search(self, capsys):
        articles = _make_articles(1)
        with patch("gpw_news_aggregator.cli.NewsAggregator") as MockAgg:
            MockAgg.return_value.search.return_value = articles
            rc = main(["--search", "GPW"])
        assert rc == 0
        MockAgg.return_value.search.assert_called_once_with("GPW")

    def test_search_shows_query_in_output(self, capsys):
        with patch("gpw_news_aggregator.cli.NewsAggregator") as MockAgg:
            MockAgg.return_value.search.return_value = []
            main(["--search", "WIG20"])
        captured = capsys.readouterr()
        assert "WIG20" in captured.out
