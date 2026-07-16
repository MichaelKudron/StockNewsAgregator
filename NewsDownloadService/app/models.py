from datetime import datetime
from enum import Enum
from typing import Optional

from pydantic import BaseModel, Field


class SourceType(str, Enum):
    NEWS = "NEWS"


class ContentSource(str, Enum):
    FULL_SCRAPED = "FULL_SCRAPED"
    TRUNCATED_PAYWALL = "TRUNCATED_PAYWALL"
    RSS_ONLY = "RSS_ONLY"


class FetchRequest(BaseModel):
    max_age_hours: Optional[int] = Field(
        default=24,
        ge=1,
        description="Pobierz newsy nie starsze niz podana liczba godzin.",
    )

    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "max_age_hours": 24,
                }
            ]
        }
    }


class ArticleDto(BaseModel):
    source_code: str
    source_type: SourceType = SourceType.NEWS
    url: str
    title: str
    summary: Optional[str] = None
    content: Optional[str] = None
    content_source: ContentSource
    content_length: int
    author: Optional[str] = None
    category: Optional[str] = None
    published_at: Optional[datetime] = None


class FetchError(BaseModel):
    source_code: str
    message: str


class FetchResponse(BaseModel):
    fetched_at: datetime
    articles: list[ArticleDto]
    errors: list[FetchError]


class HealthResponse(BaseModel):
    status: str
    version: str
