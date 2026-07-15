from app.models.engine import AnalysisEngine
from app.schemas.sentiment import (
    CompanySentimentRequest,
    CompanySentimentResponse,
    SentimentLabel,
    SentimentResult,
    SentimentScores,
)
from app.services.context_service import sentiment_context


class SentimentService:
    def __init__(self, engine: AnalysisEngine, max_context_chars: int) -> None:
        self._engine = engine
        self._max_context_chars = max_context_chars

    @property
    def ready(self) -> bool:
        return self._engine is not None

    def analyze(self, request: CompanySentimentRequest) -> CompanySentimentResponse:
        results: list[SentimentResult] = []
        for company in request.companies:
            context, evidence = sentiment_context(
                request.title,
                request.summary,
                request.content,
                company.name,
                company.evidence,
                self._max_context_chars,
            )
            prediction = self._engine.predict_sentiment(context, company.name)
            results.append(
                SentimentResult(
                    company_id=company.company_id,
                    sentiment=prediction.label,
                    confidence=prediction.confidence,
                    scores=SentimentScores(
                        positive=prediction.scores.get(SentimentLabel.POSITIVE, 0.0),
                        neutral=prediction.scores.get(SentimentLabel.NEUTRAL, 0.0),
                        negative=prediction.scores.get(SentimentLabel.NEGATIVE, 0.0),
                        mixed=prediction.scores.get(SentimentLabel.MIXED, 0.0),
                    ),
                    evidence=evidence,
                )
            )

        return CompanySentimentResponse(
            article_id=request.article_id,
            results=results,
            model_version=self._engine.version,
        )
