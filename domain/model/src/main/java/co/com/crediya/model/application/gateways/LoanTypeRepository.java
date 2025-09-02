package co.com.crediya.model.application.gateways;

import co.com.crediya.model.application.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Mono<LoanType> findById(Long id);
}
