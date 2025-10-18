package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CommemoratedEvent;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CommemoratedEventService extends AbstractService<CommemoratedEvent, CommemoratedEventRepository> {

    private static final Logger logger = LogManager.getLogger(CommemoratedEventService.class);

    public CommemoratedEventService(CommemoratedEventRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing
    public UUID findUuidByName(String name) {
        return findUuidByPropertyStringValue(CommemoratedEvent.LABEL, "name", name);
    }

    // End: Methods for Numista parsing

    public CommemoratedEvent findByNameOrCreate(String name) {
        CommemoratedEvent commemoratedEvent = repository.findByName(name);
        if (commemoratedEvent == null) {
            return repository.save(new CommemoratedEvent(name));
        }
        return commemoratedEvent;
    }

    public CommemoratedEvent update(CommemoratedEvent commemoratedEvent, String name) {
        if (commemoratedEvent == null || !commemoratedEvent.getName().equals(name)) {
            commemoratedEvent = repository.findByName(name);
        }
        if (commemoratedEvent == null) {
            logger.info("New CommemoratedEvent with name=" + name + " was created.");
            return repository.save(new CommemoratedEvent(name));
        }
        return commemoratedEvent;
    }

}
