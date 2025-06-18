package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CommemoratedEvent;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class CommemoratedEventService extends AbstractService<CommemoratedEvent, CommemoratedEventRepository> {

    public CommemoratedEventService(CommemoratedEventRepository repository) {
        super(repository);
    }


    public CommemoratedEvent findByNameOrCreate(String name){
        CommemoratedEvent commemoratedEvent = repository.findByName(name);
        if(commemoratedEvent == null){
            return repository.save(new CommemoratedEvent(name));
        }
        return commemoratedEvent;
    }


    public CommemoratedEvent update(CommemoratedEvent commemoratedEvent, String name){
        if(commemoratedEvent == null || !commemoratedEvent.getName().equals(name)) {
            commemoratedEvent = repository.findByName(name);
        }
        if (commemoratedEvent == null) {
            DebugUtil.showInfo(this, "New CommemoratedEvent with name=" + name + " was created.");
            return repository.save(new CommemoratedEvent(name));
        }
        return commemoratedEvent;
    }


}
