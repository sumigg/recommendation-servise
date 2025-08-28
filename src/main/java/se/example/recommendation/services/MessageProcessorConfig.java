package se.example.recommendation.services;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import se.example.api.core.recommendation.Recommendation;
import se.example.api.core.recommendation.RecommendationService;
import se.example.api.event.Event;
import se.example.api.exception.EventProcessingException;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final RecommendationService recommendationService;

    @Autowired
    public MessageProcessorConfig(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Bean
    Consumer<Event<Integer, Recommendation>> messageProcessor() {
        return event -> {
            LOGGER.debug("Process event: {}", event);
            switch (event.getEventType()) {
                case CREATE:
                    Recommendation recommendation = event.getData();
                    LOGGER.debug("Create recommendation with ID: {}", recommendation.getProductId());
                    recommendationService.createRecommendation(recommendation).block();
                    break;
                case DELETE:
                    Integer productId = event.getKey();
                    LOGGER.debug("Delete recommendation with ID: {}", productId);
                    recommendationService.deleteRecommendations(productId).block();
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType()
                            + ", expected a CREATE or DELETE event";
                    LOGGER.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }
            LOGGER.debug("Event processed successfully: {}", event);
        };
    }

}
