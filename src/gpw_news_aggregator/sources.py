"""Default GPW-related news sources (RSS feeds)."""

from gpw_news_aggregator.models import NewsSource

DEFAULT_SOURCES = [
    NewsSource(
        name="Bankier.pl – GPW",
        url="https://www.bankier.pl/rss/wiadomosci.xml",
        description="Financial and stock market news from Bankier.pl",
    ),
    NewsSource(
        name="Money.pl – Giełda",
        url="https://www.money.pl/rss/gielda.xml",
        description="Stock exchange news from Money.pl",
    ),
    NewsSource(
        name="StockWatch.pl",
        url="https://stockwatch.pl/rss/",
        description="GPW company analysis and news from StockWatch.pl",
    ),
    NewsSource(
        name="Parkiet.com",
        url="https://www.parkiet.com/rss/Feed",
        description="Polish capital market news from Parkiet.com",
    ),
]
