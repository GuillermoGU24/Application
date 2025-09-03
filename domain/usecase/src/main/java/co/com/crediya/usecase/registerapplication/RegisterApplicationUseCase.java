package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.exeption.LoanDomainValidator;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.IdentityGateway;
import co.com.crediya.model.application.gateways.LoanTypeRepository;
import co.com.crediya.model.application.gateways.StateRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final IdentityGateway identityGateway;

    public Mono<Application> create(Application application) {
        return identityGateway.existsByDocument(application.getDocument())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("document: Client does not exist"));
                    }
                    return loanTypeRepository.findById(application.getLoanTypeId())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("loanType: Loan type not found")))
                            .flatMap(loanType -> {
                                try {
                                    LoanDomainValidator.validate(application, loanType);
                                } catch (IllegalArgumentException ex) {
                                    return Mono.error(ex);
                                }
                                return stateRepository.findById(1L)
                                        .switchIfEmpty(Mono.error(new IllegalArgumentException("state: Initial state not found")))
                                        .map(state -> {
                                            application.setStateId(state.getStateId());
                                            return application;
                                        });
                            })
                            .flatMap(applicationRepository::save);
                });
    }

}
