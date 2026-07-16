package stocknewsaggregator.companyservice.service.view;

import stocknewsaggregator.companyservice.dto.*;
import stocknewsaggregator.companyservice.entity.Company;
import stocknewsaggregator.companyservice.entity.CompanyAlias;
import stocknewsaggregator.companyservice.entity.CompanyChartMapping;
import stocknewsaggregator.companyservice.mapper.CompanyAliasMapper;
import stocknewsaggregator.companyservice.mapper.CompanyChartMappingMapper;
import stocknewsaggregator.companyservice.mapper.CompanyMapper;
import stocknewsaggregator.companyservice.repository.CompanyAliasRepository;
import stocknewsaggregator.companyservice.repository.CompanyChartMappingRepository;
import stocknewsaggregator.companyservice.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CompanyViewServiceImpl implements CompanyViewService {
    private final CompanyRepository companyRepository;
    private final CompanyAliasRepository companyAliasRepository;
    private final CompanyChartMappingRepository companyChartMappingRepository;

    @Override
    public CompanyViewDto GetCompanyView(String isin) {
        Company company = companyRepository.findByIsin(isin).get();
        CompanyChartMapping companyChartMapping = companyChartMappingRepository.findByCompanyId(company.getId());
        List<CompanyAlias> companyAliases = companyAliasRepository.findByCompanyId(company.getId());
        CompanyDto companyDto = CompanyMapper.CompanyToCompanyDto(company);
        CompanyChartMappingDto companyChartMappingDto = CompanyChartMappingMapper.CompanyChartMappingToCompanyChartMappingDto(companyChartMapping);
        List<CompanyAliasDto> companyAliasesDtos = companyAliases.stream().map(CompanyAliasMapper::CompanyAliasToCompanyAliasDto).toList();
        return new CompanyViewDto(companyDto, companyChartMappingDto, companyAliasesDtos);
    }

    @Override
    public CompanyViewDto GetCompanyViewById(UUID id) {
        Company company = companyRepository.findById(id).get();
        CompanyChartMapping companyChartMapping = companyChartMappingRepository.findByCompanyId(company.getId());
        List<CompanyAlias> companyAliases = companyAliasRepository.findByCompanyId(company.getId());
        CompanyDto companyDto = CompanyMapper.CompanyToCompanyDto(company);
        CompanyChartMappingDto companyChartMappingDto = CompanyChartMappingMapper.CompanyChartMappingToCompanyChartMappingDto(companyChartMapping);
        List<CompanyAliasDto> companyAliasesDtos = companyAliases.stream().map(CompanyAliasMapper::CompanyAliasToCompanyAliasDto).toList();
        return new CompanyViewDto(companyDto, companyChartMappingDto, companyAliasesDtos);
    }

    @Override
    public List<CompanyViewDto> SearchCompanies(CompanySearchParamsDto params) {
        Sort sort = params.getSortDirection().equalsIgnoreCase("desc")
                ? Sort.by(params.getSortBy()).descending()
                : Sort.by(params.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        return companyRepository
                .searchCompanies(params.getQuery(), params.getMarket(), pageable)
                .stream()
                .map(company -> {
                    CompanyChartMapping chartMapping = companyChartMappingRepository.findByCompanyId(company.getId());
                    List<CompanyAlias> aliases = companyAliasRepository.findByCompanyId(company.getId());

                    CompanyDto companyDto = CompanyMapper.CompanyToCompanyDto(company);
                    CompanyChartMappingDto chartMappingDto = CompanyChartMappingMapper.CompanyChartMappingToCompanyChartMappingDto(chartMapping);
                    List<CompanyAliasDto> aliasesDtos = aliases.stream().map(CompanyAliasMapper::CompanyAliasToCompanyAliasDto).toList();

                    return new CompanyViewDto(companyDto, chartMappingDto, aliasesDtos);
                })
                .toList();
    }
}
