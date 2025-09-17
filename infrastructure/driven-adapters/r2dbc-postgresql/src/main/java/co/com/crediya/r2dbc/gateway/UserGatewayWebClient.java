package co.com.crediya.r2dbc.gateway;


import co.com.crediya.model.application.gateways.UserGateway;
import co.com.crediya.model.auth.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserGatewayWebClient implements UserGateway {

    private final WebClient webClient;

    @Override
    public Flux<User> findUsersByDocuments(List<String> documents, String bearerToken) {
        if (documents == null || documents.isEmpty()) {
            log.warn("No se enviaron documentos al micro de autenticación.");
            return Flux.empty();
        }

        Map<String, Object> payload = Map.of("documents", documents);
        log.debug("Consultando usuarios con payload: {}", payload);

        return webClient.post()
                .uri("/api/v1/usuarios/documents")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error("Error 4xx al consultar usuarios: {}", response.statusCode());
                    return response.createException().flatMap(Mono::error);
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Error 5xx al consultar usuarios: {}", response.statusCode());
                    return response.createException().flatMap(Mono::error);
                })
                .bodyToFlux(User.class)
                .map(this::toDomain);
    }

    private User toDomain(User response) {
        return User.builder()
                .document(response.getDocument())
                .name(response.getName())
                .lastName(response.getLastName())
                .email(response.getEmail())
                .baseSalary(response.getBaseSalary())
                .build();
    }
}
