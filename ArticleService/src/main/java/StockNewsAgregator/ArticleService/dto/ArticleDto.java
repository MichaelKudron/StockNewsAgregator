package StockNewsAgregator.ArticleService.dto;

import StockNewsAgregator.ArticleService.entity.enums.ContentSource;
import StockNewsAgregator.ArticleService.entity.enums.ProcessingStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleDto {
        private UUID id;

        @JsonProperty("source_code")
        private String sourceCode;

        @JsonProperty("source_type")
        private String sourceType;

        private String url;
        private String title;
        private String summary;
        private String content;

        @JsonProperty("content_length")
        private int contentLength;

        private String author;
        private String category;

        @JsonProperty("content_source")
        private ContentSource contentSource;

        @JsonProperty("published_at")
        private LocalDateTime publishedAt;

        @JsonProperty("fetched_at")
        private LocalDateTime fetchedAt;

        @JsonProperty("processing_status")
        private ProcessingStatus processingStatus;

}
