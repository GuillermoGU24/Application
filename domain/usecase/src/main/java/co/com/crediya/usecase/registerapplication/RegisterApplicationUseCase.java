package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.application.Application;
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
        return identityGateway.existsByDocument(application.getDocumento())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("documento: Cliente no existe"));
                    }
                    return loanTypeRepository.findById(application.getIdTipoPrestamo())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("tipoPrestamo: Tipo de préstamo no existe")))
                            .flatMap(tipo -> {
                                if (application.getMonto() < tipo.getMontoMinimo() || application.getMonto() > tipo.getMontoMaximo()) {
                                    return Mono.error(new IllegalArgumentException("monto: Monto fuera de rango permitido"));
                                }
                                return stateRepository.findById(1L)
                                        .switchIfEmpty(Mono.error(new IllegalArgumentException("estado: Estado inicial no encontrado")))
                                        .map(estado -> {
                                            application.setIdEstado(estado.getIdEstado());
                                            return application;
                                        });
                            })
                            .flatMap(applicationRepository::save);
                });
    }

}

