from dataclasses import dataclass
from typing import Protocol

from app.schemas.matching import CompanyCandidate, MatchLevel
from app.schemas.sentiment import SentimentLabel


@dataclass(frozen=True)
class RelevancePrediction:
    relevant: bool
    level: MatchLevel
    confidence: float
    reason: str


@dataclass(frozen=True)
class SentimentPrediction:
    label: SentimentLabel
    confidence: float
    scores: dict[SentimentLabel, float]


class AnalysisEngine(Protocol):
    @property
    def version(self) -> str: ...

    def predict_relevance(
        self,
        context: str,
        candidate: CompanyCandidate,
    ) -> RelevancePrediction: ...

    def predict_sentiment(
        self,
        context: str,
        company_name: str,
    ) -> SentimentPrediction: ...

