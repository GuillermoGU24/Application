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
public class ApplicationEntity {
    @Id
    @Column("id_solicitud")
    private Long applicationId;

    @Column("monto")
    private Double amount;

    @Column("plazo")
    private Integer term;

    @Column("documento")
    private String document;

    @Column("id_estado")
    private Long stateId;

    @Column("id_tipo_prestamo")
    private Long loanTypeId;

}
