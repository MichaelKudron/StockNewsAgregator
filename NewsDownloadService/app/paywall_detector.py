from typing import Optional


PAYWALL_PHRASES = [
    "zaloguj sie",
    "zaloguj się",
    "premium",
    "wykup dostep",
    "wykup dostęp",
    "czytaj dalej w prenumeracie",
    "subskrybuj",
    "prenumerata",
    "dostep tylko dla zalogowanych",
    "dostęp tylko dla zalogowanych",
    "tresc dostepna dla zalogowanych",
    "treść dostępna dla zalogowanych",
    "kup dostep",
    "kup dostęp",
]


def detect_paywall(content: Optional[str], min_length: int) -> bool:
    if not content or len(content.strip()) < min_length:
        return True

    normalized = content.lower()
    tail = normalized[len(normalized) * 2 // 3:]
    return any(phrase in tail for phrase in PAYWALL_PHRASES)
