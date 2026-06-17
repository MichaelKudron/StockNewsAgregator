package StocksNewsAgregator.CompanyService.Service.CompanyView;

import StocksNewsAgregator.CompanyService.Dtos.CompanySearchParamsDto;
import StocksNewsAgregator.CompanyService.Dtos.CompanyViewDto;

import java.util.List;

public interface CompanyViewService {
    CompanyViewDto GetCompanyView(String isin);
    List<CompanyViewDto> SearchCompanies(CompanySearchParamsDto companySearchParamsDto);
}
