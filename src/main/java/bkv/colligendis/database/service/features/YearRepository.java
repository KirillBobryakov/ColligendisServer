package bkv.colligendis.database.service.features;

import org.springframework.data.neo4j.repository.query.Query;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface YearRepository extends AbstractNeo4jRepository<Year> {

    Year findYearByValueAndCalendar_Code(Integer value, String calendarCode);

    @Query("MATCH (n:YEAR)-[:TO_NUMBER_IN]->(c:CALENDAR) WHERE n.value = $value AND c.code = $calendarCode RETURN n.uuid")
    String findYearUuidByValueAndCalendarCode(Integer value, String calendarCode);
}
