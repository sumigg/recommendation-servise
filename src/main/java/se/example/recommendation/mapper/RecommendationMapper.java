package se.example.recommendation.mapper;

import org.springframework.stereotype.Component;

import se.example.api.core.recommendation.Recommendation;
import se.example.recommendation.persistence.RecommendationEntity;

@Component
public class RecommendationMapper {

    public Recommendation mapToRecommendation(RecommendationEntity entity) {
        if (entity == null) {
            return null;
        }       
        return new Recommendation(
                entity.getProductId(),
                entity.getRecommendationId(),
                entity.getAuthor(),
                entity.getRate(),
                entity.getContent(),
                null);
    }   
    public RecommendationEntity mapToEntity(Recommendation recommendation) {
        if (recommendation == null) {
            return null;
        }   
        return new RecommendationEntity(
                recommendation.getProductId(),
                recommendation.getRecommendationId(),
                recommendation.getAuthor(),
                recommendation.getRate(),
                recommendation.getContent());
    }
    

}
