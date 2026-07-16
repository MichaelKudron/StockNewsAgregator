package stocknewsaggregator.companyservice.entity;

import stocknewsaggregator.companyservice.entity.enums.Market;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 16)
    private String ticker;

    @Column(unique = true, length = 12)
    private String isin;

    @Column(nullable = false)
    private String name;

    private String shortName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Market market;

    private String sector;
    private String industry;
    private String website;
    private String headquarter;
    private LocalDate ipoDate;

    @Column(nullable = false)
    private boolean isActive = true;

    private LocalDateTime delistedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CompanyAlias> aliases = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CompanyChartMapping> chartMappings = new ArrayList<>();

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}