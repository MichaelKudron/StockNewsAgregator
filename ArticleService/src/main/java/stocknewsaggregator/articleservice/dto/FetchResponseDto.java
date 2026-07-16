package stocknewsaggregator.articleservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetchResponseDto {
    @JsonProperty("fetched_at")
    private LocalDateTime fetchedAt;

    private List<ArticleDto> articles;

    private List<FetchErrorDto> errors;
}
