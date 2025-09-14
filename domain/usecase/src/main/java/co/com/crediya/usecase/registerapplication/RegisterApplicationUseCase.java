package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.exeption.LoanDomainValidator;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.IdentityGateway;
import co.com.crediya.model.application.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.sql.SQLOutput;
import java.util.Objects;

@RequiredArgsConstructor
public class RegisterApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final IdentityGateway identityGateway;

    public Mono<Application> register(Application app, String bearerToken) {
        return identityGateway.findAuthUserByToken(bearerToken)
                .flatMap(authUser -> {

                    if ("CLIENTE".equals(authUser.getRol().getName()) && !authUser.getDocument().equals(app.getDocument())) {
                        return Mono.error(new IllegalArgumentException("forbidden: Cannot create application for another user"));
                    }

                    app.setStateId(1L);

                    return loanTypeRepository.findById(app.getLoanTypeId())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("loanTypeId: Loan type not found")))
                            .flatMap(loanType -> {
                                LoanDomainValidator.validate(app, loanType);
                                return applicationRepository.save(app);
                            });
                });


    }
}
