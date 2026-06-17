package StocksNewsAgregator.CompanyService.Contoller;

import StocksNewsAgregator.CompanyService.Dtos.CompanySearchParamsDto;
import StocksNewsAgregator.CompanyService.Dtos.CompanyViewDto;
import StocksNewsAgregator.CompanyService.Service.CompanyView.CompanyViewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company-view")
@AllArgsConstructor
public class CompanyViewController {
    private final CompanyViewService companyViewService;
    @GetMapping("{isin}")
    public ResponseEntity<CompanyViewDto> GetCompanyView(@PathVariable String isin) {
        return ResponseEntity.ok(companyViewService.GetCompanyView(isin));
    }
    @PostMapping("/search")
    public ResponseEntity<List<CompanyViewDto>> SearchCompanyView(@RequestBody CompanySearchParamsDto params) {
        return ResponseEntity.ok(companyViewService.SearchCompanies(params));
    }
}
