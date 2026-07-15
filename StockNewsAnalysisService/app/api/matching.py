from typing import Annotated

from fastapi import APIRouter, Depends

from app.api.dependencies import get_matching_service
from app.schemas.matching import CompanyMatchingRequest, CompanyMatchingResponse
from app.services.matching_service import MatchingService


router = APIRouter(prefix="/api/v1", tags=["company matching"])


@router.post("/company-matching", response_model=CompanyMatchingResponse)
def match_companies(
    request: CompanyMatchingRequest,
    service: Annotated[MatchingService, Depends(get_matching_service)],
) -> CompanyMatchingResponse:
    return service.analyze(request)

