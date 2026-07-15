from uuid import uuid4

from app.schemas.matching import CompanyCandidate
from app.services.context_service import clean_text, contains_whole_phrase, matching_context


def test_whole_phrase_does_not_match_inside_another_word() -> None:
    assert not contains_whole_phrase("Magdalena przedstawiła raport", "LENA")
    assert not contains_whole_phrase("Orlen obniżył ceny", "LEN")
    assert contains_whole_phrase("LENA opublikowała raport", "LENA")


def test_repairs_mojibake() -> None:
    assert clean_text("Orlen obniĹĽyĹ‚ ceny") == "Orlen obniżył ceny"


def test_context_uses_matching_sentence_and_neighbours() -> None:
    candidate = CompanyCandidate(company_id=uuid4(), name="ORLEN")
    context, evidence = matching_context(
        "Rynek paliw",
        "Zmiana cennika.",
        "Pierwsze zdanie. Orlen obniżył ceny. Ostatnie zdanie.",
        candidate,
        8_000,
    )
    assert "Orlen obniżył ceny" in context
    assert evidence == "Orlen obniżył ceny."

