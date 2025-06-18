package bkv.colligendis.database.service.features;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class YearService extends AbstractService<Year, YearRepository> {
    public YearService(YearRepository repository) {
        super(repository);
    }

    public Year findYearByValueAndCalendarCode(Integer value, String calendarCode){
        return repository.findYearByValueAndCalendar_Code(value, calendarCode);
    }


}
