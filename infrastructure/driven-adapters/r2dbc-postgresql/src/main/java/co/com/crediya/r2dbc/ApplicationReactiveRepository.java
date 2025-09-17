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
        SELECT COUNT(*)
        FROM solicitud s
        LEFT JOIN estados e ON e.id_estado = s.id_estado
        WHERE e.nombre = ANY(:estados)
        """)
    Mono<Long> countByStates(String[] estados);



}
