from app.core.config import Settings
from app.models.engine import AnalysisEngine
from app.models.heuristic_engine import HeuristicEngine
from app.models.transformers_engine import TransformersEngine


def create_engines(settings: Settings) -> tuple[AnalysisEngine, AnalysisEngine]:
    if settings.model_backend == "heuristic":
        engine = HeuristicEngine()
        return engine, engine

    matching = TransformersEngine(settings.matching_model_name, settings.model_device)
    if settings.sentiment_model_name == settings.matching_model_name:
        return matching, matching

    sentiment = TransformersEngine(settings.sentiment_model_name, settings.model_device)
    return matching, sentiment

