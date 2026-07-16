package stocknewsaggregator.companyservice.service.view;

import stocknewsaggregator.companyservice.dto.CompanySearchParamsDto;
import stocknewsaggregator.companyservice.dto.CompanyViewDto;

import java.util.List;
import java.util.UUID;

public interface CompanyViewService {
    CompanyViewDto GetCompanyView(String isin);
    CompanyViewDto GetCompanyViewById(UUID id);
    List<CompanyViewDto> SearchCompanies(CompanySearchParamsDto companySearchParamsDto);
}
