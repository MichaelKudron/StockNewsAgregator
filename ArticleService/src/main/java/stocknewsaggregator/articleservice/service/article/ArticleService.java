package stocknewsaggregator.articleservice.service.article;

import stocknewsaggregator.articleservice.dto.ArticleDto;
import stocknewsaggregator.articleservice.entity.Article;

import java.util.List;
import java.util.UUID;

public interface ArticleService {
    List<ArticleDto> GetArticlesByCompanyId(UUID companyId);
}
