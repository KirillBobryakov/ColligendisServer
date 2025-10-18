package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Technique;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class TechniqueService extends AbstractService<Technique, TechniqueRepository> {
    private static final Logger logger = LogManager.getLogger(TechniqueService.class);

    public TechniqueService(TechniqueRepository repository) {
        super(repository);
    }

    public boolean compareName(UUID techniqueUuid, String name) {
        return comparePropertyValue(techniqueUuid, "name", name, String.class);
    }

    public boolean setName(UUID techniqueUuid, String name) {
        return setPropertyStringValue(techniqueUuid, "name", name);
    }

    // Start: Methods for Numista parsing

    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Technique.LABEL, "nid", nid);
    }

    // End: Methods for Numista parsing

    public Technique findByNid(String nid, String name) {
        Technique technique = repository.findByNid(nid);
        if (technique != null) {
            if (!technique.getName().equals(name)) {
                logger.warn("Trying to find Technique with nid=" + nid + " and name=" + name
                        + ". But there is a Technique with the same nid and other name = " + technique.getName()
                        + " in DB already.");
                logger.warn("Technique.name was updated.");
                technique.setName(name);
            }
        } else {
            logger.info("New Technique with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new Technique(nid, name));
        }
        return technique;
    }

}
