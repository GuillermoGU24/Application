package co.com.crediya.model.application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationForReview {
    private Long applicationId;
    private Double monto;
    private Integer plazo;
    private String documento;
    private String nombreCompleto;
    private String email;
    private String tipoPrestamo;
    private Double tasaInteres;
    private String estadoSolicitud;
    private Long salarioBase;
    private Double montoMensualSolicitud; // <--- nuevo
}

