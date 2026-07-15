package StockNewsAgregator.ArticleService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCandidateDto {
    private UUID companyId;
    private String name;
    private String shortName;
    private String ticker;
    private String isin;
    @Builder.Default
    private List<String> aliases = new ArrayList<>();
    private String matchedPhrase;
    private Double ruleScore;
}