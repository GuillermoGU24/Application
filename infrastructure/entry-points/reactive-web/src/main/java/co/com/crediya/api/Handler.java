package co.com.crediya.api;

import co.com.crediya.api.dto.ApplicationListItemResponse;
import co.com.crediya.api.dto.ApplicationRequest;
import co.com.crediya.api.dto.PagedResponse;
import co.com.crediya.api.mapper.ApplicationMapper;
import co.com.crediya.api.util.ValidationUtil;
import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.usecase.Review.ListApplicationsForReviewUseCase;
import co.com.crediya.usecase.Review.ListPendingApplicationsUseCase;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final RegisterApplicationUseCase registerApplicationUseCase;
    private final ListApplicationsForReviewUseCase listApplicationsForReviewUseCase;
    private final ListPendingApplicationsUseCase listPendingApplicationsUseCase;
    private final ApplicationMapper applicationMapper;
    private final Validator validator;

    public Mono<ServerResponse> register(ServerRequest request) {
        String bearerToken = request.headers().firstHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }
        return request.bodyToMono(ApplicationRequest.class)
                .flatMap(req -> ValidationUtil.validate(req, validator))
                .map(applicationMapper::toDomain)
                .flatMap(app -> registerApplicationUseCase.register(app, bearerToken))
                .map(applicationMapper::toResponse)
                .flatMap(saved -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "status", 201,
                                "message", "Application created successfully"
                        )));
    }

    public Mono<ServerResponse> listForReview(ServerRequest request) {
        String bearerToken = request.headers().firstHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }

        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("20"));
        List<String> estados = request.queryParam("estados")
                .map(s -> Arrays.asList(s.split(",")))
                .orElse(List.of("Pendiente de revisión", "Rechazadas", "Revision manual"));

        return listApplicationsForReviewUseCase.listForUser(bearerToken, estados, page, size)
                .flatMap(result -> {
                    List<ApplicationListItemResponse> items = result.items().stream()
                            .map(applicationMapper::toListItemResponse)
                            .toList();

                    PagedResponse<ApplicationListItemResponse> response =
                            PagedResponse.<ApplicationListItemResponse>builder()
                                    .items(items)
                                    .total(result.total())
                                    .page(result.page())
                                    .size(result.size())
                                    .build();

                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("status", 403, "error", "forbidden", "message", e.getMessage()))
                );
    }

    public Mono<ServerResponse> listAllPending(ServerRequest request) {
        String bearerToken = request.headers().firstHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .bodyValue(Map.of("status", 401, "message", "Missing Bearer token"));
        }

        log.info("Listando todas las solicitudes con estado = 1 (pendientes)");

        return listPendingApplicationsUseCase.listAllPending(bearerToken)
                .flatMap(apps -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(apps))
                .doOnError(e -> log.error("Error listando pendientes: {}", e.getMessage(), e))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Map.of("status", 400, "message", e.getMessage())));
    }


}
