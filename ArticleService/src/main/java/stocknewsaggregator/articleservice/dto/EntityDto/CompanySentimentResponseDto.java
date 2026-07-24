package stocknewsaggregator.articleservice.dto.EntityDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stocknewsaggregator.articleservice.dto.SentimentResultDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanySentimentResponseDto {
    private UUID articleId;
    private List<SentimentResultDto> results = new ArrayList<>();
}
