package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CountryRepository extends AbstractNeo4jRepository<Country> {

    Mono<Country> findByName(String name);

    Flux<Country> findByNameContainingIgnoreCase(String nameFilter);

    @Query("MATCH (n:COUNTRY) RETURN n ORDER BY n.name")
    Flux<Country> findAllWithSort();

}
