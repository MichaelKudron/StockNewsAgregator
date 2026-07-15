package StocksNewsAgregator.CompanyService.Service.CompanyImport;

import StocksNewsAgregator.CompanyService.Entities.Company;
import StocksNewsAgregator.CompanyService.Entities.CompanyAlias;
import StocksNewsAgregator.CompanyService.Entities.CompanyChartMapping;
import StocksNewsAgregator.CompanyService.Entities.Enums.Market;
import StocksNewsAgregator.CompanyService.Repository.CompanyAliasRepository;
import StocksNewsAgregator.CompanyService.Repository.CompanyChartMappingRepository;
import StocksNewsAgregator.CompanyService.Repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class CompanyImportServiceImpl implements CompanyImportService {

    private final CompanyRepository companyRepository;
    private final CompanyAliasRepository companyAliasRepository;
    private final CompanyChartMappingRepository companyChartMappingRepository;

    @Override
    @Transactional
    public void ImportCompanies(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheet("companies");

            if (sheet == null) {
                throw new RuntimeException("Sheet companies not found");
            }

            Map<String, Integer> columns = ReadHeader(sheet.getRow(0));

            RequireColumn(columns, "ticker");
            RequireColumn(columns, "isin");
            RequireColumn(columns, "name");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String ticker = GetString(row, columns.get("ticker"));
                String isin = NormalizeIsin(GetString(row, columns.get("isin")));
                String name = GetString(row, columns.get("name"));

                if (IsBlank(ticker) || IsBlank(isin) || IsBlank(name)) {
                    continue;
                }

                Company company = companyRepository.findByIsin(isin).orElseGet(Company::new);

                company.setTicker(ticker);
                company.setIsin(isin);
                company.setName(name);
                company.setMarket(ParseMarket(GetString(row, columns.get("market"))));
                company.setShortName(GetString(row, columns.get("shortname")));
                company.setSector(GetString(row, columns.get("sector")));
                company.setIndustry(GetString(row, columns.get("industry")));
                company.setWebsite(GetString(row, columns.get("website")));
                company.setHeadquarter(GetString(row, columns.get("headquarterscity")));
                company.setIpoDate(ParseDate(row, columns.get("ipodate")));

                Company savedCompany = companyRepository.save(company);

                SyncAliases(savedCompany, GetString(row, columns.get("aliases")));
                SyncTradingViewMapping(savedCompany, GetString(row, columns.get("tradingviewsymbol")));
            }

        } catch (Exception ex) {
            throw new RuntimeException("Company import failed: " + ex.getMessage(), ex);
        }
    }

    private void SyncAliases(Company company, String aliasesRaw) {
        companyAliasRepository.deleteAllInBatch(
                companyAliasRepository.findByCompanyId(company.getId())
        );

        if (IsBlank(aliasesRaw)) {
            return;
        }

        String[] aliases = aliasesRaw.split(";");

        int priority = 0;

        for (String aliasRaw : aliases) {
            String alias = aliasRaw.trim();

            if (IsBlank(alias)) {
                continue;
            }

            CompanyAlias companyAlias = new CompanyAlias();

            companyAlias.setCompany(company);
            companyAlias.setAlias(alias);
            companyAlias.setPriority(priority);
            companyAlias.setCreatedAt(LocalDateTime.now());

            companyAliasRepository.save(companyAlias);

            priority++;
        }
    }

    private void SyncTradingViewMapping(Company company, String tradingViewSymbol) {
        if (IsBlank(tradingViewSymbol)) {
            return;
        }

        String symbol = tradingViewSymbol.trim();

        CompanyChartMapping mapping = companyChartMappingRepository.findByCompanyId(company.getId());

        if (mapping == null) {
            mapping = new CompanyChartMapping();
            mapping.setCompany(company);
            mapping.setProvider("TradingView");
            mapping.setDefault(true);
            mapping.setCreatedAt(LocalDateTime.now());
        }

        mapping.setSymbol(symbol);
        mapping.setUpdatedAt(LocalDateTime.now());

        companyChartMappingRepository.save(mapping);
    }

    private Market ParseMarket(String value) {
        if (IsBlank(value)) {
            return null;
        }

        String normalized = value.trim()
                .replace(" ", "")
                .replace("_", "")
                .replace("-", "")
                .toLowerCase();

        if (normalized.equals("gpw")) {
            return Market.Gpw;
        }

        if (normalized.equals("newconnect")) {
            return Market.NewConnect;
        }

        throw new RuntimeException("Unknown market: " + value);
    }

    private Map<String, Integer> ReadHeader(Row headerRow) {
        if (headerRow == null) {
            throw new RuntimeException("Header row not found");
        }

        Map<String, Integer> columns = new HashMap<>();

        for (Cell cell : headerRow) {
            String header = GetCellString(cell);

            if (!IsBlank(header)) {
                columns.put(NormalizeHeader(header), cell.getColumnIndex());
            }
        }

        return columns;
    }

    private void RequireColumn(Map<String, Integer> columns, String columnName) {
        if (!columns.containsKey(columnName)) {
            throw new RuntimeException("Required column not found: " + columnName);
        }
    }

    private String NormalizeHeader(String value) {
        return value
                .trim()
                .replace(" ", "")
                .replace("_", "")
                .replace("-", "")
                .toLowerCase();
    }

    private String NormalizeIsin(String value) {
        if (value == null) {
            return null;
        }

        return value.trim().toUpperCase();
    }

    private String GetString(Row row, Integer columnIndex) {
        if (row == null || columnIndex == null) {
            return null;
        }

        Cell cell = row.getCell(columnIndex);

        if (cell == null) {
            return null;
        }

        return GetCellString(cell);
    }

    private String GetCellString(Cell cell) {
        if (cell == null) {
            return null;
        }

        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);

        if (value == null) {
            return null;
        }

        return value.trim();
    }

    private LocalDate ParseDate(Row row, Integer columnIndex) {
        if (row == null || columnIndex == null) {
            return null;
        }

        Cell cell = row.getCell(columnIndex);

        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        String value = GetString(row, columnIndex);

        if (IsBlank(value)) {
            return null;
        }

        return LocalDate.parse(value);
    }

    private boolean IsBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}