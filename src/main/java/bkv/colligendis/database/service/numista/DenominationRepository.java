package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Calendar;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import reactor.core.publisher.Mono;

public interface CalendarRepository extends AbstractNeo4jRepository<Calendar> {

    Calendar findByCode(String code);


}
