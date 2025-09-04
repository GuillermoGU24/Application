package co.com.crediya.model.application.gateways;

import reactor.core.publisher.Mono;

public interface IdentityGateway {
    Mono<Boolean> existsByDocument(String document,String bearerToke);
}
