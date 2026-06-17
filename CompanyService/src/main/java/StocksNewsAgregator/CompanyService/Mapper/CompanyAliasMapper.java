package StocksNewsAgregator.CompanyService.Mapper;

import StocksNewsAgregator.CompanyService.Dtos.CompanyAliasDto;

import StocksNewsAgregator.CompanyService.Entities.Company;
import StocksNewsAgregator.CompanyService.Entities.CompanyAlias;


public class CompanyAliasMapper {
    public static CompanyAliasDto CompanyAliasToCompanyAliasDto(CompanyAlias companyAlias) {
        if (companyAlias == null) {
            return null;
        }

        CompanyAliasDto dto = new CompanyAliasDto();

        dto.setId(companyAlias.getId());

        if (companyAlias.getCompany() != null) {
            dto.setCompanyId(companyAlias.getCompany().getId());
        }

        dto.setAlias(companyAlias.getAlias());
        dto.setPriority(companyAlias.getPriority());
        dto.setCreatedAt(companyAlias.getCreatedAt());

        return dto;
    }

    public static CompanyAlias CompanyAliasDtoToCompanyAlias(
            CompanyAliasDto companyAliasDto,
            Company company
    ) {
        if (companyAliasDto == null) {
            return null;
        }

        CompanyAlias companyAlias = new CompanyAlias();

        companyAlias.setId(companyAliasDto.getId());
        companyAlias.setCompany(company);
        companyAlias.setAlias(companyAliasDto.getAlias());
        companyAlias.setPriority(companyAliasDto.getPriority());
        companyAlias.setCreatedAt(companyAliasDto.getCreatedAt());

        return companyAlias;
    }
}
