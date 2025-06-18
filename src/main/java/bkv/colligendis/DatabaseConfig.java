package bkv.colligendis;

import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.DatabaseSelection;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.Neo4jRepositoryConfigurationExtension;
import org.springframework.data.neo4j.repository.config.ReactiveNeo4jRepositoryConfigurationExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    /**
     * Neo4j version-aware database selector.
     *
     * This is only needed for applications running with both Neo4j versions 3 (where multitenancy is not available) and 4.
     *
     * Ideally, one would run instead (where driver is an instance of org.neo4j.driver.Driver):
     * <code>
     *   String neo4jVersion = driver.session().run("RETURN 1").consume().server().version();
     * </code>
     * ... but this requires permissions that the user configured by default does not have.
     *
     * @param database the configured database name
     * @return DatabaseSelection the corresponding database name for Neo4j 4+ or undefined otherwise
     */
    @Bean
    DatabaseSelectionProvider databaseSelectionProvider(@Value("${spring.data.neo4j.database}") String database) {
        return () -> {
            String neo4jVersion = System.getenv("NEO4J_VERSION");
            if (neo4jVersion == null || neo4jVersion.startsWith("4")) {
                return DatabaseSelection.byName(database);
            }
            return DatabaseSelection.undecided();
        };
    }

    @Bean(Neo4jRepositoryConfigurationExtension.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
    public TransactionManager reactiveTransactionManager(Driver driver,
                                                         DatabaseSelectionProvider databaseNameProvider) {
        return new Neo4jTransactionManager(driver, databaseNameProvider);
    }


}
