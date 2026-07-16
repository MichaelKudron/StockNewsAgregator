import html
import re
from html.parser import HTMLParser
from typing import Optional


MOJIBAKE_MARKERS = (
    "\u00c2",  # Â
    "\u00c3",  # Ã
    "\u00c4",  # Ä
    "\u00c5",  # Å
    "\u0139",  # Ĺ
    "\u0102",  # Ă
    "\u00e2\u20ac",  # â€
    "\u00e2\u20ac\u201c",  # â€“
    "\u00e2\u20ac\u201d",  # â€”
    "\u00e2\u20ac\u00a6",  # â€¦
    "\u00e2\u201e\u00a2",  # â„¢
    "\u00e2\u201a\u00ac",  # â‚¬
)


class _TextExtractor(HTMLParser):
    def __init__(self) -> None:
        super().__init__(convert_charrefs=True)
        self._skip_depth = 0
        self._parts: list[str] = []

    def handle_starttag(self, tag: str, attrs: list[tuple[str, Optional[str]]]) -> None:
        if tag in {"script", "style", "noscript"}:
            self._skip_depth += 1
        elif tag in {"p", "br", "div", "li", "h1", "h2", "h3"}:
            self._parts.append("\n")

    def handle_endtag(self, tag: str) -> None:
        if tag in {"script", "style", "noscript"} and self._skip_depth:
            self._skip_depth -= 1
        elif tag in {"p", "div", "li", "h1", "h2", "h3"}:
            self._parts.append("\n")

    def handle_data(self, data: str) -> None:
        if not self._skip_depth:
            self._parts.append(data)

    def text(self) -> str:
        return "".join(self._parts)


def _mojibake_score(value: str) -> int:
    return sum(value.count(marker) for marker in MOJIBAKE_MARKERS) + value.count("\ufffd") * 3


def _fix_mojibake(value: str) -> str:
    best = value
    best_score = _mojibake_score(best)

    for _ in range(2):
        if best_score == 0:
            break

        improved = False
        for encoding in ("cp1250", "cp1252", "latin1"):
            try:
                candidate = best.encode(encoding).decode("utf-8")
            except (UnicodeEncodeError, UnicodeDecodeError):
                continue

            candidate_score = _mojibake_score(candidate)
            if candidate_score < best_score:
                best = candidate
                best_score = candidate_score
                improved = True

        if not improved:
            break

    return best


def clean_text(value: Optional[str], *, keep_paragraphs: bool = False) -> Optional[str]:
    if not value:
        return None

    raw = _fix_mojibake(html.unescape(str(value)))
    if "<" in raw and ">" in raw:
        parser = _TextExtractor()
        parser.feed(raw)
        raw = _fix_mojibake(parser.text())

    raw = raw.replace("\xa0", " ")
    raw = re.sub(r"[ \t\r\f\v]+", " ", raw)

    if keep_paragraphs:
        raw = re.sub(r" *\n+ *", "\n", raw)
        raw = re.sub(r"\n{3,}", "\n\n", raw)
    else:
        raw = re.sub(r"\s+", " ", raw)

    cleaned = raw.strip()
    return cleaned or None
