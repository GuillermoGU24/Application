package co.com.crediya.api;

import co.com.crediya.api.dto.ApplicationRequest;
import co.com.crediya.api.mapper.ApplicationMapper;
import co.com.crediya.api.util.ValidationUtil;
import co.com.crediya.usecase.registerapplication.RegisterApplicationUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final RegisterApplicationUseCase registerApplicationUseCase;
    private final ApplicationMapper applicationMapper;
    private final Validator validator;

    public Mono<ServerResponse> register(ServerRequest request) {
        String bearerToken = request.headers().firstHeader("Authorization");

        return request.bodyToMono(ApplicationRequest.class)
                .flatMap(req -> ValidationUtil.validate(req, validator))
                .map(applicationMapper::toDomain)
                .flatMap(app -> registerApplicationUseCase.create(app, bearerToken))
                .map(applicationMapper::toResponse)
                .flatMap(saved -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "status", 201,
                                "message", "Application created successfully"
                        )));
    }

}
