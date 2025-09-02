package co.com.crediya.r2dbc.config;


import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;



@Configuration
@EnableConfigurationProperties(PostgresqlConnectionProperties.class)
public class PostgreSQLConnectionPool {

    @Bean
    public ConnectionFactory connectionFactory(PostgresqlConnectionProperties properties) {
        return ConnectionFactories.get(
                String.format("r2dbc:postgresql://%s:%s@%s:%d/%s",
                        properties.username(),
                        properties.password(),
                        properties.host(),
                        properties.port(),
                        properties.database()
                )
        );
    }

    @Bean
    public R2dbcTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public TransactionalOperator transactionalOperator(R2dbcTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}