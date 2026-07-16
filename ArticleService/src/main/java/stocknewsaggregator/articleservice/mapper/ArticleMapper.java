package stocknewsaggregator.articleservice.mapper;

import stocknewsaggregator.articleservice.dto.ArticleDto;
import stocknewsaggregator.articleservice.entity.Article;
import stocknewsaggregator.articleservice.entity.enums.ProcessingStatus;

public class ArticleMapper {

    public static ArticleDto toDto(Article article) {
        if (article == null) {
            return null;
        }

        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setSourceCode(article.getSourceCode());
        dto.setUrl(article.getUrl());
        dto.setTitle(article.getTitle());
        dto.setSummary(article.getSummary());
        dto.setContent(article.getContent());
        dto.setContentLength(article.getContentLength());
        dto.setAuthor(article.getAuthor());
        dto.setCategory(article.getCategory());
        dto.setContentSource(article.getContentSource());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setFetchedAt(article.getFetchedAt());
        dto.setProcessingStatus(article.getProcessingStatus());

        return dto;
    }

    public static Article toEntity(ArticleDto dto) {
        if (dto == null) {
            return null;
        }

        Article article = new Article();
        article.setId(dto.getId());
        article.setSourceCode(dto.getSourceCode());
        article.setUrl(dto.getUrl());
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setContentLength(dto.getContentLength());
        article.setAuthor(dto.getAuthor());
        article.setCategory(dto.getCategory());
        article.setContentSource(dto.getContentSource());
        article.setPublishedAt(dto.getPublishedAt());
        article.setFetchedAt(dto.getFetchedAt());

        if (dto.getProcessingStatus() != null) {
            article.setProcessingStatus(dto.getProcessingStatus());
        } else {
            article.setProcessingStatus(ProcessingStatus.FETCHED);
        }

        return article;
    }
}