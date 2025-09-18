package co.com.crediya.r2dbc;

import co.com.crediya.model.application.State;
import co.com.crediya.model.application.gateways.StateRepository;
import co.com.crediya.r2dbc.entity.StateEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class StateReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<State, StateEntity, Long, StateReactiveRepository>
        implements StateRepository {

    public StateReactiveRepositoryAdapter(StateReactiveRepository repository,
                                          ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, State.class));
    }

    @Override
    public Mono<State> findById(Long id) {
        log.debug("Looking up state with id={}", id);

        return repository.findById(id)
                .map(this::toEntity)
                .doOnSuccess(state -> {
                    if (state != null) {
                        log.info("Found state with id={} -> {}", id, state.getName());
                    } else {
                        log.warn("No state found with id={}", id);
                    }
                })
                .doOnError(e -> log.error("Error fetching state with id={}: {}", id, e.getMessage(), e));
    }
}
