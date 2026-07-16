package stocknewsaggregator.companyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyAliasDto {
        private UUID id;
        private UUID companyId;
        private String alias;
        private int priority;
        private LocalDateTime createdAt;
}
