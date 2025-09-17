package co.com.crediya.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationListItemResponse {
    private Double monto;
    private Integer plazo;
    private String email;
    private String nombre;
    private String tipoPrestamo;
    private Double tasaInteres;
    private String estadoSolicitud;
    private Double salarioBase;
}
