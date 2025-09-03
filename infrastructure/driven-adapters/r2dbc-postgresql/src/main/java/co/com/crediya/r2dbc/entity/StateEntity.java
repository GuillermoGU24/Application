package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("estados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateEntity {
    @Id
    @Column("id_estado")
    private Long stateId;

    @Column("nombre")
    private String name;

    @Column("descripcion")
    private String description;
}
