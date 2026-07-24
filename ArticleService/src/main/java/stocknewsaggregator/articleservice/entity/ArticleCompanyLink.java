package stocknewsaggregator.articleservice.entity;

import stocknewsaggregator.articleservice.entity.enums.MatchLevel;
import stocknewsaggregator.articleservice.entity.enums.MatchType;
import stocknewsaggregator.articleservice.entity.enums.Sentiment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ArticleCompanyLink {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;
    private UUID companyId;
    private UUID articleId;
    private double matchScore;
    @Enumerated(EnumType.STRING)
    private MatchLevel matchLevel;
    @Enumerated(EnumType.STRING)
    private MatchType matchType;
    private String matchedPhrase;
    @Enumerated(EnumType.STRING)
    private Sentiment sentiment;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
