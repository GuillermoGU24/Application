package co.com.crediya.r2dbc.gateway;

import co.com.crediya.model.application.gateways.IdentityGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Repository
public class IdentityGatewayWebClient implements IdentityGateway {

    private final WebClient authWebClient;

    public IdentityGatewayWebClient(WebClient authWebClient) {
        this.authWebClient = authWebClient;
    }

    @Override
    public Mono<Boolean> existsByDocument(String document) {
        log.info("Validando cliente por documento {}", document);

        return authWebClient.get()
                .uri("/api/v1/usuarios/document/{document}", document)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        resp -> Mono.error(new IllegalArgumentException("cliente: Cliente no encontrado")))
                .onStatus(status -> status.is5xxServerError(),
                        resp -> Mono.error(new IllegalStateException("cliente: Error en servicio de autenticación")))
                .bodyToMono(Object.class)
                .doOnNext(u -> log.info("Cliente validado: {}", document))
                .map(u -> true)  // Convert to Boolean since method should return Mono<Boolean>
                .onErrorReturn(IllegalArgumentException.class, false); // Return false if client not found
    }
}