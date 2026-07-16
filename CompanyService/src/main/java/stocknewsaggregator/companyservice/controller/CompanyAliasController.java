package stocknewsaggregator.companyservice.controller;

import stocknewsaggregator.companyservice.dto.CompanyAliasDto;
import stocknewsaggregator.companyservice.service.alias.CompanyAliasService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alias")
@AllArgsConstructor
public class CompanyAliasController {
    private final CompanyAliasService companyAliasService;
    @PostMapping
    public ResponseEntity<CompanyAliasDto> CreateAlias(@RequestBody CompanyAliasDto companyAliasDto) {
        return ResponseEntity.ok(companyAliasService.CreateAlias(companyAliasDto));
    }
    @GetMapping("{id}")
    public ResponseEntity<CompanyAliasDto> GetAliasById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyAliasService.GetAliasById(id));
    }
    @GetMapping("/company/{id}")
    public ResponseEntity<List<CompanyAliasDto>> GetAliasesByCompany(@PathVariable UUID id) {
        return ResponseEntity.ok(companyAliasService.GetAliasesByCompany(id));
    }
    @GetMapping
    public ResponseEntity<List<CompanyAliasDto>> GetAliases() {
        return ResponseEntity.ok(companyAliasService.GetAliases());
    }
    @PutMapping
    public ResponseEntity<CompanyAliasDto> UpdateAlias(@RequestBody CompanyAliasDto companyAliasDto) {
        return ResponseEntity.ok(companyAliasService.UpdateAlias(companyAliasDto));
    }
    @DeleteMapping("/id/{id}")
    public void DeleteAlias(@PathVariable UUID id) {
        companyAliasService.DeleteAlias(id);
    }
}
