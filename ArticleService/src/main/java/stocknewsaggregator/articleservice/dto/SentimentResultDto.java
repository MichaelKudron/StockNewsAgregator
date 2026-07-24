package stocknewsaggregator.articleservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stocknewsaggregator.articleservice.entity.enums.Sentiment;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SentimentResultDto {
    private UUID companyId;
    private Sentiment sentiment;
    private double confidence;
}