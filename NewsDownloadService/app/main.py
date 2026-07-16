import json
from contextlib import asynccontextmanager
from typing import Any

import structlog
from fastapi import Body, FastAPI
from starlette.responses import JSONResponse

from app.logging_setup import setup_logging
from app.models import FetchRequest, FetchResponse, HealthResponse
from app.orchestrator import fetch_orchestrate

log = structlog.get_logger(__name__)

APP_VERSION = "1.0.0"


class AsciiJSONResponse(JSONResponse):
    def render(self, content: Any) -> bytes:
        return json.dumps(
            content,
            ensure_ascii=True,
            allow_nan=False,
            separators=(",", ":"),
        ).encode("utf-8")


@asynccontextmanager
async def lifespan(app: FastAPI):
    setup_logging()
    log.info("startup", version=APP_VERSION)
    yield
    log.info("shutdown")


app = FastAPI(
    title="News Fetcher API",
    version=APP_VERSION,
    lifespan=lifespan,
    default_response_class=AsciiJSONResponse,
)


@app.post("/fetch", response_model=FetchResponse, tags=["fetch"])
async def fetch(request: FetchRequest = Body(default_factory=FetchRequest)):
    return await fetch_orchestrate(request)


@app.get("/health", response_model=HealthResponse, tags=["meta"])
async def health():
    return HealthResponse(status="ok", version=APP_VERSION)
