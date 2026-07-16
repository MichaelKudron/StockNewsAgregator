package stocknewsaggregator.articleservice.dto;

import stocknewsaggregator.articleservice.entity.enums.MatchLevel;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CompanyArticleDto {
    private String ArticleTitle;
    private UUID articleId;
    private String references;
    private MatchLevel matchLevel;
}
