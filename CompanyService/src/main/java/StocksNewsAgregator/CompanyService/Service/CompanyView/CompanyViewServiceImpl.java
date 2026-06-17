package StocksNewsAgregator.CompanyService.Service.CompanyView;

import StocksNewsAgregator.CompanyService.Dtos.*;
import StocksNewsAgregator.CompanyService.Entities.Company;
import StocksNewsAgregator.CompanyService.Entities.CompanyAlias;
import StocksNewsAgregator.CompanyService.Entities.CompanyChartMapping;
import StocksNewsAgregator.CompanyService.Mapper.CompanyAliasMapper;
import StocksNewsAgregator.CompanyService.Mapper.CompanyChartMappingMapper;
import StocksNewsAgregator.CompanyService.Mapper.CompanyMapper;
import StocksNewsAgregator.CompanyService.Repository.CompanyAliasRepository;
import StocksNewsAgregator.CompanyService.Repository.CompanyChartMappingRepository;
import StocksNewsAgregator.CompanyService.Repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
