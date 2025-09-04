package co.com.crediya.api.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        if (ex instanceof ConstraintViolationException) {
            return handleConstraintViolation(exchange, (ConstraintViolationException) ex);
        }

        if (ex instanceof WebExchangeBindException) {
            return handleValidationErrors(exchange, (WebExchangeBindException) ex);
        }

        if (ex instanceof ServerWebInputException) {
            return handleInvalidFormat(exchange, (ServerWebInputException) ex);
        }

        if (ex instanceof IllegalArgumentException) {
            return handleIllegalArgument(exchange, (IllegalArgumentException) ex);
        }

        if (ex instanceof org.springframework.dao.DuplicateKeyException dke) {
            return handleDuplicateKey(exchange, dke);
        }

        if (ex instanceof R2dbcDataIntegrityViolationException rdv) {
            return handleDuplicateKey(exchange, new org.springframework.dao.DuplicateKeyException(rdv.getMessage()));
        }
        if (ex instanceof IllegalStateException) {
            return handleIllegalState(exchange, (IllegalStateException) ex);
        }
        log.error("Unhandled exception: ", ex);
        return writeErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
                Map.of("status", 500, "error", "Internal Server Error"));
    }

    private Mono<Void> handleIllegalState(ServerWebExchange exchange, IllegalStateException ex) {
        log.error("Illegal state error: {}", ex.getMessage());

        String field = "general";
        String message = ex.getMessage();

        if (message != null && message.contains(":")) {
            String[] parts = message.split(":", 2);
            field = parts[0].trim();
            message = parts[1].trim();
        }

        HttpStatus status;
        switch (field) {
            case "authorization" -> status = HttpStatus.UNAUTHORIZED; // 401
            case "forbidden" -> status = HttpStatus.FORBIDDEN;       // 403
            case "client" -> status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
            default -> status = HttpStatus.BAD_REQUEST;
        }

        Map<String, Object> errorResponse = Map.of(
                "status", status.value(),
                "error", "Upstream service error",
                "details", List.of(Map.of(
                        "field", field,
                        "message", message
                ))
        );

        return writeErrorResponse(exchange, status, errorResponse);
    }


    private Mono<Void> handleConstraintViolation(ServerWebExchange exchange, ConstraintViolationException ex) {
        log.error("Constraint validation error: {}", ex.getMessage());

        List<Map<String, String>> details = ex.getConstraintViolations().stream()
                .map(violation -> Map.of(
                        "field", violation.getPropertyPath().toString(),
                        "message", violation.getMessage()
                ))
                .collect(Collectors.toList());

        Map<String, Object> errorResponse = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation failed",
                "details", details
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> handleValidationErrors(ServerWebExchange exchange, WebExchangeBindException ex) {
        log.error("Binding validation error: {}", ex.getMessage());

        List<Map<String, String>> details = ex.getFieldErrors().stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "message", err.getDefaultMessage() != null ? err.getDefaultMessage() : "Validation error"
                ))
                .collect(Collectors.toList());

        Map<String, Object> errorResponse = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation failed",
                "details", details
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> handleInvalidFormat(ServerWebExchange exchange, ServerWebInputException ex) {
        log.error("Invalid input format", ex);

        String fieldName = "field";
        String message = "Invalid format";

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            if (!ife.getPath().isEmpty()) {
                fieldName = ife.getPath().get(0).getFieldName();
            }
            message = String.format("Value '%s' is not valid for field %s. Expected type: %s",
                    ife.getValue(),
                    fieldName,
                    ife.getTargetType().getSimpleName());
        }

        Map<String, Object> errorResponse = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Invalid request format",
                "details", List.of(Map.of("message", message))
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> handleIllegalArgument(ServerWebExchange exchange, IllegalArgumentException ex) {
        log.error("Illegal argument error: {}", ex.getMessage());

        String message = ex.getMessage();

        Map<String, Object> errorResponse;

        // Si el mensaje empieza con { y contiene "status", asumimos que ya es JSON de otro micro
        if (message != null && message.trim().startsWith("{") && message.contains("\"status\"")) {
            try {
                // Parsear el JSON original y devolverlo tal cual
                errorResponse = objectMapper.readValue(message, Map.class);
                return writeErrorResponse(exchange, HttpStatus.valueOf((Integer) errorResponse.get("status")), errorResponse);
            } catch (Exception parseEx) {
                log.error("Error parsing forwarded JSON error", parseEx);
                // fallback a formato estándar
            }
        }

        // comportamiento original (tu formato de Domain validation failed)
        String field = "general";
        if (message != null && message.contains(":")) {
            String[] parts = message.split(":", 2);
            field = parts[0].trim();
            message = parts[1].trim();
        }

        errorResponse = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Domain validation failed",
                "details", List.of(Map.of(
                        "field", field,
                        "message", message
                ))
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> handleDuplicateKey(ServerWebExchange exchange,
                                          org.springframework.dao.DuplicateKeyException ex) {
        log.error("Duplicate key error: {}", ex.getMessage());

        String field = "general";
        String message = "Duplicate key violation";

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("solicitud_documento_key")) {
                field = "documento";
                message = "Document already exists";
            }
        }

        Map<String, Object> errorResponse = Map.of(
                "status", HttpStatus.CONFLICT.value(),
                "error", "Data integrity violation",
                "details", List.of(Map.of(
                        "field", field,
                        "message", message
                ))
        );

        return writeErrorResponse(exchange, HttpStatus.CONFLICT, errorResponse);
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, Map<String, Object> errorResponse) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(
                Mono.fromSupplier(() -> {
                    try {
                        byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
                        return exchange.getResponse().bufferFactory().wrap(bytes);
                    } catch (JsonProcessingException e) {
                        log.error("Error serializing error response", e);
                        return exchange.getResponse().bufferFactory()
                                .wrap("{\"status\":500,\"error\":\"Internal Server Error\"}".getBytes(StandardCharsets.UTF_8));
                    }
                })
        );
    }
}
