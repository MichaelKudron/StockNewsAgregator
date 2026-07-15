from enum import StrEnum
from uuid import UUID

from pydantic import Field, field_validator

from app.schemas.base import ApiModel


class MatchLevel(StrEnum):
    NONE = "NONE"
    MENTION = "MENTION"
    TOPIC = "TOPIC"


class CompanyCandidate(ApiModel):
    company_id: UUID
    name: str = Field(min_length=1, max_length=300)
    short_name: str | None = Field(default=None, max_length=150)
    ticker: str | None = Field(default=None, max_length=30)
    isin: str | None = Field(default=None, max_length=20)
    aliases: list[str] = Field(default_factory=list, max_length=30)
    matched_phrase: str | None = Field(default=None, max_length=300)
    rule_score: float | None = None


class CompanyMatchingRequest(ApiModel):
    article_id: UUID
    title: str = Field(default="", max_length=1_000)
    summary: str = Field(default="", max_length=5_000)
    content: str = Field(default="", max_length=100_000)
    candidates: list[CompanyCandidate] = Field(default_factory=list, max_length=50)

    @field_validator("candidates")
    @classmethod
    def unique_company_ids(cls, candidates: list[CompanyCandidate]) -> list[CompanyCandidate]:
        ids = [candidate.company_id for candidate in candidates]
        if len(ids) != len(set(ids)):
            raise ValueError("candidates must contain unique companyId values")
        return candidates


class MatchingResult(ApiModel):
    company_id: UUID
    relevant: bool
    match_level: MatchLevel
    confidence: float = Field(ge=0.0, le=1.0)
    evidence: str | None = None
    reason: str


class CompanyMatchingResponse(ApiModel):
    article_id: UUID
    results: list[MatchingResult]
    model_version: str

