package co.com.crediya.r2dbc;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.ApplicationForReview;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.r2dbc.entity.ApplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Repository
public class ApplicationReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<Application, ApplicationEntity, Long, ApplicationReactiveRepository>
        implements ApplicationRepository {

    private final TransactionalOperator tx;
    private final ApplicationReactiveRepository reactiveRepository;
    private final DatabaseClient databaseClient;

    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository repository,
                                                ObjectMapper mapper,
                                                TransactionalOperator tx,
                                                DatabaseClient databaseClient) {
        super(repository, mapper, entity -> mapper.map(entity, Application.class));
        this.reactiveRepository = repository;
        this.tx = tx;
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Application> save(Application application) {
        log.debug("Saving application with document: {}", application.getDocument());

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
                        .doOnSuccess(saved -> log.info("Application saved successfully with id: {}", saved.getApplicationId()))
                        .doOnError(e -> log.error("Error saving application: {}", e.getMessage(), e))
                        .map(saved -> mapper.map(saved, Application.class))
        );
    }

    @Override
    public Flux<ApplicationForReview> findForReview(List<String> states, int page, int size) {
        long offset = (long) page * size;
        log.debug("Fetching applications for review with states: {}, page: {}, size: {}", states, page, size);

        String sql = """
        SELECT s.id_solicitud, s.monto, s.plazo, s.documento,
               t.nombre as tipo_nombre, t.tasa_interes,
               e.nombre as estado_nombre
        FROM solicitud s
        LEFT JOIN tipo_prestamo t ON t.id_tipo_prestamo = s.id_tipo_prestamo
        LEFT JOIN estados e ON e.id_estado = s.id_estado
        WHERE e.nombre = ANY(:estados)
        ORDER BY s.id_solicitud DESC
        LIMIT :limit OFFSET :offset
        """;

        return databaseClient.sql(sql)
                .bind("estados", states.toArray(new String[0]))
                .bind("limit", size)
                .bind("offset", offset)
                .map((row, meta) -> ApplicationForReview.builder()
                        .applicationId(row.get("id_solicitud", Long.class))
                        .amount(row.get("monto", Double.class))
                        .term(row.get("plazo", Integer.class))
                        .document(row.get("documento", String.class))
                        .loanType(row.get("tipo_nombre", String.class))
                        .interestRate(row.get("tasa_interes", Double.class))
                        .applicationStatus(row.get("estado_nombre", String.class))
                        .build()
                )
                .all()
                .doOnComplete(() -> log.info("Finished fetching applications for review."))
                .doOnError(e -> log.error("Error fetching applications for review: {}", e.getMessage(), e));
    }

    @Override
    public Mono<Long> countForReview(List<String> states) {
        log.debug("Counting applications for review with states: {}", states);
        String[] estadosArray = states.toArray(new String[0]);

        return reactiveRepository.countByStates(estadosArray)
                .doOnSuccess(count -> log.info("Found {} applications for review.", count))
                .doOnError(e -> log.error("Error counting applications for review: {}", e.getMessage(), e));
    }

    @Override
    public Flux<ApplicationForReview> findByStateId(Long stateId) {
        log.debug("Fetching applications by stateId: {}", stateId);

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
                        .amount(row.get("monto", Double.class))
                        .term(row.get("plazo", Integer.class))
                        .document(row.get("documento", String.class))
                        .loanType(row.get("tipo_prestamo", String.class))
                        .interestRate(row.get("tasa_interes", Double.class))
                        .applicationStatus(row.get("estado_solicitud", String.class))
                        .build())
                .all()
                .doOnComplete(() -> log.info("Finished fetching applications for stateId={}", stateId))
                .doOnError(e -> log.error("Error fetching applications by stateId {}: {}", stateId, e.getMessage(), e));
    }
}
