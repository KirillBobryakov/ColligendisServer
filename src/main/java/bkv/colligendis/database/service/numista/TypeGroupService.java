package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CollectibleTypeGroup;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class TypeGroupService extends AbstractService<CollectibleTypeGroup, CollectibleTypeGroupRepository> {

    private static final Logger logger = LogManager.getLogger(TypeGroupService.class);

    public TypeGroupService(CollectibleTypeGroupRepository repository) {
        super(repository);
    }

    public CollectibleTypeGroup findByName(String name) {
        return repository.findByName(name);
    }

    public CollectibleTypeGroup update(String name) {
        CollectibleTypeGroup typeGroup = repository.findByName(name);

        if (typeGroup == null) {
            typeGroup = repository.save(new CollectibleTypeGroup(name));
            logger.info("New CollectibleTypeGroup with name=" + name + " was created.");
            return typeGroup;
        } else {
            if (!typeGroup.getName().equals(name)) {
                logger.info("CollectibleTypeGroup has stale name=" + name);
                typeGroup.setName(name);
                typeGroup = repository.save(typeGroup);
                logger.info("CollectibleTypeGroup with a new name=" + name);
                return typeGroup;
            }
        }

        return typeGroup;
    }

}
