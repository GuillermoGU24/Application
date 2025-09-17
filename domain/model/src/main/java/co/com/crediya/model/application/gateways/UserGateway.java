package co.com.crediya.model.application.gateways;

import co.com.crediya.model.auth.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserGateway {
    Flux<User> findUsersByDocuments(List<String> documents, String bearerToken);
}

