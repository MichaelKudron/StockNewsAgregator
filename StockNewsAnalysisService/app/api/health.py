from fastapi import APIRouter, Request, Response, status


router = APIRouter(prefix="/health", tags=["health"])


@router.get("/live")
def live() -> dict[str, str]:
    return {"status": "UP"}


@router.get("/ready")
def ready(request: Request, response: Response) -> dict[str, str]:
    services_ready = hasattr(request.app.state, "matching_service") and hasattr(
        request.app.state, "sentiment_service"
    )
    if not services_ready:
        response.status_code = status.HTTP_503_SERVICE_UNAVAILABLE
        return {"status": "DOWN"}
    return {"status": "UP"}

