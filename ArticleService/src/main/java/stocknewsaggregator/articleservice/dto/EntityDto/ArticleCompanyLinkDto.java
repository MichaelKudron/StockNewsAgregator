package stocknewsaggregator.articleservice.dto.EntityDto;

import stocknewsaggregator.articleservice.entity.enums.MatchLevel;
import stocknewsaggregator.articleservice.entity.enums.MatchType;
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
