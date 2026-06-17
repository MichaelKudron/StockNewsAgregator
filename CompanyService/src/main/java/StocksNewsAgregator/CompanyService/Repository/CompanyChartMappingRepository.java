package StocksNewsAgregator.CompanyService.Repository;

import StocksNewsAgregator.CompanyService.Entities.CompanyChartMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyChartMappingRepository extends JpaRepository<CompanyChartMapping, UUID> {

    CompanyChartMapping findByCompanyId(UUID companyId);
    boolean existsByCompany_IdAndProviderIgnoreCaseAndSymbolIgnoreCase(
            UUID companyId,
            String provider,
            String symbol
    );
}
