package stocknewsaggregator.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    // Adresy backendów — domyślnie localhost, w Dockerze nadpisywane env.
    @Value("${gateway.article-uri:http://localhost:8080}")
    private String articleUri;
    @Value("${gateway.company-uri:http://localhost:8081}")
    private String companyUri;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        // DedupeResponseHeader zwija zdublowany nagłówek CORS (gateway + backend go dodają).
        return builder.routes()
                .route("article", r -> r
                        .path("/api/v1/article/**")
                        .filters(f -> f.dedupeResponseHeader(
                                "Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                        .uri(articleUri))
                .route("company", r -> r
                        .path("/api/v1/company/**",
                              "/api/v1/company-view/**",
                              "/api/v1/company-chart-mapping/**",
                              "/api/v1/alias/**",
                              "/api/v1/import/**")
                        .filters(f -> f.dedupeResponseHeader(
                                "Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                        .uri(companyUri))
                .build();
    }

    /** CORS obsługiwany na gatewayu (m.in. preflight OPTIONS, którego gateway nie przekazuje dalej). */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
