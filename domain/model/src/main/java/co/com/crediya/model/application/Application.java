package co.com.crediya.model.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    private Long idSolicitud;
    private Double monto;
    private Integer plazo;
    private String documento;
    private Long idEstado;
    private Long idTipoPrestamo;

}

