package stocknewsaggregator.companyservice.service.alias;

import stocknewsaggregator.companyservice.dto.CompanyAliasDto;

import java.util.List;
import java.util.UUID;

public interface CompanyAliasService {
    CompanyAliasDto CreateAlias(CompanyAliasDto companyAliasDto);
    CompanyAliasDto GetAliasById(UUID id);
    List<CompanyAliasDto> GetAliases();
    List<CompanyAliasDto> GetAliasesByCompany(UUID companyId);
    CompanyAliasDto UpdateAlias(CompanyAliasDto companyAliasDto);
    void DeleteAlias(UUID id);
}
