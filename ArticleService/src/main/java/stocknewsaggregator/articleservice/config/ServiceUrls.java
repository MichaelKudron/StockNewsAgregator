package stocknewsaggregator.articleservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Adresy pozostałych serwisów. Domyślnie localhost (lokalne odpalenie),
 * w Dockerze nadpisywane zmiennymi SERVICES_COMPANY / SERVICES_ANALYSIS /
 * SERVICES_NEWSDOWNLOAD (nazwy serwisów z docker-compose).
 */
@Component
@ConfigurationProperties(prefix = "services")
@Getter
@Setter
public class ServiceUrls {
    private String company = "http://localhost:8081";
    private String analysis = "http://localhost:8004";
    private String newsdownload = "http://localhost:8003";
}
