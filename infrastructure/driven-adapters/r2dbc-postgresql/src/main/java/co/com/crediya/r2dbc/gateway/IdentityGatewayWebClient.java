package co.com.crediya.r2dbc.gateway;

import co.com.crediya.model.application.gateways.IdentityGateway;
import co.com.crediya.model.auth.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Repository
public class IdentityGatewayWebClient implements IdentityGateway {

    private final WebClient authWebClient;

    public IdentityGatewayWebClient(WebClient authWebClient) {
        this.authWebClient = authWebClient;
    }

    @Override
    public Mono<AuthUser> findAuthUserByToken(String bearerToken) {
        return authWebClient.get()
                .uri("/api/v1/me")
                .header("Authorization", bearerToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        Mono.error(new IllegalArgumentException("authorization: Invalid or missing token")))
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                        Mono.error(new IllegalStateException("client: Authentication service error")))
                .bodyToMono(AuthUser.class);

    }
}
