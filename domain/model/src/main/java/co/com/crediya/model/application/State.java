package co.com.crediya.model.application;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private Long idEstado;
    private String nombre;
    private String descripcion;
}


