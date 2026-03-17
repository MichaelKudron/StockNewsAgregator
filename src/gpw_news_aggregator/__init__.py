"""GPW News Aggregator - collects news from Polish financial markets."""

from gpw_news_aggregator.models import NewsArticle, NewsSource
from gpw_news_aggregator.aggregator import NewsAggregator

__all__ = ["NewsArticle", "NewsSource", "NewsAggregator"]
__version__ = "0.1.0"
