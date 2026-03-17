"""Tests for data models."""

from datetime import datetime

import pytest

from gpw_news_aggregator.models import NewsArticle, NewsSource


class TestNewsSource:
    def test_str(self):
        src = NewsSource(name="Test Source", url="https://example.com/rss")
        assert "Test Source" in str(src)
        assert "https://example.com/rss" in str(src)

    def test_description_default(self):
        src = NewsSource(name="Test", url="https://example.com/rss")
        assert src.description == ""


class TestNewsArticle:
    def test_str_with_date(self):
        article = NewsArticle(
            title="Market Surges",
            url="https://example.com/article",
            source_name="TestSource",
            published=datetime(2026, 3, 17, 10, 30),
        )
        text = str(article)
        assert "Market Surges" in text
        assert "TestSource" in text
        assert "2026-03-17" in text

    def test_str_without_date(self):
        article = NewsArticle(
            title="Market Surges",
            url="https://example.com/article",
            source_name="TestSource",
        )
        text = str(article)
        assert "unknown date" in text

    def test_tags_default_empty(self):
        article = NewsArticle(title="A", url="https://x.com", source_name="S")
        assert article.tags == []

    def test_summary_default_empty(self):
        article = NewsArticle(title="A", url="https://x.com", source_name="S")
        assert article.summary == ""
