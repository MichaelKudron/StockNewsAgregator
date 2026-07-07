package StockNewsAgregator.ArticleService.repository;

import StockNewsAgregator.ArticleService.entity.ArticleCompanyLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArticleCompanyLinkRepository extends JpaRepository<ArticleCompanyLink, UUID> {
    List<ArticleCompanyLink> findByCompanyId(UUID companyId);
}
