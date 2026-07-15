from typing import Annotated

from fastapi import APIRouter, Depends

from app.api.dependencies import get_sentiment_service
from app.schemas.sentiment import CompanySentimentRequest, CompanySentimentResponse
from app.services.sentiment_service import SentimentService


router = APIRouter(prefix="/api/v1", tags=["company sentiment"])


@router.post("/company-sentiment", response_model=CompanySentimentResponse)
def analyze_company_sentiment(
    request: CompanySentimentRequest,
    service: Annotated[SentimentService, Depends(get_sentiment_service)],
) -> CompanySentimentResponse:
    return service.analyze(request)

