package StockNewsAgregator.ArticleService.entity;

import StockNewsAgregator.ArticleService.entity.enums.ContentSource;
import StockNewsAgregator.ArticleService.entity.enums.ProcessingStatus;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 2048)
    private String url;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(length = 4000)
    private String summary;


    @Column(columnDefinition = "TEXT")
    private String content;

    private int contentLength;

    private String author;

    private String category;

    private String sourceCode;

    @Enumerated(EnumType.STRING)
    private ContentSource contentSource;

    private LocalDateTime publishedAt;

    private LocalDateTime fetchedAt;

    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus;
}
