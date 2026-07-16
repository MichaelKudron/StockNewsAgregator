package stocknewsaggregator.companyservice.controller;

import stocknewsaggregator.companyservice.dto.CompanySearchParamsDto;
import stocknewsaggregator.companyservice.dto.CompanyViewDto;
import stocknewsaggregator.companyservice.service.view.CompanyViewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/company-view")
@AllArgsConstructor
public class CompanyViewController {
    private final CompanyViewService companyViewService;
    @GetMapping("{isin}")
    public ResponseEntity<CompanyViewDto> GetCompanyView(@PathVariable String isin) {
        return ResponseEntity.ok(companyViewService.GetCompanyView(isin));
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<CompanyViewDto> GetCompanyViewById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyViewService.GetCompanyViewById(id));
    }
    @PostMapping("/search")
    public ResponseEntity<List<CompanyViewDto>> SearchCompanyView(@RequestBody CompanySearchParamsDto params) {
        return ResponseEntity.ok(companyViewService.SearchCompanies(params));
    }
}
