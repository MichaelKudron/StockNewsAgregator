package stocknewsaggregator.articleservice.service.download;

import stocknewsaggregator.articleservice.dto.EntityDto.ArticleDto;
import stocknewsaggregator.articleservice.dto.FetchResponseDto;
import stocknewsaggregator.articleservice.entity.Article;
import stocknewsaggregator.articleservice.mapper.ArticleMapper;
import stocknewsaggregator.articleservice.config.ServiceUrls;
import stocknewsaggregator.articleservice.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
@AllArgsConstructor
public class ArticleDownloadServiceImpl implements ArticleDownloadService {
    private final ArticleRepository articleRepository;
    private WebClient webClient;
    private final ServiceUrls serviceUrls;
    @Override
    public FetchResponseDto fetchArticles(int hours) {
        FetchResponseDto fetchResponseDto = webClient.post()
                .uri(serviceUrls.getNewsdownload() + "/fetch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("max_age_hours", hours))
                .retrieve()
                .bodyToMono(FetchResponseDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(WebClientRequestException.class::isInstance))
                .block();
        for(ArticleDto articleDto : fetchResponseDto.getArticles())
        {
            if(articleRepository.existsByUrl(articleDto.getUrl())){
                continue;
            }
            Article article = ArticleMapper.toEntity(articleDto);
            article.setFetchedAt(fetchResponseDto.getFetchedAt());
            articleRepository.save(article);
        }
        return fetchResponseDto;
    }
}
