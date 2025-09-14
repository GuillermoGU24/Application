package co.com.crediya.model.application.gateways;

import co.com.crediya.model.auth.AuthUser;
import reactor.core.publisher.Mono;

public interface IdentityGateway {
    Mono<AuthUser> findAuthUserByToken(String bearerToken);
}

