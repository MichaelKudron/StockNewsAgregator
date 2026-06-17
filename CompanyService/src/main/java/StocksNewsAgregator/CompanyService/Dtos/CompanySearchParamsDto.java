package StocksNewsAgregator.CompanyService.Dtos;

import StocksNewsAgregator.CompanyService.Entities.Enums.Market;
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