package stocknewsaggregator.articleservice.service.Analysis;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.util.retry.Retry;
import stocknewsaggregator.articleservice.config.ServiceUrls;
import stocknewsaggregator.articleservice.dto.*;
import stocknewsaggregator.articleservice.dto.EntityDto.CompanySentimentResponseDto;
import stocknewsaggregator.articleservice.entity.Article;
import stocknewsaggregator.articleservice.entity.ArticleCompanyLink;
import stocknewsaggregator.articleservice.entity.enums.ProcessingStatus;
import stocknewsaggregator.articleservice.entity.enums.Sentiment;
import stocknewsaggregator.articleservice.repository.ArticleCompanyLinkRepository;
import stocknewsaggregator.articleservice.repository.ArticleRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor

public class AnalyzeArticleServiceImpl implements ArticleAnaliseService{
    private final ArticleRepository articleRepository;
    private final ArticleCompanyLinkRepository articleCompanyLinkRepository;
    private final WebClient webClient;
    private final ServiceUrls serviceUrls;
    @Override
    public void AnalyzeArticle() {
    List<Article> matchedArticles = articleRepository.findByProcessingStatus(ProcessingStatus.MATCHED);
    for (Article article : matchedArticles) {
        List<ArticleCompanyLink> links = articleCompanyLinkRepository.findByArticleId(article.getId());
        if (links.isEmpty()) continue;

        List<SentimentCompanyDto> companies = new ArrayList<>();
        for (ArticleCompanyLink link : links) {
            MatchingCompanyDto company = webClient.get()
                    .uri(serviceUrls.getCompany() + "/api/v1/company/matching/" + link.getCompanyId())
                    .retrieve()
                    .bodyToMono(MatchingCompanyDto.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .filter(WebClientRequestException.class::isInstance))
                    .block();

            SentimentCompanyDto companyDto = new SentimentCompanyDto();
            companyDto.setCompanyId(company.getId());
            companyDto.setName(company.getName());
            companyDto.setTicker(company.getTicker());
            String phrase = link.getMatchedPhrase() == null ? company.getName() : link.getMatchedPhrase();
            companyDto.setEvidence(getTextToAnalyze(article, phrase));
            companies.add(companyDto);
        }

        CompanySentimentRequestDto request = new CompanySentimentRequestDto();
        request.setArticleId(article.getId());
        request.setTitle(article.getTitle());
        request.setSummary(article.getSummary());
        request.setContent(article.getContent());
        request.setCompanies(companies);

        CompanySentimentResponseDto response = webClient.post()
                .uri(serviceUrls.getAnalysis() + "/api/v1/company-sentiment")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CompanySentimentResponseDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(WebClientRequestException.class::isInstance))
                .block();

        Map<UUID, Sentiment> sentimentByCompany = response.getResults().stream()
                .collect(Collectors.toMap(SentimentResultDto::getCompanyId, SentimentResultDto::getSentiment));

        for (ArticleCompanyLink link : links) {
            Sentiment sentiment = sentimentByCompany.get(link.getCompanyId());
            if (sentiment != null) link.setSentiment(sentiment);
        }
        articleCompanyLinkRepository.saveAll(links);

        article.setProcessingStatus(ProcessingStatus.PROCESSED);
        articleRepository.save(article);
    }

    }


    private List<String> cutToPhrases(String content){
        return new ArrayList<String>(Arrays.asList(content.split("(?<=[.!?])\\s+")));
    }
    private int findPhrase(List<String> phrases, String phrase){
        if(phrases.isEmpty()) return -1;
        int highestLength = 0;
        int highestIndex = 0;
        for(String p : phrases){
            if(p.toLowerCase().contains(phrase.toLowerCase()) && p.length() > highestLength) {
                highestIndex = phrases.indexOf(p);
                highestLength = p.length();

            }



        }
        if(highestLength == 0){
            return -1;
        }
        else return highestIndex;
    }
    private List<String> getTextToAnalyze(Article article ,String phrase){
        String textToAnalyze = article.getTitle()+' '+article.getSummary();
        List<String> phrases = new ArrayList<>();
        if(article.getContent() == null || article.getContent().isBlank()) {
            return phrases;
        }
        List<String> cutToPhrasest = cutToPhrases(article.getContent());

        while (textToAnalyze.length() < 1000){
            int index = findPhrase(cutToPhrasest, phrase);
            if(index == -1) break;
            textToAnalyze += ' ' + cutToPhrasest.get(index);
            phrases.add(cutToPhrasest.get(index));
            cutToPhrasest.remove(index);
        }
        return phrases;

    }

}
