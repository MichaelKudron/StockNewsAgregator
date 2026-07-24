package stocknewsaggregator.articleservice.dto;

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
public class CompanySentimentRequestDto {
    private UUID articleId;
    private String title;
    private String summary;
    private String content;
    @Builder.Default
    private List<SentimentCompanyDto> companies = new ArrayList<>();
}