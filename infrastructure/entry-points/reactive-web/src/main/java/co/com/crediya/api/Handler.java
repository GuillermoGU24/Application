package co.com.crediya.api;

import co.com.crediya.api.dto.SolicitudRequest;
import co.com.crediya.api.mapper.SolicitudMapper;

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


    private final RegisterApplicationUseCase registrarSolicitudUseCase;
    private final SolicitudMapper solicitudMapper;
    private final Validator validator;

    public Mono<ServerResponse> registrar(ServerRequest request) {
        return request.bodyToMono(SolicitudRequest.class)
                .flatMap(req -> ValidationUtil.validate(req, validator) // validar DTO
                        .thenReturn(req))
                .map(solicitudMapper::toDomain)
                .flatMap(registrarSolicitudUseCase::create)
                .map(solicitudMapper::toResponse)
                .flatMap(saved -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "status", 201,
                                "message", "Solicitud Creada con exito"
                        )));
    }
}

