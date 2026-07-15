import re

from app.models.engine import AnalysisEngine, RelevancePrediction, SentimentPrediction
from app.schemas.matching import CompanyCandidate, MatchLevel
from app.schemas.sentiment import SentimentLabel
from app.services.context_service import candidate_terms, contains_whole_phrase


POSITIVE_WORDS = ("zysk", "wzrost", "rekord", "umowa", "dywidend", "obniżył koszty")
NEGATIVE_WORDS = ("strata", "spadek", "kara", "pozew", "zadłuż", "upadło", "ryzyko")


class HeuristicEngine(AnalysisEngine):
    """Deterministic development backend. It is not a production AI model."""

    @property
    def version(self) -> str:
        return "heuristic-v1"

    def predict_relevance(
        self,
        context: str,
        candidate: CompanyCandidate,
    ) -> RelevancePrediction:
        matches = [term for term in candidate_terms(candidate) if contains_whole_phrase(context, term)]
        if not matches:
            return RelevancePrediction(
                False, MatchLevel.NONE, 0.98, "Brak pełnego tokenu lub frazy spółki w kontekście"
            )

        strongest = max(matches, key=len)
        confidence = 0.97 if len(strongest) >= 5 else 0.76
        return RelevancePrediction(
            True, MatchLevel.TOPIC, confidence, f"Wykryto pełną frazę: {strongest}"
        )

    def predict_sentiment(self, context: str, company_name: str) -> SentimentPrediction:
        normalized = context.casefold()
        positive = sum(bool(re.search(pattern, normalized)) for pattern in POSITIVE_WORDS)
        negative = sum(bool(re.search(pattern, normalized)) for pattern in NEGATIVE_WORDS)

        if positive and negative:
            label = SentimentLabel.MIXED
        elif positive:
            label = SentimentLabel.POSITIVE
        elif negative:
            label = SentimentLabel.NEGATIVE
        else:
            label = SentimentLabel.NEUTRAL

        scores = {item: 0.05 for item in SentimentLabel}
        scores[label] = 0.85
        return SentimentPrediction(label, scores[label], scores)

