package StocksNewsAgregator.CompanyService.Service.CompanyImport;

import org.springframework.web.multipart.MultipartFile;

public interface CompanyImportService {
    void ImportCompanies(MultipartFile file);
}
