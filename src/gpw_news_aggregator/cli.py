"""Command-line interface for the GPW News Aggregator."""

import argparse
import logging
import sys

from gpw_news_aggregator.aggregator import NewsAggregator
from gpw_news_aggregator.sources import DEFAULT_SOURCES


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="gpw-news",
        description="GPW News Aggregator – fetch the latest Polish stock market news.",
    )
    parser.add_argument(
        "-n",
        "--count",
        type=int,
        default=20,
        metavar="N",
        help="number of articles to display (default: 20)",
    )
    parser.add_argument(
        "-s",
        "--search",
        metavar="QUERY",
        help="filter articles by keyword",
    )
    parser.add_argument(
        "--list-sources",
        action="store_true",
        help="list configured news sources and exit",
    )
    parser.add_argument(
        "-v",
        "--verbose",
        action="store_true",
        help="enable verbose logging",
    )
    return parser


def main(argv=None) -> int:
    parser = build_parser()
    args = parser.parse_args(argv)

    logging.basicConfig(
        level=logging.DEBUG if args.verbose else logging.WARNING,
        format="%(levelname)s %(name)s: %(message)s",
    )

    if args.list_sources:
        print("Configured news sources:")
        for src in DEFAULT_SOURCES:
            print(f"  • {src}")
        return 0

    aggregator = NewsAggregator()

    if args.search:
        articles = aggregator.search(args.search)
        print(f"Search results for '{args.search}':")
    else:
        articles = aggregator.fetch_all()

    articles = articles[: args.count]

    if not articles:
        print("No articles found.")
        return 0

    for article in articles:
        print(article)
        if article.summary:
            # Print first 120 chars of summary
            summary = article.summary[:120]
            if len(article.summary) > 120:
                summary += "…"
            print(f"   {summary}")
        print()

    return 0


if __name__ == "__main__":
    sys.exit(main())
