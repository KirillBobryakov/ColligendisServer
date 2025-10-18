package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.Calendar;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.database.service.features.YearService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CalendarService extends AbstractService<Calendar, CalendarRepository> {
    private static final Logger logger = LogManager.getLogger(CalendarService.class);

    private final YearService yearService;

    public CalendarService(CalendarRepository repository, @Lazy YearService yearService) {
        super(repository);
        this.yearService = yearService;
    }

    // Start: Methods for Numista parsing
    public UUID findUuidByCode(String code) {
        return findUuidByPropertyStringValue(Calendar.LABEL, "code", code);
    }

    // Name
    public boolean compareName(UUID calendarUuid, String name) {
        return comparePropertyValue(calendarUuid, "name", name, String.class);
    }

    public void setName(UUID calendarUuid, String name) {
        setPropertyStringValue(calendarUuid, "name", name);
    }

    // To Gregorian Shift
    public boolean compareToGregorianShift(UUID calendarUuid, Integer toGregorianShift) {
        return comparePropertyValue(calendarUuid, "toGregorianShift", toGregorianShift, Integer.class);
    }

    public void setToGregorianShift(UUID calendarUuid, Integer toGregorianShift) {
        setPropertyIntValue(calendarUuid, "toGregorianShift", toGregorianShift);
    }

    // End: Methods for Numista parsing

    /**
     * Find a Gregorian Year by value. If can't - then create a year and link to
     * Gregorian Calendar
     * 
     * @param value Gregorian year value
     * @return Gregorian Year
     */
    public Year findGregorianYearByValueOrCreate(int value) {
        Year year = yearService.findYearByValueAndCalendar_Code(value, Calendar.GREGORIAN_CODE);
        if (year == null) {
            Calendar calendar = repository.findCalendarByCode(Calendar.GREGORIAN_CODE);
            assert calendar != null;

            year = new Year(value, calendar);
            year = yearService.save(year);
        }

        return year;
    }

    public Calendar findByCode(String code) {
        return repository.findCalendarByCode(code);
    }

    public Calendar findByCode(String code, String name) {
        Calendar calendar = repository.findCalendarByCode(code);
        if (calendar != null) {
            if (!calendar.getName().equals(name)) {
                logger.warn("Trying to find Calendar with code=" + code + " and name=" + name
                        + ". But there is an Calendar with the same code and other name= " + calendar.getName()
                        + "in DB already.");
                logger.warn("Calendar.name was updated.");
                calendar.setName(name);
                return repository.save(calendar);
            }
        } else {
            logger.info("New Calendar with code=" + code + " and name=" + name + " was created.");
            return repository.save(new Calendar(code, name));
        }
        return calendar;
    }

}
