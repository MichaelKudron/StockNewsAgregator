package StocksNewsAgregator.CompanyService.Service.Company;

import StocksNewsAgregator.CompanyService.Dtos.CompanyDto;
import StocksNewsAgregator.CompanyService.Entities.Company;
import StocksNewsAgregator.CompanyService.Mapper.CompanyMapper;
import StocksNewsAgregator.CompanyService.Repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;


    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;

    }

    @Override
    public CompanyDto CreateCompany(CompanyDto companyDto) {
        Company company = CompanyMapper.CompanyDtoToCompany(companyDto);
        companyRepository.save(company);
        return CompanyMapper.CompanyToCompanyDto(company);
    }

    @Override
    public CompanyDto GetCompany(String isin) {
        Optional<Company> company = companyRepository.findByIsin(isin);
        return CompanyMapper.CompanyToCompanyDto(company.get());
    }

    @Override
    public List<CompanyDto> GetCompanies() {
       List<Company> listOdCompanies =  companyRepository.findAll();
       return listOdCompanies.stream().map(CompanyMapper::CompanyToCompanyDto).toList();

    }

    @Override
    public CompanyDto UpdateCompany(CompanyDto companyDto) {
        Company company = companyRepository.findByIsin(companyDto.getIsin()).get();
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
        return CompanyMapper.CompanyToCompanyDto(companyRepository.save(company));
    }

    @Override
    public void DeleteCompany(String isin) {
        Optional<Company> company = companyRepository.findByIsin(isin);
        companyRepository.delete(company.get());
    }
}
