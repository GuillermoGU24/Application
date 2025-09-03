package co.com.crediya.r2dbc;


import co.com.crediya.model.application.LoanType;
import co.com.crediya.model.application.gateways.LoanTypeRepository;
import co.com.crediya.r2dbc.LoanTypeReactiveRepository;
import co.com.crediya.r2dbc.entity.LoanTypeEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<LoanType, LoanTypeEntity, Long, LoanTypeReactiveRepository>
        implements LoanTypeRepository {

    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository,
                                                 ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, LoanType.class));
    }


    @Override
    public Mono<LoanType> findById(Long id) {
        return repository.findById(id).map(this::toEntity);
    }
}
