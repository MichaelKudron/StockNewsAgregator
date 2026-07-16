from pydantic import BaseModel, HttpUrl


class NewsSource(BaseModel):
    code: str
    name: str
    rss_url: HttpUrl


SUPPORTED_SOURCES: dict[str, NewsSource] = {
    "bankier": NewsSource(
        code="bankier",
        name="Bankier.pl - wiadomosci",
        rss_url="https://www.bankier.pl/rss/wiadomosci.xml",
    ),
    "money": NewsSource(
        code="money",
        name="Money.pl",
        rss_url="https://www.money.pl/rss/",
    ),
    "interia_biznes": NewsSource(
        code="interia_biznes",
        name="Interia Biznes",
        rss_url="https://biznes.interia.pl/feed",
    ),
}


def list_sources() -> list[NewsSource]:
    return list(SUPPORTED_SOURCES.values())
