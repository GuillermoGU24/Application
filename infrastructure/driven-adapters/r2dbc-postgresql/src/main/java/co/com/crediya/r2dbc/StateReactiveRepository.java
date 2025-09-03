package co.com.crediya.r2dbc;


import co.com.crediya.r2dbc.entity.StateEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateReactiveRepository extends ReactiveCrudRepository<StateEntity, Long>, ReactiveQueryByExampleExecutor<StateEntity> {
}
