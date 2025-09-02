package co.com.crediya.model.application.gateways;

import co.com.crediya.model.application.State;
import reactor.core.publisher.Mono;

public interface StateRepository {
    Mono<State> findById(Long id);

}
