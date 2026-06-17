package StocksNewsAgregator.CompanyService.Contoller;

import StocksNewsAgregator.CompanyService.Service.CompanyImport.CompanyImportService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/import")
@AllArgsConstructor
public class CompanyImportController {

    private final CompanyImportService companyImportService;

    @PostMapping(
            value = "/companies",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> ImportCompanies(@RequestParam("file") MultipartFile file) {
        companyImportService.ImportCompanies(file);
        return ResponseEntity.noContent().build();
    }
}