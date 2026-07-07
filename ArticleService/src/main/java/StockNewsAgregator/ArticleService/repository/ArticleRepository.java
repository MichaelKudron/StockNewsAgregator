package StockNewsAgregator.ArticleService.repository;

import StockNewsAgregator.ArticleService.entity.Article;
import StockNewsAgregator.ArticleService.entity.enums.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    boolean existsByUrl(String url);
    List<Article> findByProcessingStatus(ProcessingStatus processingStatus);
}
