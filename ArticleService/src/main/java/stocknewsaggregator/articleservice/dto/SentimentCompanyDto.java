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
public class SentimentCompanyDto {
    private UUID companyId;
    private String name;
    private String ticker;
    @Builder.Default
    private List<String> evidence = new ArrayList<>();
}