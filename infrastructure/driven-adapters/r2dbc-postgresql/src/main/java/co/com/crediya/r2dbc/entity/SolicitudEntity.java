package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("solicitud")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEntity {
    @Id
    @Column("id_solicitud")
    private Long idSolicitud;

    private Double monto;
    private Integer plazo;
    private String documento;

    @Column("id_estado")
    private Long idEstado;

    @Column("id_tipo_prestamo")
    private Long idTipoPrestamo;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;
}
