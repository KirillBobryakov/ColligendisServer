package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Printer;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface PrinterRepository extends AbstractNeo4jRepository<Printer> {

    Printer findByNid(String nid);
}
