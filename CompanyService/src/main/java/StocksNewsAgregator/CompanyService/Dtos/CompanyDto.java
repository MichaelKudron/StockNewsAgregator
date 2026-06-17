package StocksNewsAgregator.CompanyService.Dtos;

import StocksNewsAgregator.CompanyService.Entities.Enums.Market;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {
    private UUID id;
    private String ticker;
    private String isin;
    private String name;
    private String shortName;
    private Market market;
    private String sector;
    private String industry;
    private String website;
    private String headquarter;
    private LocalDate ipoDate;
    private boolean active;
    private LocalDateTime delistedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
//