package StocksNewsAgregator.CompanyService.Repository;

import StocksNewsAgregator.CompanyService.Entities.CompanyAlias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyAliasRepository extends JpaRepository<CompanyAlias, UUID> {
    List<CompanyAlias> findByCompanyId(UUID companyId);
    boolean existsByCompany_IdAndAliasIgnoreCase(UUID companyId, String alias);
}
