package stocknewsaggregator.companyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyViewDto {
    private CompanyDto company;
    private CompanyChartMappingDto companyChartMapping;
    private List<CompanyAliasDto> companyAliases;
}
