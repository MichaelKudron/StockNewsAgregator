package StocksNewsAgregator.CompanyService.Service.CompanyChartMapping;

import StocksNewsAgregator.CompanyService.Dtos.CompanyChartMappingDto;

import java.util.UUID;

public interface CompanyChartMappingService {
    CompanyChartMappingDto CreateCompanyChartMapping(CompanyChartMappingDto companyChartMappingDto);
    CompanyChartMappingDto GetCompanyChartMapping(UUID companyId);
    CompanyChartMappingDto GetCompanyChartMappingByChartId(UUID chartId);
    CompanyChartMappingDto UpdateCompanyChartMapping(CompanyChartMappingDto companyChartMappingDto);
    void DeleteCompanyChartMapping(UUID companyId);
}
