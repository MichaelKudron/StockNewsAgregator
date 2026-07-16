package stocknewsaggregator.articleservice.service.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import stocknewsaggregator.articleservice.dto.ArticleDto;
import stocknewsaggregator.articleservice.dto.CompanyArticleDto;
import stocknewsaggregator.articleservice.entity.Article;

import java.util.List;
import java.util.UUID;

public interface ArticleService {
    List<CompanyArticleDto> GetArticlesByCompanyId(UUID companyId);


    ArticleDto GetArticleById(UUID id);
}
