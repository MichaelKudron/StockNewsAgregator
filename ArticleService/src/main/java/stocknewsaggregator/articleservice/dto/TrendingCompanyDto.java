package stocknewsaggregator.articleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendingCompanyDto {
    private String name;
    private String ticker;
    private String isin;
    private long articleCount;   // JSON: article_count
}
