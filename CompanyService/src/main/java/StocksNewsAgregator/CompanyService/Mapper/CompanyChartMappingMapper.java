package StocksNewsAgregator.CompanyService.Mapper;

import StocksNewsAgregator.CompanyService.Dtos.CompanyChartMappingDto;
import StocksNewsAgregator.CompanyService.Entities.Company;
import StocksNewsAgregator.CompanyService.Entities.CompanyChartMapping;

public class CompanyChartMappingMapper {

    public static CompanyChartMappingDto CompanyChartMappingToCompanyChartMappingDto(
            CompanyChartMapping companyChartMapping
    ) {
        if (companyChartMapping == null) {
            return null;
        }

        CompanyChartMappingDto dto = new CompanyChartMappingDto();

        dto.setId(companyChartMapping.getId());

        if (companyChartMapping.getCompany() != null) {
            dto.setCompanyId(companyChartMapping.getCompany().getId());
        }

        dto.setProvider(companyChartMapping.getProvider());
        dto.setSymbol(companyChartMapping.getSymbol());
        dto.setDefault(companyChartMapping.isDefault());
        dto.setCreatedAt(companyChartMapping.getCreatedAt());
        dto.setUpdatedAt(companyChartMapping.getUpdatedAt());

        return dto;
    }

    public static CompanyChartMapping CompanyChartMappingDtoToCompanyChartMapping(
            CompanyChartMappingDto companyChartMappingDto,
            Company company
    ) {
        if (companyChartMappingDto == null) {
            return null;
        }

        CompanyChartMapping companyChartMapping = new CompanyChartMapping();

        companyChartMapping.setId(companyChartMappingDto.getId());
        companyChartMapping.setCompany(company);
        companyChartMapping.setProvider(companyChartMappingDto.getProvider());
        companyChartMapping.setSymbol(companyChartMappingDto.getSymbol());
        companyChartMapping.setDefault(companyChartMappingDto.isDefault());
        companyChartMapping.setCreatedAt(companyChartMappingDto.getCreatedAt());
        companyChartMapping.setUpdatedAt(companyChartMappingDto.getUpdatedAt());

        return companyChartMapping;
    }
}
