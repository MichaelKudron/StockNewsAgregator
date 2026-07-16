package stocknewsaggregator.companyservice.dto;

import stocknewsaggregator.companyservice.entity.enums.Market;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanySearchParamsDto {

    private String query;

    private Market market;

    private Integer page = 0;

    private Integer size = 20;

    private String sortBy = "name";

    private String sortDirection = "asc";
}