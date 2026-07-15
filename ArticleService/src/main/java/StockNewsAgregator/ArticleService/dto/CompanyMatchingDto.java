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
public class CompanyMatchingDto {
    private UUID articleId;
    private String title;
    private String summary;
    private String content;
    @Builder.Default
    private List<CompanyCandidateDto> candidates = new ArrayList<>();
}
