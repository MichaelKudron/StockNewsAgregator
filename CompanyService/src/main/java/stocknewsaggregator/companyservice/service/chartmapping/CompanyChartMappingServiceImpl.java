package stocknewsaggregator.companyservice.service.chartmapping;

import stocknewsaggregator.companyservice.dto.CompanyChartMappingDto;
import stocknewsaggregator.companyservice.entity.Company;
import stocknewsaggregator.companyservice.entity.CompanyChartMapping;
import stocknewsaggregator.companyservice.mapper.CompanyChartMappingMapper;
import stocknewsaggregator.companyservice.repository.CompanyChartMappingRepository;
import stocknewsaggregator.companyservice.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class CompanyChartMappingServiceImpl implements CompanyChartMappingService{
    private final CompanyChartMappingRepository companyChartMappingRepository;
    private final CompanyRepository companyRepository;
    public CompanyChartMappingServiceImpl(CompanyChartMappingRepository companyChartMappingRepository, CompanyRepository companyRepository) {
        this.companyChartMappingRepository = companyChartMappingRepository;
        this.companyRepository = companyRepository;
    }
    @Override
    public CompanyChartMappingDto CreateCompanyChartMapping(CompanyChartMappingDto companyChartMappingDto) {
        Company company = companyRepository.getById(companyChartMappingDto.getCompanyId());
        CompanyChartMapping companyChartMapping = CompanyChartMappingMapper.CompanyChartMappingDtoToCompanyChartMapping(companyChartMappingDto, company);
        companyChartMappingRepository.save(companyChartMapping);
        return CompanyChartMappingMapper.CompanyChartMappingToCompanyChartMappingDto(companyChartMappingRepository.save(companyChartMapping));

    }

    @Override
    public CompanyChartMappingDto GetCompanyChartMapping(UUID companyId) {
        CompanyChartMapping companyChartMapping = companyChartMappingRepository.findByCompanyId(companyId);
        return CompanyChartMappingMapper.CompanyChartMappingToCompanyChartMappingDto(companyChartMapping);
    }

    @Override
    public CompanyChartMappingDto GetCompanyChartMappingByChartId(UUID chartId) {
        CompanyChartMapping companyChartMapping = companyChartMappingRepository.getById(chartId);
        return CompanyChartMappingMapper.CompanyChartMappingToCompanyChartMappingDto(companyChartMapping);
    }

    @Override
    public CompanyChartMappingDto UpdateCompanyChartMapping(CompanyChartMappingDto companyChartMappingDto) {
        CompanyChartMapping companyChartMapping = companyChartMappingRepository.getById(companyChartMappingDto.getId());
        Company company = companyRepository.getById(companyChartMappingDto.getCompanyId());
        companyChartMapping.setCompany(company);
        companyChartMapping.setProvider(companyChartMappingDto.getProvider());
        companyChartMapping.setSymbol(companyChartMappingDto.getSymbol());
        companyChartMapping.setDefault(companyChartMappingDto.isDefault());
        companyChartMapping.setCreatedAt(companyChartMappingDto.getCreatedAt());
        companyChartMapping.setUpdatedAt(companyChartMappingDto.getUpdatedAt());
        return CompanyChartMappingMapper.CompanyChartMappingToCompanyChartMappingDto(companyChartMappingRepository.save(companyChartMapping));
    }

    @Override
    public void DeleteCompanyChartMapping(UUID companyId) {
        CompanyChartMapping companyChartMapping = companyChartMappingRepository.findByCompanyId(companyId);
        companyChartMappingRepository.delete(companyChartMapping);

    }
}
