package StocksNewsAgregator.CompanyService.Mapper;

import StocksNewsAgregator.CompanyService.Dtos.CompanyDto;
import StocksNewsAgregator.CompanyService.Dtos.MatchingCompanyDto;
import StocksNewsAgregator.CompanyService.Entities.Company;

public class CompanyMapper {
    public static CompanyDto CompanyToCompanyDto(Company company) {
        return new CompanyDto(company.getId(), company.getTicker(), company.getIsin(), company.getName(),
                company.getShortName(), company.getMarket(), company.getSector(),
                company.getIndustry(), company.getWebsite(), company.getHeadquarter(),
                company.getIpoDate(), company.isActive(), company.getDelistedAt(),
                company.getCreatedAt(), company.getUpdatedAt());
    }
    public static Company CompanyDtoToCompany(CompanyDto companyDto) {
        Company company = new Company();
        company.setId(companyDto.getId());
        company.setTicker(companyDto.getTicker());
        company.setIsin(companyDto.getIsin());
        company.setName(companyDto.getName());
        company.setShortName(companyDto.getShortName());
        company.setMarket(companyDto.getMarket());
        company.setSector(companyDto.getSector());
        company.setIndustry(companyDto.getIndustry());
        company.setWebsite(companyDto.getWebsite());
        company.setHeadquarter(companyDto.getHeadquarter());
        company.setIpoDate(companyDto.getIpoDate());
        company.setActive(companyDto.isActive());
        company.setDelistedAt(companyDto.getDelistedAt());

        return company;
    }
    public static MatchingCompanyDto CompanyToMatchingCompanyDto(Company company) {
        MatchingCompanyDto matchingCompanyDto = new MatchingCompanyDto();
        matchingCompanyDto.setId(company.getId());
        matchingCompanyDto.setName(company.getName());
        matchingCompanyDto.setShortName(company.getShortName());
        matchingCompanyDto.setTicker(company.getTicker());
        return matchingCompanyDto;
    }
}
