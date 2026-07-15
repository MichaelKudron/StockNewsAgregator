from uuid import uuid4

from fastapi.testclient import TestClient

from app.core.config import Settings
from app.main import create_app


def create_test_client() -> TestClient:
    settings = Settings(model_backend="heuristic")
    return TestClient(create_app(settings))


def test_matching_rejects_substring_and_accepts_company() -> None:
    article_id = str(uuid4())
    orlen_id = str(uuid4())
    lena_id = str(uuid4())
    payload = {
        "articleId": article_id,
        "title": "Orlen tnie ceny paliw",
        "summary": "Orlen zmienił cennik.",
        "content": "Orlen obniżył ceny benzyny.",
        "candidates": [
            {"companyId": orlen_id, "name": "ORLEN", "matchedPhrase": "Orlen"},
            {"companyId": lena_id, "name": "LENA", "shortName": "LEN", "matchedPhrase": "LEN"},
        ],
    }

    with create_test_client() as client:
        response = client.post("/api/v1/company-matching", json=payload)

    assert response.status_code == 200
    results = {item["companyId"]: item for item in response.json()["results"]}
    assert results[orlen_id]["relevant"] is True
    assert results[orlen_id]["matchLevel"] == "TOPIC"
    assert results[lena_id]["relevant"] is False
    assert results[lena_id]["matchLevel"] == "NONE"


def test_matching_marks_enumeration_as_mention() -> None:
    orlen_id, kghm_id, pzu_id = str(uuid4()), str(uuid4()), str(uuid4())
    payload = {
        "articleId": str(uuid4()),
        "title": "Nowe przepisy dla rynku energii",
        "summary": "Rząd przedstawił projekt ustawy.",
        "content": (
            "Rząd przedstawił projekt nowej ustawy energetycznej. "
            "Regulacja obejmie m.in. spółki ORLEN, KGHM i PZU. "
            "Projekt trafi teraz do konsultacji publicznych."
        ),
        "candidates": [
            {"companyId": orlen_id, "name": "ORLEN"},
            {"companyId": kghm_id, "name": "KGHM"},
            {"companyId": pzu_id, "name": "PZU"},
        ],
    }

    with create_test_client() as client:
        response = client.post("/api/v1/company-matching", json=payload)

    assert response.status_code == 200
    for item in response.json()["results"]:
        assert item["matchLevel"] == "MENTION"
        assert item["relevant"] is False
        assert "ORLEN" in item["evidence"]


def test_sentiment_returns_result_for_each_company() -> None:
    company_id = str(uuid4())
    payload = {
        "articleId": str(uuid4()),
        "title": "Spółka podpisała nową umowę",
        "summary": "Umowa zwiększy przychody.",
        "content": "ORLEN podpisał korzystną umowę.",
        "companies": [
            {
                "companyId": company_id,
                "name": "ORLEN",
                "ticker": "PKN",
                "evidence": ["ORLEN podpisał korzystną umowę."],
            }
        ],
    }

    with create_test_client() as client:
        response = client.post("/api/v1/company-sentiment", json=payload)

    assert response.status_code == 200
    assert response.json()["results"][0]["companyId"] == company_id
    assert response.json()["results"][0]["sentiment"] == "POSITIVE"


def test_health_endpoints() -> None:
    with create_test_client() as client:
        assert client.get("/health/live").status_code == 200
        assert client.get("/health/ready").json() == {"status": "UP"}
