package StockNewsAgregator.ArticleService.service.Article;


import StockNewsAgregator.ArticleService.dto.ArticleDto;
import StockNewsAgregator.ArticleService.entity.Article;
import StockNewsAgregator.ArticleService.entity.ArticleCompanyLink;
import StockNewsAgregator.ArticleService.mapper.ArticleMapper;
import StockNewsAgregator.ArticleService.repository.ArticleCompanyLinkRepository;
import StockNewsAgregator.ArticleService.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleCompanyLinkRepository articleCompanyLinkRepository;
    @Override
    public List<ArticleDto> GetArticlesByCompanyId(UUID companyId) {
        List<ArticleCompanyLink> articleCompanyLinks = articleCompanyLinkRepository.findByCompanyId(companyId);
        List<Article> articles = articleCompanyLinks.stream().map(a->articleRepository.findById(a.getArticleId()).get()).toList();
        return articles.stream().map(ArticleMapper::toDto).toList();
    }
}
