import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.api import health, matching, sentiment
from app.core.config import Settings, get_settings
from app.core.logging import configure_logging
from app.models.factory import create_engines
from app.services.matching_service import MatchingService
from app.services.sentiment_service import SentimentService


logger = logging.getLogger(__name__)


def create_app(settings: Settings | None = None) -> FastAPI:
    app_settings = settings or get_settings()
    configure_logging(app_settings.log_level)

    @asynccontextmanager
    async def lifespan(app: FastAPI):
        logger.info("Loading analysis models using backend=%s", app_settings.model_backend)
        matching_engine, sentiment_engine = create_engines(app_settings)
        app.state.matching_service = MatchingService(
            matching_engine,
            app_settings.max_context_chars,
        )
        app.state.sentiment_service = SentimentService(
            sentiment_engine,
            app_settings.max_context_chars,
        )
        logger.info("Analysis models are ready")
        yield

    application = FastAPI(
        title=app_settings.app_name,
        version="0.1.0",
        lifespan=lifespan,
    )
    application.include_router(health.router)
    application.include_router(matching.router)
    application.include_router(sentiment.router)
    return application


app = create_app()

