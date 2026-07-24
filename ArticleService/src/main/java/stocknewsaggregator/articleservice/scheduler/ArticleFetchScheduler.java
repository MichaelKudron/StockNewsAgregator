package stocknewsaggregator.articleservice.scheduler;

import stocknewsaggregator.articleservice.service.download.ArticleDownloadService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import stocknewsaggregator.articleservice.service.matching.MatchingArticleCompanyService;
import stocknewsaggregator.articleservice.service.Analysis.ArticleAnaliseService;

@Service
@AllArgsConstructor
public class ArticleFetchScheduler {
    ArticleDownloadService articleDownloadService;
    MatchingArticleCompanyService matchingArticleCompanyService;
    ArticleAnaliseService articleAnaliseService;
    @Scheduled(fixedRate = 1000 * 60 * 60, initialDelay = 0)
    public void fetchArticles() {
        articleDownloadService.fetchArticles(1);
        System.out.println("Articles fetched");
        matchingArticleCompanyService.MatchArticleCompany();
        System.out.println("Companies matched");
        articleAnaliseService.AnalyzeArticle();
        System.out.println("Sentiment analyzed");
    }
}
