package co.com.crediya.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SolicitudRequest {

    @NotNull(message = "Monto is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monto must be greater than 0")
    private Double monto;

    @NotNull(message = "Plazo is required")
    @Min(value = 1, message = "Plazo must be at least 1 month")
    private Integer plazo;

    @NotBlank(message = "Documento is required")
    private String documento;


    @NotNull(message = "Tipo de préstamo is required")
    private Long idTipoPrestamo;
}
