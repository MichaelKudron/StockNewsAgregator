package StockNewsAgregator.ArticleService.service.articleDownload;

import StockNewsAgregator.ArticleService.dto.FetchResponseDto;

public interface ArticleDownloadService {
    FetchResponseDto fetchArticles(int hours);
}
