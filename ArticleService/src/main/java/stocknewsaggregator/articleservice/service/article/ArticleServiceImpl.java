package stocknewsaggregator.articleservice.service.article;


import stocknewsaggregator.articleservice.dto.ArticleDto;
import stocknewsaggregator.articleservice.entity.Article;
import stocknewsaggregator.articleservice.entity.ArticleCompanyLink;
import stocknewsaggregator.articleservice.mapper.ArticleMapper;
import stocknewsaggregator.articleservice.repository.ArticleCompanyLinkRepository;
import stocknewsaggregator.articleservice.repository.ArticleRepository;
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
