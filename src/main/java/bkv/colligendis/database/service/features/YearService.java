package bkv.colligendis.database.service.features;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.Calendar;
import bkv.colligendis.database.service.numista.CalendarService;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class YearService extends AbstractService<Year, YearRepository> {
    private static final Logger logger = LogManager.getLogger(YearService.class);

    private final CalendarService calendarService;

    public YearService(YearRepository repository, CalendarService calendarService) {
        super(repository);
        this.calendarService = calendarService;
    }

    // Start: Methods for Numista parsing
    public UUID findYearUuidByValueAndCalendarCode(Integer value, String calendarCode) {
        String uuidString = repository.findYearUuidByValueAndCalendarCode(value, calendarCode);
        UUID uuid = null;
        if (uuidString == null) {
            UUID calendarUuid = calendarService.findUuidByCode(calendarCode);
            if (calendarUuid == null) {
                logger.error("Can't find Calendar with code: {} while finding Year with value: {}", calendarCode,
                        value);
                return null;
            }
            uuid = save(new Year(value)).getUuid();
            setCalendar(uuid, calendarUuid);

        } else {
            uuid = UUID.fromString(uuidString);
        }
        return uuid;
    }

    public UUID findGregorianYearUuidByValue(Integer value) {
        return findYearUuidByValueAndCalendarCode(value, Calendar.GREGORIAN_CODE);
    }

    public void setCalendar(UUID yearUuid, UUID calendarUuid) {
        setSingleOutgoingRelationshipToNode(yearUuid, calendarUuid, Year.TO_NUMBER_IN, Calendar.LABEL);
    }

    // End: Methods for Numista parsing

    public Year findYearByValueAndCalendar_Code(Integer value, String calendarCode) {
        return repository.findYearByValueAndCalendar_Code(value, calendarCode);
    }

}
