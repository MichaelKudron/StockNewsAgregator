# Stock News Analysis Service

Serwis FastAPI realizujący dwa zadania:

- potwierdzanie powiązań artykuł-spółka,
- analizę sentymentu informacji względem konkretnej spółki.

## Uruchomienie

Wymagany jest Python 3.11 lub nowszy.

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -e ".[dev]"
Copy-Item .env.example .env
uvicorn app.main:app --host 127.0.0.1 --port 8004 --reload
```

Dokumentacja OpenAPI będzie dostępna pod adresem:

```text
http://127.0.0.1:8004/docs
```

Przy pierwszym uruchomieniu backend `transformers` pobierze model z Hugging Face.
Do szybkiego uruchomienia bez modelu ustaw w `.env`:

```text
MODEL_BACKEND=heuristic
```

Backend heurystyczny służy wyłącznie do developmentu i testów.

## Endpointy

### Dopasowanie spółek

```http
POST /api/v1/company-matching
```

```json
{
  "articleId": "6e3f65d9-79df-4c15-b5fd-d14d3ad82bb1",
  "title": "Orlen tnie ceny paliw",
  "summary": "Koncern zmienił ceny hurtowe.",
  "content": "Orlen obniżył ceny benzyny.",
  "candidates": [
    {
      "companyId": "4c66eae7-bcf8-41e6-8149-66eaf8107c4a",
      "name": "ORLEN",
      "shortName": "Orlen",
      "ticker": "PKN",
      "isin": "PLPKN0000018",
      "aliases": ["PKN Orlen", "Orlenu"],
      "matchedPhrase": "Orlen",
      "ruleScore": 150
    }
  ]
}
```

### Sentyment wobec spółki

```http
POST /api/v1/company-sentiment
```

```json
{
  "articleId": "6e3f65d9-79df-4c15-b5fd-d14d3ad82bb1",
  "title": "Orlen tnie ceny paliw",
  "summary": "Koncern zmienił ceny hurtowe.",
  "content": "Orlen obniżył ceny benzyny, ale podniósł ceny diesla.",
  "companies": [
    {
      "companyId": "4c66eae7-bcf8-41e6-8149-66eaf8107c4a",
      "name": "ORLEN",
      "ticker": "PKN",
      "evidence": ["Orlen obniżył ceny benzyny, ale podniósł ceny diesla."]
    }
  ]
}
```

Klasy sentymentu: `POSITIVE`, `NEUTRAL`, `NEGATIVE`, `MIXED`.

Poziomy dopasowania w odpowiedzi `company-matching` (pole `matchLevel`):

- `TOPIC` — artykuł dotyczy spółki (`relevant: true`),
- `MENTION` — spółka jest tylko wymieniona w tekście (`relevant: false`),
- `NONE` — brak frazy spółki w tekście lub przypadkowa zbieżność (`relevant: false`).

## Testy

```powershell
pytest
```

## Integracja

Java generuje szeroką listę kandydatów i wywołuje `company-matching`. Linki z
zaakceptowanym wynikiem zapisuje w swojej bazie, a następnie przekazuje je do
`company-sentiment`. Serwis AI pozostaje bezstanowy i nie zapisuje danych.
