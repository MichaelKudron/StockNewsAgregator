package StockNewsAgregator.ArticleService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResultDto {
    private UUID companyId;
    private boolean relevant;
    private String matchLevel;
    private double confidence;
    private String evidence;
    private String reason;
}
