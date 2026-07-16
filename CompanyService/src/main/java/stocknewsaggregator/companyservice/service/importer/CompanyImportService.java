package stocknewsaggregator.companyservice.service.importer;

import org.springframework.web.multipart.MultipartFile;

public interface CompanyImportService {
    void ImportCompanies(MultipartFile file);
}
