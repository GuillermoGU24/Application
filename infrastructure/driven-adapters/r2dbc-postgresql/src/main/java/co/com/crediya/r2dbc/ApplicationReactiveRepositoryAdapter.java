package co.com.crediya.r2dbc;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.ApplicationForReview;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.r2dbc.entity.ApplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class ApplicationReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<Application, ApplicationEntity, Long, ApplicationReactiveRepository>
        implements ApplicationRepository {

    private final TransactionalOperator tx;
    private final ApplicationReactiveRepository reactiveRepository;
    private final DatabaseClient databaseClient;


    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository repository,
                                                ObjectMapper mapper,
                                                TransactionalOperator tx, DatabaseClient databaseClient) {
        super(repository, mapper, entity -> mapper.map(entity, Application.class));
        this.reactiveRepository = repository;
        this.tx = tx;
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Application> save(Application application) {
        ApplicationEntity entity = ApplicationEntity.builder()
                .applicationId(application.getApplicationId())
                .amount(application.getAmount())
                .term(application.getTerm())
                .document(application.getDocument())
                .stateId(application.getState() != null ? application.getState().getStateId() : null)
                .loanTypeId(application.getLoanType() != null ? application.getLoanType().getLoanTypeId() : null)
                .build();

        return tx.transactional(
                reactiveRepository.save(entity)
                        .map(saved -> mapper.map(saved, Application.class))
        );
    }

    @Override
    public Flux<ApplicationForReview> findForReview(List<String> estados, int page, int size) {
        int limit = size;
        long offset = (long) page * size;

        return reactiveRepository.findByStatesPaged(estados, limit, offset)
                .map(row -> ApplicationForReview.builder()
                        .applicationId(((Number) row.get("id_solicitud")).longValue())
                        .monto(((Number) row.get("monto")).doubleValue())
                        .plazo(((Number) row.get("plazo")).intValue())
                        .documento((String) row.get("documento"))
                        .tipoPrestamo((String) row.get("tipo_nombre"))
                        .tasaInteres(row.get("tasa_interes") != null ? ((Number) row.get("tasa_interes")).doubleValue() : null)
                        .estadoSolicitud((String) row.get("estado_nombre"))
                        .build());
    }

    @Override
    public Mono<Long> countForReview(List<String> estados) {
        return reactiveRepository.countByStates(estados);
    }

    @Override
    public Flux<ApplicationForReview> findByStateId(Long stateId) {
        String sql = """
            SELECT 
                s.id_solicitud,
                s.monto,
                s.plazo,
                s.documento,
                t.nombre AS tipo_prestamo,
                t.tasa_interes,
                e.nombre AS estado_solicitud
            FROM solicitud s
            LEFT JOIN tipo_prestamo t ON t.id_tipo_prestamo = s.id_tipo_prestamo
            LEFT JOIN estados e ON e.id_estado = s.id_estado
            WHERE s.id_estado = :stateId
            ORDER BY s.id_solicitud DESC
        """;

        return databaseClient.sql(sql)
                .bind("stateId", stateId)
                .map((row, metadata) -> ApplicationForReview.builder()
                        .applicationId(row.get("id_solicitud", Long.class))
                        .monto(row.get("monto", Double.class))
                        .plazo(row.get("plazo", Integer.class))
                        .documento(row.get("documento", String.class))
                        .tipoPrestamo(row.get("tipo_prestamo", String.class))
                        .tasaInteres(row.get("tasa_interes", Double.class))
                        .estadoSolicitud(row.get("estado_solicitud", String.class))
                        .build())
                .all();
    }



}
