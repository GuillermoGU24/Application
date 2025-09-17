package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.ApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Repository
public interface ApplicationReactiveRepository extends ReactiveCrudRepository<ApplicationEntity, Long>, ReactiveQueryByExampleExecutor<ApplicationEntity> {

    @Query("""
            SELECT s.id_solicitud, s.monto, s.plazo, s.documento,
                   t.nombre as tipo_nombre, t.tasa_interes,
                   e.nombre as estado_nombre
            FROM solicitud s
            LEFT JOIN tipo_prestamo t ON t.id_tipo_prestamo = s.id_tipo_prestamo
            LEFT JOIN estados e ON e.id_estado = s.id_estado
            WHERE e.nombre IN (:estados)
            ORDER BY s.id_solicitud DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<Map<String, Object>> findByStatesPaged(List<String> estados, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM solicitud s
            LEFT JOIN estados e ON e.id_estado = s.id_estado
            WHERE e.nombre = ANY(:estados)
            """)
    Mono<Long> countByStates(List<String> estados);

}
