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
public class LoanTypeEntity {
    @Id
    @Column("id_tipo_prestamo")
    private Long loanTypeId;

    @Column("nombre")
    private String name;

    @Column("monto_minimo")
    private Double minAmount;

    @Column("monto_maximo")
    private Double maxAmount;

    @Column("tasa_interes")
    private Double interestRate;

    @Column("validacion_automatica")
    private Boolean autoValidation;
}
