package stocknewsaggregator.articleservice.controller;

import stocknewsaggregator.articleservice.dto.ArticleDto;
import stocknewsaggregator.articleservice.dto.FetchResponseDto;
import stocknewsaggregator.articleservice.service.article.ArticleService;
import stocknewsaggregator.articleservice.service.matching.MatchingArticleCompanyService;
import stocknewsaggregator.articleservice.service.download.ArticleDownloadService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/article")
@AllArgsConstructor
public class ArticleController {
    private ArticleDownloadService articleDownloadService;
    private MatchingArticleCompanyService matchingArticleCompanyService;
    private ArticleService articleService;
    @GetMapping("{hours}")
    public ResponseEntity<FetchResponseDto> fetchArticles(@PathVariable int hours) {
        FetchResponseDto fetchResponseDto = articleDownloadService.fetchArticles(hours);
        return ResponseEntity.ok(fetchResponseDto);
    }
    @GetMapping("/match")
    public ResponseEntity matchArticles() {
        matchingArticleCompanyService.MatchArticleCompany();
        return ResponseEntity.ok().build();
    }
    @GetMapping("/company/{id}")
    public ResponseEntity<List<ArticleDto>> GetArticlesByCompany(@PathVariable UUID id) {
        return ResponseEntity.ok(articleService.GetArticlesByCompanyId(id));
    }
}
