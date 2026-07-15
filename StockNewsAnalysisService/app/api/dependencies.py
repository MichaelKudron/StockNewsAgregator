from fastapi import Request

from app.services.matching_service import MatchingService
from app.services.sentiment_service import SentimentService


def get_matching_service(request: Request) -> MatchingService:
    return request.app.state.matching_service


def get_sentiment_service(request: Request) -> SentimentService:
    return request.app.state.sentiment_service

