package stocknewsaggregator.articleservice.repository;

import stocknewsaggregator.articleservice.entity.ArticleCompanyLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArticleCompanyLinkRepository extends JpaRepository<ArticleCompanyLink, UUID> {
    List<ArticleCompanyLink> findByCompanyId(UUID companyId);
}
