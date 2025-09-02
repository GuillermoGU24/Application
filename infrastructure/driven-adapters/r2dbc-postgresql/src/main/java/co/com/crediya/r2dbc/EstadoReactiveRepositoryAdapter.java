package co.com.crediya.r2dbc;


import co.com.crediya.model.application.State;
import co.com.crediya.model.application.gateways.StateRepository;
import co.com.crediya.r2dbc.entity.EstadoEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class EstadoReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<State, EstadoEntity, Long, EstadoReactiveRepository>
        implements StateRepository {

    public EstadoReactiveRepositoryAdapter(EstadoReactiveRepository repository,
                                           ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, State.class));
    }

    @Override
    public Mono<State> findById(Long id) {
        return repository.findById(id).map(this::toEntity);
    }
}
