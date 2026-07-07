package StocksNewsAgregator.CompanyService.Service.CompanyView;

import StocksNewsAgregator.CompanyService.Dtos.CompanySearchParamsDto;
import StocksNewsAgregator.CompanyService.Dtos.CompanyViewDto;

import java.util.List;
import java.util.UUID;

public interface CompanyViewService {
    CompanyViewDto GetCompanyView(String isin);
    CompanyViewDto GetCompanyViewById(UUID id);
    List<CompanyViewDto> SearchCompanies(CompanySearchParamsDto companySearchParamsDto);
}
