package stocknewsaggregator.companyservice.service.alias;

import stocknewsaggregator.companyservice.dto.CompanyAliasDto;
import stocknewsaggregator.companyservice.entity.Company;
import stocknewsaggregator.companyservice.entity.CompanyAlias;
import stocknewsaggregator.companyservice.mapper.CompanyAliasMapper;
import stocknewsaggregator.companyservice.repository.CompanyAliasRepository;
import stocknewsaggregator.companyservice.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyAliasServiceImpl implements CompanyAliasService {
    private final CompanyAliasRepository companyAliasRepository;
    private final CompanyRepository companyRepository;
    public CompanyAliasServiceImpl(CompanyAliasRepository companyAliasRepository, CompanyRepository companyRepository) {
        this.companyAliasRepository = companyAliasRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public CompanyAliasDto CreateAlias(CompanyAliasDto companyAliasDto) {
        Company company = companyRepository.getById(companyAliasDto.getCompanyId());
        CompanyAlias companyAlias = CompanyAliasMapper.CompanyAliasDtoToCompanyAlias(companyAliasDto, company);
        return CompanyAliasMapper.CompanyAliasToCompanyAliasDto(companyAliasRepository.save(companyAlias));
    }

    @Override
    public CompanyAliasDto GetAliasById(UUID id) {
        CompanyAlias companyAlias = companyAliasRepository.getById(id);
        return CompanyAliasMapper.CompanyAliasToCompanyAliasDto(companyAlias);
    }

    @Override
    public List<CompanyAliasDto> GetAliases() {
        List<CompanyAlias> companyAliases = companyAliasRepository.findAll();
        return companyAliases.stream().map(CompanyAliasMapper::CompanyAliasToCompanyAliasDto).toList();
    }

    @Override
    public List<CompanyAliasDto> GetAliasesByCompany(UUID companyId) {
        List<CompanyAlias> companyAliases = companyAliasRepository.findByCompanyId(companyId);
        return companyAliases.stream().map(CompanyAliasMapper::CompanyAliasToCompanyAliasDto).toList();
    }

    @Override
    public CompanyAliasDto UpdateAlias(CompanyAliasDto companyAliasDto) {
        CompanyAlias companyAlias = companyAliasRepository.getById(companyAliasDto.getId());
        companyAlias.setAlias(companyAliasDto.getAlias());
        companyAlias.setPriority(companyAliasDto.getPriority());
        return CompanyAliasMapper.CompanyAliasToCompanyAliasDto(companyAliasRepository.save(companyAlias));
    }


    @Override
    public void DeleteAlias(UUID id) {
        companyAliasRepository.deleteById(id);
    }
}
