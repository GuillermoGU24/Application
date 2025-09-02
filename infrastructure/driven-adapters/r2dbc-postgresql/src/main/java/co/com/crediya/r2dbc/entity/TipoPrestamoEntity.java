package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("tipo_prestamo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoPrestamoEntity {
    @Id
    @Column("id_tipo_prestamo")
    private Long idTipoPrestamo;

    private String nombre;

    @Column("monto_minimo")
    private Double montoMinimo;

    @Column("monto_maximo")
    private Double montoMaximo;

    @Column("tasa_interes")
    private Double tasaInteres;

    @Column("validacion_automatica")
    private Boolean validacionAutomatica;
}
