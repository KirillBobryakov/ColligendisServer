package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.Calendar;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.database.service.features.YearService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CalendarService extends AbstractService<Calendar, CalendarRepository> {


    private final YearService yearService;

    public CalendarService(CalendarRepository repository, YearService yearService) {
        super(repository);
        this.yearService = yearService;
    }


    /**
     * Try to find Calendar's UUID by Calendar Numista code
     * @param code Numista Calendar code
     * @return Calendar's UUID
     */
    public UUID findCalendarUuidByCode(String code){
        String uuid = repository.findCalendarUuidByCode(code);
        return uuid != null ? UUID.fromString(uuid) : null;
    }

    /**
     * Find Gregorian Year by year in Integer value
     * @param value Year in Integer
     * @return Gregorian Year
     */
    public Year findGregorianYearByValue(int value){


        return repository.findYearUuidByValue(value, Calendar.GREGORIAN_CODE);
    }


    /**
     * Link the Year with {@code UUID} to the Calendar with {@code code} with relationship -[:TO_NUMBER_IN]->
     * @param yearUuid Year's UUID
     * @param code Calendar's code
     * @return if linked them return true
     */
    public Boolean setYearNumberInCalendar(UUID yearUuid, String code){
        return repository.setYearNumberInCalendar(yearUuid.toString(), code);
    }

    /**
     * Find a Gregorian Year by value. If can't - then create a year and link to Gregorian Calendar
     * @param value Gregorian year value
     * @return Gregorian Year
     */
    public Year findGregorianYearByValueOrCreate(int value){
        Year year = yearService.findYearByValueAndCalendarCode(value, Calendar.GREGORIAN_CODE);
        if(year == null){
            Calendar calendar = repository.findCalendarByCode(Calendar.GREGORIAN_CODE);
            assert calendar != null;

            year = new Year(value, calendar);
            year = yearService.save(year);
        }

        return year;
    }

    public Calendar findByCode(String code, String name){
        Calendar calendar = repository.findCalendarByCode(code);
        if (calendar != null) {
            if(!calendar.getName().equals(name)){
                DebugUtil.showServiceMessage(this, "Trying to find Calendar with code=" + code + " and name=" + name
                        + ". But there is an Calendar with the same code and other name= " + calendar.getName() + "in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Calendar.name was updated.");
                calendar.setName(name);
                return repository.save(calendar);
            }
        } else {
            DebugUtil.showInfo(this, "New Calendar with code=" + code + " and name=" + name + " was created.");
            return repository.save(new Calendar(code, name));
        }
        return calendar;
    }

}
