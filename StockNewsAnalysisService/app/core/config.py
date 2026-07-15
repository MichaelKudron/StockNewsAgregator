from functools import lru_cache
from typing import Literal

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    app_name: str = "Stock News Analysis Service"
    app_env: str = "development"
    log_level: str = "INFO"

    model_backend: Literal["transformers", "heuristic"] = "transformers"
    matching_model_name: str = "MoritzLaurer/mDeBERTa-v3-base-mnli-xnli"
    sentiment_model_name: str = "MoritzLaurer/mDeBERTa-v3-base-mnli-xnli"
    model_device: int = -1

    matching_acceptance_threshold: float = Field(default=0.75, ge=0.0, le=1.0)
    max_context_chars: int = Field(default=8_000, ge=1_000, le=50_000)


@lru_cache
def get_settings() -> Settings:
    return Settings()

