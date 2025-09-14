package co.com.crediya.model.auth.gateway;

import co.com.crediya.model.auth.AuthUser;
import reactor.core.publisher.Mono;

public interface AuthUserRepository {
    Mono<AuthUser> findByEmail(String email);
}
