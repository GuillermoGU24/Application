package co.com.crediya.r2dbc;

import co.com.crediya.model.application.LoanType;
import co.com.crediya.model.application.gateways.LoanTypeRepository;
import co.com.crediya.r2dbc.entity.LoanTypeEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
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
        log.debug("Looking up loan type with id={}", id);

        return repository.findById(id)
                .map(this::toEntity)
                .doOnSuccess(loanType -> {
                    if (loanType != null) {
                        log.info("Found loan type with id={} -> {}", id, loanType.getName());
                    } else {
                        log.warn("No loan type found with id={}", id);
                    }
                })
                .doOnError(e -> log.error("Error fetching loan type with id={}: {}", id, e.getMessage(), e));
    }
}
