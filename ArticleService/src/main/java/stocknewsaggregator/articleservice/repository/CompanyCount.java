package stocknewsaggregator.articleservice.repository;

import java.util.UUID;

/** Projekcja: liczba powiązań na spółkę (do rankingu trending). */
public interface CompanyCount {
    UUID getCompanyId();
    long getCount();
}
