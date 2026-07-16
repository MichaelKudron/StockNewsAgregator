package stocknewsaggregator.companyservice.controller;

import stocknewsaggregator.companyservice.dto.CompanyChartMappingDto;
import stocknewsaggregator.companyservice.service.chartmapping.CompanyChartMappingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/company-chart-mapping")
@AllArgsConstructor
public class CompanyChartMappingController {
    private final CompanyChartMappingService companyChartMappingService;
    @PostMapping
    public ResponseEntity<CompanyChartMappingDto> CreateCompanyChartMapping(@RequestBody CompanyChartMappingDto companyChartMappingDto) {
        return ResponseEntity.ok(companyChartMappingService.CreateCompanyChartMapping(companyChartMappingDto));
    }
    @GetMapping("/company/company/{id}")
    public ResponseEntity<CompanyChartMappingDto> GetByCompanyId(@PathVariable UUID id) {
        return ResponseEntity.ok(companyChartMappingService.GetCompanyChartMapping(id));
    }
    @GetMapping("/chart/chart/{id}")
    public ResponseEntity<CompanyChartMappingDto> GetByChartId(@PathVariable UUID id) {
        return ResponseEntity.ok(companyChartMappingService.GetCompanyChartMappingByChartId(id));
    }
    @PutMapping
    public ResponseEntity<CompanyChartMappingDto> UpdateCompanyChartMapping(@RequestBody CompanyChartMappingDto companyChartMappingDto) {
        return ResponseEntity.ok(companyChartMappingService.UpdateCompanyChartMapping(companyChartMappingDto));
    }
    @DeleteMapping("/company/companyId/{id}")
    public void DeleteCompanyChartMapping(@PathVariable UUID id) {
        companyChartMappingService.DeleteCompanyChartMapping(id);
    }
}
