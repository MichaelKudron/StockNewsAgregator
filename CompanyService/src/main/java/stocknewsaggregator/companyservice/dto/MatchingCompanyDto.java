package stocknewsaggregator.companyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MatchingCompanyDto {
    private UUID id;
    private String isin;
    private String name;
    private String shortName;
    private String ticker;
    private List<String> aliases;
}
