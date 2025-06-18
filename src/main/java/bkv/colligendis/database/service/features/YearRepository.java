package bkv.colligendis.database.service.features;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface YearRepository extends AbstractNeo4jRepository<Year> {



    Year findYearByValueAndCalendar_Code(Integer value, String calendarCode);


}
