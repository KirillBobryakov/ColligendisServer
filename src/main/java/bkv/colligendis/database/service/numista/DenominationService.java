package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Calendar;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class CalendarService extends AbstractService<Calendar, CalendarRepository> {
    public CalendarService(CalendarRepository repository) {
        super(repository);
    }


    public Calendar findByCode(String code, String name){
        Calendar calendar = repository.findByCode(code);
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

    @Override
    public Calendar setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
