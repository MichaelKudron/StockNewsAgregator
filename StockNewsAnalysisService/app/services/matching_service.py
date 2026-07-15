from collections import Counter
from uuid import UUID

from app.models.engine import AnalysisEngine
from app.schemas.matching import (
    CompanyMatchingRequest,
    CompanyMatchingResponse,
    MatchingResult,
    MatchLevel,
)
from app.services.context_service import (
    candidate_terms,
    clean_text,
    contains_whole_phrase,
    matching_context,
    split_sentences,
)


ENUMERATION_MIN_COMPANIES = 3


class MatchingService:
    def __init__(self, engine: AnalysisEngine, max_context_chars: int) -> None:
        self._engine = engine
        self._max_context_chars = max_context_chars

    @property
    def ready(self) -> bool:
        return self._engine is not None

    def analyze(self, request: CompanyMatchingRequest) -> CompanyMatchingResponse:
        sentences = split_sentences(request.content)
        head = "\n".join(part for part in (clean_text(request.title), clean_text(request.summary)) if part)

        terms_by_candidate: dict[UUID, list[str]] = {
            candidate.company_id: candidate_terms(candidate) for candidate in request.candidates
        }
        hits_by_candidate: dict[UUID, list[int]] = {
            company_id: [
                index
                for index, sentence in enumerate(sentences)
                if any(contains_whole_phrase(sentence, term) for term in terms)
            ]
            for company_id, terms in terms_by_candidate.items()
        }
        candidates_per_sentence = Counter(
            index for hits in hits_by_candidate.values() for index in set(hits)
        )

        results: list[MatchingResult] = []
        for candidate in request.candidates:
            context, evidence = matching_context(
                request.title,
                request.summary,
                request.content,
                candidate,
                self._max_context_chars,
            )
            terms = terms_by_candidate[candidate.company_id]
            hits = hits_by_candidate[candidate.company_id]
            in_head = any(contains_whole_phrase(head, term) for term in terms)

            if not hits and not in_head:
                results.append(
                    MatchingResult(
                        company_id=candidate.company_id,
                        relevant=False,
                        match_level=MatchLevel.NONE,
                        confidence=0.98,
                        evidence=None,
                        reason="Żadna fraza spółki nie występuje w artykule — pominięto model",
                    )
                )
                continue

            only_enumerations = (
                hits
                and not in_head
                and all(
                    candidates_per_sentence[index] >= ENUMERATION_MIN_COMPANIES
                    for index in hits
                )
            )
            if only_enumerations:
                results.append(
                    MatchingResult(
                        company_id=candidate.company_id,
                        relevant=False,
                        match_level=MatchLevel.MENTION,
                        confidence=0.9,
                        evidence=sentences[hits[0]],
                        reason="Spółka występuje wyłącznie w wyliczeniu obok innych spółek",
                    )
                )
                continue

            prediction = self._engine.predict_relevance(context, candidate)
            results.append(
                MatchingResult(
                    company_id=candidate.company_id,
                    relevant=prediction.relevant,
                    match_level=prediction.level,
                    confidence=prediction.confidence,
                    evidence=evidence if prediction.level != MatchLevel.NONE else None,
                    reason=prediction.reason,
                )
            )

        return CompanyMatchingResponse(
            article_id=request.article_id,
            results=results,
            model_version=self._engine.version,
        )
