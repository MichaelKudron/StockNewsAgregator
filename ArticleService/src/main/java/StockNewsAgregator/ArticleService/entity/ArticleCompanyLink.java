package StockNewsAgregator.ArticleService.entity;

import StockNewsAgregator.ArticleService.entity.enums.MatchLevel;
import StockNewsAgregator.ArticleService.entity.enums.MatchType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
