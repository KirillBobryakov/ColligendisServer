package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Calendar;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface CalendarRepository extends AbstractNeo4jRepository<Calendar> {

    Calendar findCalendarByCode(String code);

}
