package StockNewsAgregator.ArticleService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableScheduling
public class ArticleServiceApplication {
	@Bean
	public WebClient webClient(){
		return WebClient.builder().build();
	};
	public static void main(String[] args) {
		SpringApplication.run(ArticleServiceApplication.class, args);
	}

}
