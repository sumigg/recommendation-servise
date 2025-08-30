package se.example.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"se.example.recommendation",
		"se.example.api.core.recommendation",
		"se.example.util.http"
})
public class RecommendationApplication {
	public static void main(String[] args) {
		SpringApplication.run(RecommendationApplication.class, args);
	}

}
