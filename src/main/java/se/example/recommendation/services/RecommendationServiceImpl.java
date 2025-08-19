package se.example.recommendation.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.DuplicateKeyException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.example.api.core.recommendation.Recommendation;
import se.example.api.core.recommendation.RecommendationService;
import se.example.api.exception.InvalidInputException;
import se.example.recommendation.mapper.RecommendationMapper;
import se.example.recommendation.persistence.RecommendationEntity;
import se.example.recommendation.persistence.RecommendationRepository;
import se.example.util.http.ServiceUtil;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

  private final RecommendationRepository repository;

  private final RecommendationMapper mapper;

  private final ServiceUtil serviceUtil;

  @Autowired
  public RecommendationServiceImpl(RecommendationRepository repository, RecommendationMapper mapper,
      ServiceUtil serviceUtil) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
  }

  @Override
  public Mono<Recommendation> createRecommendation(Recommendation body) {

    if (body.getProductId() < 1) {
      throw new InvalidInputException("Invalid productId: " + body.getProductId());
    }
    if (body.getRecommendationId() < 1) {
      throw new InvalidInputException("Invalid recommendationId: " + body.getRecommendationId());
    }

    RecommendationEntity entity = mapper.mapToEntity(body);

    Mono<RecommendationEntity> newEntity = repository.save(entity);
    LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(),
        body.getRecommendationId());
    return newEntity.map(mapper::mapToRecommendation)
        .map(this::setServiceAddress)
        .onErrorMap(DuplicateKeyException.class, ex -> new InvalidInputException(
            "Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId()));

  }

  @Override
  public Flux<Recommendation> getRecommendations(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    Flux<RecommendationEntity> entityList = repository.findByProductId(productId);
    return entityList.map(mapper::mapToRecommendation)
        .map(this::setServiceAddress);
  }

  @Override
  public Mono<Void> deleteRecommendations(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
    return repository.deleteAll(repository.findByProductId(productId));
  }

  private Recommendation setServiceAddress(Recommendation e) {
    e.setServiceAddress(serviceUtil.getServiceAddress());
    return e;
  }
  
}