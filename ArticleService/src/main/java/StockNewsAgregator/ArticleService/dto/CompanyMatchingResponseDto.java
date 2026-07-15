package StockNewsAgregator.ArticleService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMatchingResponseDto {
    private UUID articleId;
    private List<MatchingResultDto> results = new ArrayList<>();
    private String modelVersion;
}