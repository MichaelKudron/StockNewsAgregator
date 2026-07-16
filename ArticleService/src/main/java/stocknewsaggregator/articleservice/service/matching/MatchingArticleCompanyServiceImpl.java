package stocknewsaggregator.articleservice.service.matching;

import stocknewsaggregator.articleservice.dto.*;
import stocknewsaggregator.articleservice.entity.Article;
import stocknewsaggregator.articleservice.entity.enums.MatchLevel;
import stocknewsaggregator.articleservice.entity.enums.MatchType;
import stocknewsaggregator.articleservice.entity.enums.ProcessingStatus;
import stocknewsaggregator.articleservice.mapper.ArticleCompanyLinkMapper;
import stocknewsaggregator.articleservice.repository.ArticleCompanyLinkRepository;
import stocknewsaggregator.articleservice.repository.ArticleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class MatchingArticleCompanyServiceImpl implements MatchingArticleCompanyService{
    private final ArticleRepository articleRepository;
    private final WebClient webClient;
    private final ArticleCompanyLinkRepository articleCompanyLinkRepository;
    @Override
    @Transactional
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
            List<CompanyCandidateDto> cadidates = new LinkedList<>();
            for (MatchingCompanyDto company : matchingCompanyDtos) {
                ArticleCompanyLinkDto articleCompanyLinkDto = MatchArticleToCompanies(company, article);
                if (articleCompanyLinkDto != null) {
                    matchedCandidates.add(articleCompanyLinkDto);
                    CompanyCandidateDto companyCandidateDto = new CompanyCandidateDto();
                    companyCandidateDto.setCompanyId(company.getId());
                    companyCandidateDto.setName(company.getName());
                    companyCandidateDto.setShortName(company.getShortName());
                    companyCandidateDto.setTicker(company.getTicker());
                    companyCandidateDto.setIsin(company.getIsin());
                    companyCandidateDto.setAliases(company.getAliases());
                    companyCandidateDto.setMatchedPhrase(articleCompanyLinkDto.getMatchedPhrase());
                    companyCandidateDto.setRuleScore(articleCompanyLinkDto.getMatchScore());
                    cadidates.add(companyCandidateDto);
                }
            }
            if (matchedCandidates.isEmpty()){
                article.setProcessingStatus(ProcessingStatus.UNMATCHED);
                continue;
            }
            CompanyMatchingDto companyMatchingDto = new CompanyMatchingDto();
            companyMatchingDto.setArticleId(article.getId());
            companyMatchingDto.setTitle(article.getTitle());
            companyMatchingDto.setSummary(article.getSummary());
            companyMatchingDto.setContent(article.getContent());
            companyMatchingDto.setCandidates(cadidates);
            CompanyMatchingResponseDto matchingResponseDto = webClient.post()
                    .uri("http://localhost:8004/api/v1/company-matching")
                    .bodyValue(companyMatchingDto)
                    .retrieve()
                    .bodyToMono(CompanyMatchingResponseDto.class)
                    .block();
            if(matchingResponseDto.getResults().stream().count() == 0){
                article.setProcessingStatus(ProcessingStatus.UNMATCHED);
                continue;
            }
            for (MatchingResultDto resultDto : matchingResponseDto.getResults()) {
                MatchLevel level = MatchLevel.valueOf(resultDto.getMatchLevel());
                boolean acceptedTopic = level == MatchLevel.TOPIC && resultDto.getConfidence() >= 0.75;
                boolean mention = level == MatchLevel.MENTION;
                if (!acceptedTopic && !mention) continue;

                matchedCandidates.stream()
                        .filter(m -> m.getCompanyId().equals(resultDto.getCompanyId()))
                        .findFirst()
                        .ifPresent(link -> {
                            link.setMatchLevel(level);
                            articleCompanyLinkRepository.save(ArticleCompanyLinkMapper.mapToEntity(link));
                            article.setProcessingStatus(ProcessingStatus.MATCHED);

                        });
            }
            if (article.getProcessingStatus() != ProcessingStatus.MATCHED) article.setProcessingStatus(ProcessingStatus.UNMATCHED);
           articleRepository.save(article);
        }




    }
    ArticleCompanyLinkDto MatchArticleToCompanies(MatchingCompanyDto company, Article article) {
        String rawText = safe(article.getTitle())
                +safe(article.getSummary())
                +safe(article.getContent());
        String articleFull = normalize(rawText);
        if(containsTicker(rawText,company.getTicker())){
            ArticleCompanyLinkDto articleCompanyLinkDto = new ArticleCompanyLinkDto();
            articleCompanyLinkDto.setArticleId(article.getId());
            articleCompanyLinkDto.setCompanyId(company.getId());
            articleCompanyLinkDto.setMatchScore(100);
            articleCompanyLinkDto.setMatchedPhrase(company.getTicker());
            articleCompanyLinkDto.setMatchType(MatchType.TICKER);
            return articleCompanyLinkDto;
        }
        if(containsProperPhrase(articleFull,company.getName())){
            ArticleCompanyLinkDto articleCompanyLinkDto = new ArticleCompanyLinkDto();
            articleCompanyLinkDto.setArticleId(article.getId());
            articleCompanyLinkDto.setCompanyId(company.getId());
            articleCompanyLinkDto.setMatchScore(100);
            articleCompanyLinkDto.setMatchedPhrase(company.getName());
            articleCompanyLinkDto.setMatchType(MatchType.NAME);
            return articleCompanyLinkDto;
        }
        if (containsPhrase(articleFull, company.getShortName())) {
            ArticleCompanyLinkDto articleCompanyLinkDto = new ArticleCompanyLinkDto();
            articleCompanyLinkDto.setArticleId(article.getId());
            articleCompanyLinkDto.setCompanyId(company.getId());
            articleCompanyLinkDto.setMatchScore(50);
            articleCompanyLinkDto.setMatchedPhrase(company.getShortName());
            articleCompanyLinkDto.setMatchType(MatchType.SHORTNAME);
            return articleCompanyLinkDto;
        }
        for(String alias : company.getAliases()) {
            if (containsPhrase(articleFull,normalize(alias))){
                ArticleCompanyLinkDto articleCompanyLinkDto = new ArticleCompanyLinkDto();
                articleCompanyLinkDto.setArticleId(article.getId());
                articleCompanyLinkDto.setCompanyId(company.getId());
                articleCompanyLinkDto.setMatchScore(10);
                articleCompanyLinkDto.setMatchedPhrase(alias);
                articleCompanyLinkDto.setMatchType(MatchType.ALIAS);
                return articleCompanyLinkDto;
            }
        }

        return null;
        }


    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase();
    }
    private String safe(String value){
        return value == null ? "" : value;
    }

    private boolean containsPhrase(String text, String phrase) {
        if (phrase == null || phrase.isBlank() || phrase.trim().length() < 4) {
            return false;
        }
        return Pattern.compile("(?<!\\p{L})" + Pattern.quote(phrase.toLowerCase().trim()) + "(?!\\p{L})")
                .matcher(text).find();
    }

    private boolean containsTicker(String text, String ticker) {
        if (ticker == null || ticker.isBlank()) {
            return false;
        }
        return Pattern.compile("(?<!\\p{L})" + Pattern.quote(ticker) + "(?!\\p{L})")
                .matcher(text).find();
    }
    private boolean containsProperPhrase(String rawText, String phrase) {
        if (phrase == null || phrase.isBlank() || phrase.trim().length() < 4) {
            return false;
        }
        String trimmed = phrase.trim();
        String first = trimmed.substring(0, 1).toUpperCase();
        String rest = trimmed.substring(1);
        return Pattern.compile(
                        "(?<!\\p{L})" + Pattern.quote(first) + "(?iu:" + Pattern.quote(rest) + ")(?!\\p{L})")
                .matcher(rawText).find();
    }
}
