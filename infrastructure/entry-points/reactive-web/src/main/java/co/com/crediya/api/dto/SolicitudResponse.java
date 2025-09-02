package co.com.crediya.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolicitudResponse {
    private Long idSolicitud;
    private Double monto;
    private Integer plazo;
    private String documento;
    private String estado;
    private String tipoPrestamo;
}
