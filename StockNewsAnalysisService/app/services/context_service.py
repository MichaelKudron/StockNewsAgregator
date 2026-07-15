import re

from ftfy import fix_text

from app.schemas.matching import CompanyCandidate


SENTENCE_BOUNDARY = re.compile(r"(?<=[.!?])\s+|[\r\n]+")


def clean_text(value: str | None) -> str:
    if not value:
        return ""
    repaired = fix_text(value)
    return re.sub(r"\s+", " ", repaired).strip()


def split_sentences(value: str) -> list[str]:
    return [part.strip() for part in SENTENCE_BOUNDARY.split(clean_text(value)) if part.strip()]


def contains_whole_phrase(text: str, phrase: str, case_sensitive: bool = False) -> bool:
    if not text or not phrase or not phrase.strip():
        return False
    flags = 0 if case_sensitive else re.IGNORECASE
    pattern = rf"(?<![\w]){re.escape(phrase.strip())}(?![\w])"
    return re.search(pattern, text, flags=flags) is not None


def candidate_terms(candidate: CompanyCandidate) -> list[str]:
    values = [
        candidate.name,
        candidate.short_name,
        candidate.ticker,
        candidate.isin,
        *candidate.aliases,
        candidate.matched_phrase,
    ]
    result: list[str] = []
    seen: set[str] = set()
    for value in values:
        if not value:
            continue
        normalized = clean_text(value).casefold()
        if normalized and normalized not in seen:
            seen.add(normalized)
            result.append(clean_text(value))
    return result


def matching_context(
    title: str,
    summary: str,
    content: str,
    candidate: CompanyCandidate,
    max_chars: int,
) -> tuple[str, str | None]:
    clean_title = clean_text(title)
    clean_summary = clean_text(summary)
    sentences = split_sentences(content)
    terms = candidate_terms(candidate)

    matching_indexes = [
        index
        for index, sentence in enumerate(sentences)
        if any(contains_whole_phrase(sentence, term) for term in terms)
    ]
    selected_indexes: set[int] = set()
    for index in matching_indexes:
        selected_indexes.update(range(max(0, index - 1), min(len(sentences), index + 2)))

    selected = [sentences[index] for index in sorted(selected_indexes)]
    evidence = next(
        (
            sentence
            for sentence in sentences
            if any(contains_whole_phrase(sentence, term) for term in terms)
        ),
        None,
    )

    sections = [part for part in (clean_title, clean_summary, " ".join(selected)) if part]
    context = "\n".join(sections)
    if not context:
        context = clean_text(content)
    return context[:max_chars], evidence


def sentiment_context(
    title: str,
    summary: str,
    content: str,
    company_name: str,
    supplied_evidence: list[str],
    max_chars: int,
) -> tuple[str, list[str]]:
    evidence = [clean_text(item) for item in supplied_evidence if clean_text(item)]
    if not evidence:
        evidence = [
            sentence
            for sentence in split_sentences(content)
            if contains_whole_phrase(sentence, company_name)
        ][:5]

    sections = [clean_text(title), clean_text(summary), *evidence]
    context = "\n".join(section for section in sections if section)
    if not context:
        context = clean_text(content)
    return context[:max_chars], evidence

