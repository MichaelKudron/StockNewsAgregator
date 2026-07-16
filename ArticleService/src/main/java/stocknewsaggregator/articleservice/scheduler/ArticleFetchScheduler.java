package stocknewsaggregator.articleservice.scheduler;

import stocknewsaggregator.articleservice.service.download.ArticleDownloadService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleFetchScheduler {
    ArticleDownloadService articleDownloadService;
    @Scheduled(fixedRate = 1000 * 60 * 60, initialDelay = 0)
    public void fetchArticles() {
        articleDownloadService.fetchArticles(1);
        System.out.println("Articles fetched");

    }
}
