package StockNewsAgregator.ArticleService.dto;

import StockNewsAgregator.ArticleService.entity.enums.MatchLevel;
import StockNewsAgregator.ArticleService.entity.enums.MatchType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ArticleCompanyLinkDto {
    private UUID companyId;
    private UUID articleId;
    private double matchScore;
    private MatchType matchType;
    private MatchLevel matchLevel;
    private String matchedPhrase;
}
