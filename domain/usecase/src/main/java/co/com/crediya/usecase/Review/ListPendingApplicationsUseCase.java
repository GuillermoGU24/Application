package co.com.crediya.usecase.Review;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.ApplicationForReview;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.UserGateway;
import co.com.crediya.model.auth.User;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ListPendingApplicationsUseCase {

    private final ApplicationRepository applicationRepository;
    private final UserGateway userGateway;

    public Mono<List<ApplicationForReview>> listAllPending(String bearerToken) {
        return applicationRepository.findByStateId(1L).collectList()
                .flatMap(apps -> {
                    List<String> documents = apps.stream()
                            .map(ApplicationForReview::getDocumento) // ✅ ahora usamos el campo correcto
                            .distinct()
                            .toList();

                    return userGateway.findUsersByDocuments(documents, bearerToken)
                            .collectMap(User::getDocument)
                            .map(usersMap -> apps.stream().map(app -> {
                                User user = usersMap.get(app.getDocumento());
                                return ApplicationForReview.builder()
                                        .applicationId(app.getApplicationId())
                                        .monto(app.getMonto())
                                        .plazo(app.getPlazo())
                                        .documento(app.getDocumento())
                                        .tipoPrestamo(app.getTipoPrestamo())
                                        .tasaInteres(app.getTasaInteres())
                                        .estadoSolicitud(app.getEstadoSolicitud())
                                        .nombreCompleto(user != null ? user.getName() + " " + user.getLastName() : null)
                                        .email(user != null ? user.getEmail() : null)
                                        .salarioBase(user != null ?
                                                (user.getBaseSalary() != null ? user.getBaseSalary().longValue() : null)
                                                : null)
                                        .build();
                            }).toList());
                });
    }
}

