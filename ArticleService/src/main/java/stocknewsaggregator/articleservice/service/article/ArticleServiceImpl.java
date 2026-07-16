package stocknewsaggregator.articleservice.service.article;


import stocknewsaggregator.articleservice.dto.ArticleDto;
import stocknewsaggregator.articleservice.dto.CompanyArticleDto;
import stocknewsaggregator.articleservice.entity.Article;
import stocknewsaggregator.articleservice.entity.ArticleCompanyLink;
import stocknewsaggregator.articleservice.mapper.ArticleMapper;
import stocknewsaggregator.articleservice.repository.ArticleCompanyLinkRepository;
import stocknewsaggregator.articleservice.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleCompanyLinkRepository articleCompanyLinkRepository;
    @Override
    public List<CompanyArticleDto> GetArticlesByCompanyId(UUID companyId) {
        List<ArticleCompanyLink> articleCompanyLinks = articleCompanyLinkRepository.findByCompanyId(companyId);
        List<CompanyArticleDto> companyArticleDtos = new ArrayList<>();
        for (ArticleCompanyLink articleCompanyLink : articleCompanyLinks) {
            Article article = articleRepository.findById(articleCompanyLink.getArticleId()).get();
            CompanyArticleDto companyArticleDto = new CompanyArticleDto();
            companyArticleDto.setArticleId(article.getId());
            companyArticleDto.setArticleTitle(article.getTitle());
            companyArticleDto.setSentiment("neutral");
            companyArticleDto.setMatchLevel(articleCompanyLink.getMatchLevel());
            companyArticleDtos.add(companyArticleDto);
        }

    return companyArticleDtos;
    }

    @Override
    public ArticleDto GetArticleById(UUID id) {
        return ArticleMapper.toDto(articleRepository.findById(id).get());
    }
}
