package StockNewsAgregator.ArticleService.service.articleDownload;

import StockNewsAgregator.ArticleService.dto.ArticleDto;
import StockNewsAgregator.ArticleService.dto.FetchResponseDto;
import StockNewsAgregator.ArticleService.entity.Article;
import StockNewsAgregator.ArticleService.mapper.ArticleMapper;
import StockNewsAgregator.ArticleService.repository.ArticleRepository;
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
