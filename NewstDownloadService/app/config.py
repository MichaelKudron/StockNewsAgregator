from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    http_timeout_seconds: int = 20
    http_user_agent: str = (
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
    )
    host_rate_limit_delay_ms: int = 500
    max_concurrent_fetches: int = 4
    paywall_min_content_length: int = 300
    log_level: str = "INFO"

    class Config:
        env_file = ".env"


settings = Settings()
