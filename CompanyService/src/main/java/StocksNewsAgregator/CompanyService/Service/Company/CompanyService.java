package StocksNewsAgregator.CompanyService.Service.Company;

import StocksNewsAgregator.CompanyService.Dtos.CompanyDto;
import StocksNewsAgregator.CompanyService.Dtos.MatchingCompanyDto;

import java.util.List;

public interface CompanyService {
    CompanyDto CreateCompany(CompanyDto companyDto);
    CompanyDto GetCompany(String isin);
    List<CompanyDto> GetCompanies();
    CompanyDto UpdateCompany(CompanyDto companyDto);
    void DeleteCompany(String isin);
    List<MatchingCompanyDto> GetMatchingCompanies();
}
