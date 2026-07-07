package StockNewsAgregator.ArticleService.service.MatchingArticleCompany;

import StockNewsAgregator.ArticleService.dto.ArticleCompanyLinkDto;
import StockNewsAgregator.ArticleService.dto.MatchingCompanyDto;
import StockNewsAgregator.ArticleService.entity.Article;
import StockNewsAgregator.ArticleService.entity.enums.MatchType;
import StockNewsAgregator.ArticleService.entity.enums.ProcessingStatus;
import StockNewsAgregator.ArticleService.mapper.ArticleCompanyLinkMapper;
import StockNewsAgregator.ArticleService.repository.ArticleCompanyLinkRepository;
import StockNewsAgregator.ArticleService.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedList;
import java.util.List;
@Service
@AllArgsConstructor
public class MatchingArticleCompanyServiceImpl implements MatchingArticleCompanyService{
    private final ArticleRepository articleRepository;
    private final WebClient webClient;
    private final ArticleCompanyLinkRepository articleCompanyLinkRepository;
    @Override
    public void MatchArticleCompany() {
        List<Article> UnmatchedArticles = articleRepository.findByProcessingStatus(ProcessingStatus.FETCHED);
        if (UnmatchedArticles.isEmpty()) return;
        List<MatchingCompanyDto> matchingCompanyDtos = webClient.get()
                .uri("http://localhost:8081/api/v1/company/matching")
                .retrieve()
                .bodyToFlux(MatchingCompanyDto.class)
                .collectList()
                .block();

        for (Article article : UnmatchedArticles) {
            List<ArticleCompanyLinkDto> matchedCandidates = new LinkedList<>();
            for (MatchingCompanyDto company : matchingCompanyDtos) {
                ArticleCompanyLinkDto articleCompanyLinkDto = MatchArticleToCompanies(company, article);
                if (articleCompanyLinkDto != null) {
                    matchedCandidates.add(articleCompanyLinkDto);
                }else {
                    article.setProcessingStatus(ProcessingStatus.UNMATCHED);
                }

            }
        }




    }
    private ArticleCompanyLinkDto MatchArticleToCompanies(
            MatchingCompanyDto company,
            Article article){
        int score = 0;

        String title = normalize(article.getTitle());
        String summary = normalize(article.getSummary());
        String content = normalize(article.getContent());

        String isin = normalize(company.getIsin());
        String ticker = normalize(company.getTicker());
        String name = normalize(company.getName());
        String shortName = normalize(company.getShortName());
        ArticleCompanyLinkDto articleCompanyLinkDto = new ArticleCompanyLinkDto();
        if (containsPhrase(title, isin) || containsPhrase(summary, isin) || containsPhrase(content, isin)) {
            score += 150;
            articleCompanyLinkDto.setMatchType(MatchType.ISIN);
        }

        if (containsPhrase(title, name)) {
            score += 100;
            if(articleCompanyLinkDto.getMatchType() == null)
                articleCompanyLinkDto.setMatchType(MatchType.NAME);
        }
        if (containsPhrase(summary, name)) {
            score += 60;
            if(articleCompanyLinkDto.getMatchType() == null)
                articleCompanyLinkDto.setMatchType(MatchType.NAME);
        }
        if (containsPhrase(content, name)) {
            score += 30;
            if(articleCompanyLinkDto.getMatchType() == null)
                articleCompanyLinkDto.setMatchType(MatchType.NAME);
        }

        if (containsPhrase(title, shortName)) {
            score += 70;
            if(articleCompanyLinkDto.getMatchType() == null)
                articleCompanyLinkDto.setMatchType(MatchType.SHORTNAME);
        }
        if (containsPhrase(summary, shortName)) {
            score += 45;
            if(articleCompanyLinkDto.getMatchType() == null)
                articleCompanyLinkDto.setMatchType(MatchType.SHORTNAME);
        }
        if (containsPhrase(content, shortName)) {
            score += 20;
            if(articleCompanyLinkDto.getMatchType() == null)
                articleCompanyLinkDto.setMatchType(MatchType.SHORTNAME);
        }

        if (company.getAliases() != null) {
            for (String alias : company.getAliases()) {
                String normalizedAlias = normalize(alias);
                if (normalizedAlias.isBlank()|| normalizedAlias.length() <= 4) {
                    continue;
                }
                if (containsPhrase(title, normalizedAlias)) {
                    score += 80;
                    if(articleCompanyLinkDto.getMatchType() == null)
                        articleCompanyLinkDto.setMatchType(MatchType.ALIAS);
                }
                if (containsPhrase(summary, normalizedAlias)) {
                    score += 50;
                    if(articleCompanyLinkDto.getMatchType() == null)
                        articleCompanyLinkDto.setMatchType(MatchType.ALIAS);
                }
                if (containsPhrase(content, normalizedAlias)) {
                    score += 25;
                    if(articleCompanyLinkDto.getMatchType() == null)
                        articleCompanyLinkDto.setMatchType(MatchType.ALIAS);
                }
            }
        }


            if (score > 60) {
            articleCompanyLinkDto.setArticleId(article.getId());
            articleCompanyLinkDto.setCompanyId(company.getId());
            articleCompanyLinkDto.setMatchScore(score);
            return articleCompanyLinkDto;
            }
        return null;

    }
    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private boolean containsPhrase(String text, String phrase) {
        return phrase != null
                && !phrase.isBlank()
                && text.contains(phrase.toLowerCase());
    }

    private boolean containsTicker(String text, String ticker) {
        if (ticker == null || ticker.isBlank()) {
            return false;
        }

        if (ticker.length() <= 3) {
            return false;
        }

        return containsPhrase(text, ticker);
    }
}
