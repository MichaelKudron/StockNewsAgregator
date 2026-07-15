from threading import Lock

from transformers import pipeline

from app.models.engine import AnalysisEngine, RelevancePrediction, SentimentPrediction
from app.schemas.matching import CompanyCandidate, MatchLevel
from app.schemas.sentiment import SentimentLabel


class TransformersEngine(AnalysisEngine):
    def __init__(self, model_name: str, device: int) -> None:
        self._model_name = model_name
        self._classifier = pipeline(
            task="zero-shot-classification",
            model=model_name,
            device=device,
        )
        self._lock = Lock()

    @property
    def version(self) -> str:
        return self._model_name

    def _classify(self, context: str, labels: list[str]) -> dict[str, float]:
        with self._lock:
            output = self._classifier(
                context,
                candidate_labels=labels,
                multi_label=False,
                hypothesis_template="Ten tekst przedstawia sytuację, w której {}.",
            )
        return dict(zip(output["labels"], output["scores"], strict=True))

    def predict_relevance(
        self,
        context: str,
        candidate: CompanyCandidate,
    ) -> RelevancePrediction:
        relevant_label = f"informacja rzeczywiście dotyczy spółki {candidate.name}"
        irrelevant_label = f"podobieństwo do nazwy spółki {candidate.name} jest przypadkowe"
        scores = self._classify(context, [relevant_label, irrelevant_label])
        relevant_score = float(scores[relevant_label])
        irrelevant_score = float(scores[irrelevant_label])
        relevant = relevant_score >= irrelevant_score
        confidence = relevant_score if relevant else irrelevant_score
        reason = (
            "Model potwierdził związek treści ze spółką"
            if relevant
            else "Model uznał wystąpienie za przypadkowe lub niezwiązane ze spółką"
        )
        return RelevancePrediction(
            relevant=relevant,
            level=MatchLevel.TOPIC if relevant else MatchLevel.NONE,
            confidence=confidence,
            reason=reason,
        )

    def predict_sentiment(self, context: str, company_name: str) -> SentimentPrediction:
        labels = {
            SentimentLabel.POSITIVE: f"informacja ma pozytywny wpływ na spółkę {company_name}",
            SentimentLabel.NEUTRAL: f"informacja ma neutralny wpływ na spółkę {company_name}",
            SentimentLabel.NEGATIVE: f"informacja ma negatywny wpływ na spółkę {company_name}",
            SentimentLabel.MIXED: f"informacja ma jednocześnie pozytywny i negatywny wpływ na spółkę {company_name}",
        }
        raw_scores = self._classify(context, list(labels.values()))
        scores = {label: float(raw_scores[text]) for label, text in labels.items()}
        best_label = max(scores, key=scores.get)
        return SentimentPrediction(best_label, scores[best_label], scores)

