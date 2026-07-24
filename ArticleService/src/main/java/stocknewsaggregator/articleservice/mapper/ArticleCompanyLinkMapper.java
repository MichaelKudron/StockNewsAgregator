package stocknewsaggregator.articleservice.mapper;

import stocknewsaggregator.articleservice.dto.EntityDto.ArticleCompanyLinkDto;
import stocknewsaggregator.articleservice.entity.ArticleCompanyLink;

public class ArticleCompanyLinkMapper {
    public static ArticleCompanyLink mapToEntity(ArticleCompanyLinkDto dto) {
       ArticleCompanyLink articleCompanyLink = new ArticleCompanyLink();
       articleCompanyLink.setArticleId(dto.getArticleId());
       articleCompanyLink.setCompanyId(dto.getCompanyId());
       articleCompanyLink.setMatchScore(dto.getMatchScore());
       articleCompanyLink.setMatchType(dto.getMatchType());
       articleCompanyLink.setMatchLevel(dto.getMatchLevel());
       articleCompanyLink.setMatchedPhrase(dto.getMatchedPhrase());
       return articleCompanyLink;
    }
    public static ArticleCompanyLinkDto mapToDto(ArticleCompanyLink entity) {
        ArticleCompanyLinkDto dto = new ArticleCompanyLinkDto();
        dto.setArticleId(entity.getArticleId());
        dto.setCompanyId(entity.getCompanyId());
        dto.setMatchScore(entity.getMatchScore());
        dto.setMatchType(entity.getMatchType());
        dto.setMatchLevel(entity.getMatchLevel());
        dto.setMatchedPhrase(entity.getMatchedPhrase());
        return dto;
    }
}
