# GPWNewsAgregator

A Python news aggregator for the **Warsaw Stock Exchange (Giełda Papierów Wartościowych – GPW)** that collects the latest market news from Polish financial RSS feeds.

## Features

- Fetches news articles from multiple Polish financial news sources (Bankier.pl, Money.pl, StockWatch.pl, Parkiet.com)
- Combines and sorts articles by publication date
- Keyword search across titles and summaries
- Simple CLI for quick terminal use

## Installation

```bash
pip install -r requirements.txt
pip install -e .
```

## Usage

```bash
# Show the 20 most recent articles
gpw-news

# Limit to 10 articles
gpw-news -n 10

# Search by keyword
gpw-news --search WIG20

# List configured sources
gpw-news --list-sources

# Enable verbose logging
gpw-news -v
```

## Project structure

```
src/gpw_news_aggregator/
├── __init__.py      – package entry point
├── aggregator.py    – core fetching & aggregation logic
├── cli.py           – command-line interface
├── models.py        – NewsArticle / NewsSource data classes
└── sources.py       – default GPW news sources
tests/
├── test_aggregator.py
├── test_cli.py
└── test_models.py
```

## Running tests

```bash
pip install -r requirements-dev.txt
pytest
```
