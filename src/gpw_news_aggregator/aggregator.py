"""Core aggregation logic for the GPW News Aggregator."""

import logging
from datetime import datetime
from typing import List, Optional

import feedparser
from dateutil import parser as dateutil_parser

from gpw_news_aggregator.models import NewsArticle, NewsSource
from gpw_news_aggregator.sources import DEFAULT_SOURCES

logger = logging.getLogger(__name__)


class NewsAggregator:
    """Fetches and aggregates news articles from GPW-related RSS feeds."""

    def __init__(self, sources: Optional[List[NewsSource]] = None):
        self.sources: List[NewsSource] = sources if sources is not None else list(DEFAULT_SOURCES)

    def fetch_from_source(self, source: NewsSource) -> List[NewsArticle]:
        """Fetch articles from a single RSS source.

        Args:
            source: The news source to fetch from.

        Returns:
            A list of NewsArticle objects parsed from the feed.
        """
        articles: List[NewsArticle] = []
        try:
            feed = feedparser.parse(source.url)
            if feed.bozo and feed.bozo_exception:
                logger.warning("Feed parse warning for %s: %s", source.name, feed.bozo_exception)

            for entry in feed.entries:
                article = self._parse_entry(entry, source.name)
                if article:
                    articles.append(article)

            logger.info("Fetched %d articles from %s", len(articles), source.name)
        except Exception as exc:  # noqa: BLE001
            logger.error("Failed to fetch from %s: %s", source.name, exc)

        return articles

    def fetch_all(self) -> List[NewsArticle]:
        """Fetch articles from all configured sources, sorted by date descending.

        Returns:
            A combined, date-sorted list of NewsArticle objects.
        """
        all_articles: List[NewsArticle] = []
        for source in self.sources:
            all_articles.extend(self.fetch_from_source(source))

        all_articles.sort(
            key=lambda a: a.published or datetime.min,
            reverse=True,
        )
        return all_articles

    def search(self, query: str) -> List[NewsArticle]:
        """Return articles whose title or summary contains the query string (case-insensitive).

        Args:
            query: The search term.

        Returns:
            Filtered list of matching NewsArticle objects.
        """
        query_lower = query.lower()
        return [
            a
            for a in self.fetch_all()
            if query_lower in a.title.lower() or query_lower in a.summary.lower()
        ]

    # ------------------------------------------------------------------
    # Helpers
    # ------------------------------------------------------------------

    @staticmethod
    def _parse_entry(entry: object, source_name: str) -> Optional[NewsArticle]:
        """Convert a feedparser entry dict into a NewsArticle."""
        title = getattr(entry, "title", "").strip()
        url = getattr(entry, "link", "").strip()

        if not title or not url:
            return None

        published: Optional[datetime] = None
        for attr in ("published", "updated", "created"):
            raw = getattr(entry, attr, None)
            if raw:
                try:
                    published = dateutil_parser.parse(raw)
                except (ValueError, OverflowError):
                    pass
                if published:
                    break

        summary_raw = getattr(entry, "summary", "") or ""
        # Strip basic HTML tags from summary
        summary = _strip_html(summary_raw).strip()

        tags = [t.get("term", "") for t in getattr(entry, "tags", []) if t.get("term")]

        return NewsArticle(
            title=title,
            url=url,
            source_name=source_name,
            published=published,
            summary=summary,
            tags=tags,
        )


def _strip_html(text: str) -> str:
    """Remove HTML tags from a string using a simple state machine."""
    result: List[str] = []
    inside_tag = False
    for char in text:
        if char == "<":
            inside_tag = True
        elif char == ">":
            inside_tag = False
        elif not inside_tag:
            result.append(char)
    return "".join(result)
