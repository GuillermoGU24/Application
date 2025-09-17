package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.LoanType;
import co.com.crediya.model.application.State;
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
                    if ("CLIENTE".equals(authUser.getRol().getName()) &&
                            !authUser.getDocument().equals(app.getDocument())) {
                        return Mono.error(new IllegalArgumentException(
                                "forbidden: Cannot create application for another user"));
                    }

                    // Creamos internamente el state
                    State state = new State();
                    state.setStateId(1L); // pendiente
                    app.setState(state);

                    // Validamos que LoanType tenga ID
                    if (app.getLoanType() == null || app.getLoanType().getLoanTypeId() == null) {
                        return Mono.error(new IllegalArgumentException("loanTypeId is required"));
                    }

                    // Buscamos LoanType completo
                    return loanTypeRepository.findById(app.getLoanType().getLoanTypeId())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("loanTypeId: Loan type not found")))
                            .flatMap(loanType -> {
                                LoanDomainValidator.validate(app, loanType);

                                // asignamos el LoanType completo al app
                                app.setLoanType(loanType);

                                return applicationRepository.save(app);
                            });
                });
    }


}
