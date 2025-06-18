package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CurrencyRepository extends AbstractNeo4jRepository<Currency> {

    Currency findByFullName(String fullName);
    Currency findByName(String name);


    /**
     * Find Currency's nid by Currency's uuid
     * @param uuid Currency's uuid
     * @return Currency's nid
     */
    @Query("MATCH (n:CURRENCY) WHERE n.uuid = $uuid RETURN n.nid")
    String findCurrencyNidByUuid(String uuid);

    Currency findCurrencyByNid(String nid);

    List<Currency> findByIssuerCode(String issuerCode);

}
