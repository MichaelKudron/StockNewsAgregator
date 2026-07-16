package stocknewsaggregator.articleservice.service.download;

import stocknewsaggregator.articleservice.dto.FetchResponseDto;

public interface ArticleDownloadService {
    FetchResponseDto fetchArticles(int hours);
}
