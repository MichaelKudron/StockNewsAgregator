package stocknewsaggregator.companyservice.repository;

import stocknewsaggregator.companyservice.entity.Company;
import stocknewsaggregator.companyservice.entity.enums.Market;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByIsin(String isin);
    boolean existsByIsinIgnoreCase(String isin);

    @Query("""
            SELECT DISTINCT c FROM Company c
            LEFT JOIN c.aliases a
            WHERE (:query IS NULL OR
                   LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
                   LOWER(c.ticker) LIKE LOWER(CONCAT('%', :query, '%')) OR
                   LOWER(c.isin) LIKE LOWER(CONCAT('%', :query, '%')) OR
                   LOWER(a.alias) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:market IS NULL OR c.market = :market)
            """)
    Page<Company> searchCompanies(@Param("query") String query,
                                  @Param("market") Market market,
                                  Pageable pageable);
}
