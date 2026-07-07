package StockNewsAgregator.ArticleService.service.Article;

import StockNewsAgregator.ArticleService.dto.ArticleDto;
import StockNewsAgregator.ArticleService.entity.Article;

import java.util.List;
import java.util.UUID;

public interface ArticleService {
    List<ArticleDto> GetArticlesByCompanyId(UUID companyId);
}
