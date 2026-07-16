package stocknewsaggregator.articleservice.service.download;

import stocknewsaggregator.articleservice.dto.ArticleDto;
import stocknewsaggregator.articleservice.dto.FetchResponseDto;
import stocknewsaggregator.articleservice.entity.Article;
import stocknewsaggregator.articleservice.mapper.ArticleMapper;
import stocknewsaggregator.articleservice.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@AllArgsConstructor
public class ArticleDownloadServiceImpl implements ArticleDownloadService {
    private final ArticleRepository articleRepository;
    private WebClient webClient;
    @Override
    public FetchResponseDto fetchArticles(int hours) {
        FetchResponseDto fetchResponseDto = webClient.post()
                .uri("http://127.0.0.1:8003/fetch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("max_age_hours", hours))
                .retrieve()
                .bodyToMono(FetchResponseDto.class)
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
