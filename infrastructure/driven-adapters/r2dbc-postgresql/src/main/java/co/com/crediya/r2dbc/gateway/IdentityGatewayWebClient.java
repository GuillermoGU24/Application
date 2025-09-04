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
    public Mono<Boolean> existsByDocument(String document, String bearerToken) {
        log.info("Validating client by document {}", document);

        return authWebClient.get()
                .uri("/api/v1/usuarios/document/{document}", document)
                .header("Authorization", bearerToken)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new IllegalArgumentException(body))))
                .onStatus(status -> status.is5xxServerError(),
                        resp -> Mono.error(new IllegalStateException("client: Authentication service error")))
                .bodyToMono(Object.class)
                .doOnNext(u -> log.info("Client validated: {}", document))
                .map(u -> true);
    }

}
