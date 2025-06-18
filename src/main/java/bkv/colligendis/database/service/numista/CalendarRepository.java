package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.Calendar;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface CalendarRepository extends AbstractNeo4jRepository<Calendar> {

    Calendar findCalendarByCode(String code);


    /**
     * Try to find Calendar's UUID by Calendar Numista code
     * @param code Numista Calendar code
     * @return Calendar's UUID in String value
     */
    @Query("MATCH (c:CALENDAR) WHERE c.code = $code RETURN c")
    String findCalendarUuidByCode(String code);


    /**
     * Try to find Year by Year's value and Calendar's code
     * @param value year in Integer value
     * @param calendarCode Calendar code (for example "gregorien" code for Gregorian Calendar)
     * @return Year
     */
    @Query("MATCH (n:YEAR)-[:TO_NUMBER_IN]->(c:CALENDAR) WHERE n.value = $value AND c.code = $calendarCode RETURN n")
    Year findYearUuidByValue(Integer value, String calendarCode);



    /**
     * Link the Year with {@code yearUuid} to the Calendar with {@code code} with relationship -[:TO_NUMBER_IN]->
     * @param yearUuid Year's UUID in String value
     * @param code Calendar's code
     * @return if linked them return true
     */
    //todo If a part of a query contains multiple disconnected patterns, this will build a cartesian product between all those parts.
    // This may produce a large amount of data and slow down query processing.
    // While occasionally intended, it may often be possible to reformulate the query that avoids the use of this cross product,
    // perhaps by adding a relationship between the different parts or by using OPTIONAL MATCH (identifier is: (c))
    @Query("MATCH (y:YEAR), (c:CALENDAR) WHERE y.uuid = $yearUuid AND c.code = $code CREATE (y)-[rel:TO_NUMBER_IN]->(c) RETURN count(rel) > 0")
    Boolean setYearNumberInCalendar(String yearUuid, String code);

}
