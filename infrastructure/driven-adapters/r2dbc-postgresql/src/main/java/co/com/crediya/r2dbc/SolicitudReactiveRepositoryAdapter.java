package co.com.crediya.r2dbc;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.r2dbc.entity.SolicitudEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class SolicitudReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<Application, SolicitudEntity, Long, SolicitudReactiveRepository>
        implements ApplicationRepository {

    private final TransactionalOperator tx;

    public SolicitudReactiveRepositoryAdapter(SolicitudReactiveRepository repository,
                                              ObjectMapper mapper,
                                              TransactionalOperator tx) {
        super(repository, mapper, entity -> mapper.map(entity, Application.class));
        this.tx = tx;
    }

    @Override
    public Mono<Application> save(Application solicitud) {
        return tx.transactional(super.save(solicitud));
    }
}
