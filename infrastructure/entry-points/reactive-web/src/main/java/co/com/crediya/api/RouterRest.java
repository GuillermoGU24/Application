package co.com.crediya.api;

import co.com.crediya.api.dto.ApplicationForReviewResponse;
import co.com.crediya.api.dto.ApplicationRequest;
import co.com.crediya.api.dto.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Solicitudes", description = "Operaciones sobre solicitudes de crédito")
public class RouterRest {

    @Bean
    @RouterOperations({

            // GET /api/v1/solicitud/pendientes
            @RouterOperation(
                    path = "/api/v1/solicitud/pendientes",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "listAllPending",
                            summary = "Listar solicitudes pendientes",
                            description = "Obtiene todas las solicitudes que están en estado pendiente de revisión.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de solicitudes pendientes",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ApplicationForReviewResponse.class)
                                            )
                                    )
                            }
                    )
            ),

            // GET /api/v1/solicitud
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "listForReview",
                            summary = "Listar solicitudes para revisión",
                            description = "Devuelve solicitudes filtradas por estados para revisión, paginadas.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de solicitudes para revisión",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ApplicationForReviewResponse.class)
                                            )
                                    )
                            }
                    )
            ),

            // POST /api/v1/solicitud
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "register",
                            summary = "Registrar nueva solicitud",
                            description = "Crea una nueva solicitud de crédito en el sistema.",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    description = "Datos de la solicitud",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = ApplicationRequest.class),
                                            examples = @ExampleObject(
                                                    name = "Nueva solicitud",
                                                    value = "{ \"document\": \"123456789\", \"amount\": 5000, \"term\": 12, \"loanTypeId\": 1 }"
                                            )
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Solicitud registrada correctamente",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ApplicationResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos inválidos en la solicitud",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    examples = @ExampleObject(
                                                            name = "Error de validación",
                                                            value = "{ \"status\": 400, \"error\": \"El monto debe ser mayor a 0\" }"
                                                    )
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routes(Handler handler) {
        return route(GET("/api/v1/solicitud/pendientes"), handler::listAllPending)
                .andRoute(GET("/api/v1/solicitud"), handler::listForReview)
                .andRoute(POST("/api/v1/solicitud"), handler::register);
    }
}
