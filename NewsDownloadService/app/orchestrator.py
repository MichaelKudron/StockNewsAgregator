import asyncio
from dataclasses import dataclass
from datetime import datetime, timedelta, timezone
from email.utils import parsedate_to_datetime
from typing import Any, Optional
from urllib.request import Request, urlopen

import feedparser
import structlog
import trafilatura

from app.config import settings
from app.models import (
    ArticleDto,
    ContentSource,
    FetchError,
    FetchRequest,
    FetchResponse,
)
from app.paywall_detector import detect_paywall
from app.sources import NewsSource, list_sources
from app.text_cleaner import clean_text

log = structlog.get_logger(__name__)


@dataclass
class ScrapedArticle:
    content: Optional[str] = None
    author: Optional[str] = None
    category: Optional[str] = None


def _entry_url(entry: Any) -> Optional[str]:
    url = getattr(entry, "link", None)
    if url:
        return str(url)

    links = getattr(entry, "links", None) or []
    for link in links:
        href = link.get("href")
        if href:
            return str(href)

    return None


def _entry_text(entry: Any, field: str) -> Optional[str]:
    value = getattr(entry, field, None)
    if not value:
        return None
    return clean_text(str(value))


def _entry_summary(entry: Any) -> Optional[str]:
    return _entry_text(entry, "summary") or _entry_text(entry, "description")


def _entry_category(entry: Any) -> Optional[str]:
    category = _entry_text(entry, "category")
    if category:
        return category

    tags = getattr(entry, "tags", None) or []
    for tag in tags:
        term = tag.get("term") if isinstance(tag, dict) else getattr(tag, "term", None)
        cleaned = clean_text(term)
        if cleaned:
            return cleaned

    return None


def _entry_published_at(entry: Any) -> Optional[datetime]:
    for attr in ("published", "updated", "created"):
        raw = getattr(entry, attr, None)
        if not raw:
            continue
        try:
            parsed = parsedate_to_datetime(str(raw))
        except (TypeError, ValueError, IndexError, OverflowError):
            continue

        if parsed.tzinfo:
            parsed = parsed.astimezone(timezone.utc).replace(tzinfo=None)
        return parsed

    return None


def _download_url(url: str) -> bytes:
    request = Request(url, headers={"User-Agent": settings.http_user_agent})
    with urlopen(request, timeout=settings.http_timeout_seconds) as response:
        return response.read()


def _scrape_article(url: str) -> ScrapedArticle:
    downloaded = trafilatura.fetch_url(url)
    if not downloaded:
        return ScrapedArticle()

    metadata = trafilatura.extract_metadata(downloaded)

    content = trafilatura.extract(
        downloaded,
        include_comments=False,
        include_tables=False,
        output_format="txt",
    )
    scraped = ScrapedArticle(content=clean_text(content, keep_paragraphs=True))

    if metadata:
        scraped.author = clean_text(metadata.author)
        if metadata.categories:
            scraped.category = clean_text(", ".join(metadata.categories))

    return scraped


def _content_source(content: Optional[str], summary: Optional[str]) -> tuple[ContentSource, int]:
    if not content:
        return ContentSource.RSS_ONLY, len(summary or "")

    if detect_paywall(content, settings.paywall_min_content_length):
        return ContentSource.TRUNCATED_PAYWALL, len(content)

    return ContentSource.FULL_SCRAPED, len(content)


def _build_article(source: NewsSource, entry: Any) -> Optional[ArticleDto]:
    url = _entry_url(entry)
    title = _entry_text(entry, "title")
    if not url or not title:
        return None

    summary = _entry_summary(entry)
    scraped = _scrape_article(url)
    content_source, content_length = _content_source(scraped.content, summary)

    return ArticleDto(
        source_code=source.code.upper(),
        url=url,
        title=title,
        summary=summary,
        content=scraped.content,
        content_source=content_source,
        content_length=content_length,
        author=scraped.author or _entry_text(entry, "author"),
        category=scraped.category or _entry_category(entry),
        published_at=_entry_published_at(entry),
    )


def _is_entry_too_old(entry: Any, cutoff: Optional[datetime]) -> bool:
    if not cutoff:
        return False

    published_at = _entry_published_at(entry)
    return bool(published_at and published_at < cutoff)


def _fetch_source(source: NewsSource, cutoff: Optional[datetime]) -> tuple[list[ArticleDto], Optional[str]]:
    feed_bytes = _download_url(str(source.rss_url))
    parsed = feedparser.parse(feed_bytes)
    if getattr(parsed, "bozo", False):
        error = getattr(parsed, "bozo_exception", None)
        return [], f"Invalid RSS feed: {error}"

    articles: list[ArticleDto] = []
    for entry in parsed.entries:
        if _is_entry_too_old(entry, cutoff):
            continue

        article = _build_article(source, entry)
        if article:
            articles.append(article)

    return articles, None


async def _fetch_source_async(
    source: NewsSource,
    cutoff: Optional[datetime],
    semaphore: asyncio.Semaphore,
) -> tuple[NewsSource, list[ArticleDto], Optional[str]]:
    async with semaphore:
        try:
            articles, error = await asyncio.to_thread(_fetch_source, source, cutoff)
            log.info("source_fetched", source=source.code, count=len(articles), error=error)
            return source, articles, error
        except Exception as exc:
            log.error("source_error", source=source.code, error=str(exc))
            return source, [], str(exc)


def _cutoff(max_age_hours: Optional[int]) -> Optional[datetime]:
    if not max_age_hours:
        return None
    return datetime.now(timezone.utc).replace(tzinfo=None) - timedelta(hours=max_age_hours)


async def fetch_orchestrate(request: FetchRequest) -> FetchResponse:
    semaphore = asyncio.Semaphore(settings.max_concurrent_fetches)
    cutoff = _cutoff(request.max_age_hours)
    sources = list_sources()
    errors: list[FetchError] = []

    results = await asyncio.gather(
        *(_fetch_source_async(source, cutoff, semaphore) for source in sources)
    )

    seen_urls: set[str] = set()
    articles: list[ArticleDto] = []

    for source, source_articles, error in results:
        if error:
            errors.append(FetchError(source_code=source.code.upper(), message=error))
            continue

        for article in source_articles:
            if article.url in seen_urls:
                continue

            seen_urls.add(article.url)
            articles.append(article)

    articles.sort(key=lambda item: item.published_at or datetime.min, reverse=True)

    return FetchResponse(
        fetched_at=datetime.now(timezone.utc).replace(tzinfo=None),
        articles=articles,
        errors=errors,
    )
