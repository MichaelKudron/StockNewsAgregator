"""Data models for the GPW News Aggregator."""

from dataclasses import dataclass, field
from datetime import datetime
from typing import Optional


@dataclass
class NewsSource:
    """Represents a news source (RSS feed) to aggregate from."""

    name: str
    url: str
    description: str = ""

    def __str__(self) -> str:
        return f"{self.name} ({self.url})"


@dataclass
class NewsArticle:
    """Represents a single news article fetched from a source."""

    title: str
    url: str
    source_name: str
    published: Optional[datetime] = None
    summary: str = ""
    tags: list = field(default_factory=list)

    def __str__(self) -> str:
        date_str = self.published.strftime("%Y-%m-%d %H:%M") if self.published else "unknown date"
        return f"[{date_str}] {self.title} — {self.source_name}"
