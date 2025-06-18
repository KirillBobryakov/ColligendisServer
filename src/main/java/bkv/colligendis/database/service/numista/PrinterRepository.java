package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Printer;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import reactor.core.publisher.Mono;

public interface PrinterRepository extends AbstractNeo4jRepository<Printer> {

    Printer findByNid(String nid);
}
