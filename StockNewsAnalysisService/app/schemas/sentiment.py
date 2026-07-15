from enum import StrEnum
from uuid import UUID

from pydantic import Field, field_validator

from app.schemas.base import ApiModel


class SentimentLabel(StrEnum):
    POSITIVE = "POSITIVE"
    NEUTRAL = "NEUTRAL"
    NEGATIVE = "NEGATIVE"
    MIXED = "MIXED"


class SentimentCompany(ApiModel):
    company_id: UUID
    name: str = Field(min_length=1, max_length=300)
    ticker: str | None = Field(default=None, max_length=30)
    evidence: list[str] = Field(default_factory=list, max_length=20)


class CompanySentimentRequest(ApiModel):
    article_id: UUID
    title: str = Field(default="", max_length=1_000)
    summary: str = Field(default="", max_length=5_000)
    content: str = Field(default="", max_length=100_000)
    companies: list[SentimentCompany] = Field(min_length=1, max_length=50)

    @field_validator("companies")
    @classmethod
    def unique_company_ids(cls, companies: list[SentimentCompany]) -> list[SentimentCompany]:
        ids = [company.company_id for company in companies]
        if len(ids) != len(set(ids)):
            raise ValueError("companies must contain unique companyId values")
        return companies


class SentimentScores(ApiModel):
    positive: float = Field(ge=0.0, le=1.0)
    neutral: float = Field(ge=0.0, le=1.0)
    negative: float = Field(ge=0.0, le=1.0)
    mixed: float = Field(ge=0.0, le=1.0)


class SentimentResult(ApiModel):
    company_id: UUID
    sentiment: SentimentLabel
    confidence: float = Field(ge=0.0, le=1.0)
    scores: SentimentScores
    evidence: list[str]


class CompanySentimentResponse(ApiModel):
    article_id: UUID
    results: list[SentimentResult]
    model_version: str

