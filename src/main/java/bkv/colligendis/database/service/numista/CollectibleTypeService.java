package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CollectibleTypeService extends AbstractService<CollectibleType, TypeRepository> {

    private static final Logger logger = LogManager.getLogger(CollectibleTypeService.class);

    public CollectibleTypeService(TypeRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing

    public UUID findUuidByCode(String code) {
        return findUuidByPropertyStringValue(CollectibleType.LABEL, "code", code);
    }

    public String getCode(UUID uuid) {
        return getPropertyValue(uuid, "code", String.class);
    }

    public UUID findTopCollectibleTypeUuid(UUID uuid) {
        String topCollectibleTypeUuid = repository.findTopCollectibleTypeUuid(uuid.toString());
        return topCollectibleTypeUuid != null ? UUID.fromString(topCollectibleTypeUuid) : null;
    }

    // End: Methods for Numista parsing

    public CollectibleType findByCode(String code) {
        return repository.findByCode(code);
    }

    /**
     * Find a CollectibleType of NType by NType's nid
     * 
     * @param nid NType's nid
     * @return CollectibleType of NType if exists, or null
     */
    public CollectibleType findByNTypeNid(String nid) {
        return repository.findByNTypeNid(nid);
    }

    public CollectibleType update(CollectibleType collectibleType, String code, String name,
            CollectibleType collectibleTypeParent) {
        if (collectibleType == null && code != null && !code.isEmpty()) {
            collectibleType = repository.findByCode(code);
        }

        if (collectibleType != null) {
            if (name != null && !name.isEmpty()) {
                if (!collectibleType.getName().equals(name)) {
                    logger.info("CollectibleType.name was updated from " + collectibleType.getName() + " to " + name);
                }
                collectibleType.setName(name);
            }

            collectibleType.setCollectibleTypeParent(collectibleTypeParent);

            return repository.save(collectibleType);
        }

        return repository.save(new CollectibleType(code, name, collectibleTypeParent));
    }
}
