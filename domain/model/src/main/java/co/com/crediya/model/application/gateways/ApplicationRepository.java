package co.com.crediya.model.application.gateways;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.ApplicationForReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ApplicationRepository {
    Mono<Application> save(Application app);
    Flux<ApplicationForReview> findForReview(List<String> estados, int page, int size);
    Mono<Long> countForReview(List<String> estados);

    Flux<ApplicationForReview> findByStateId(Long stateId);
}
