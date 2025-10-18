package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.LetteringScript;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class LetteringScriptService extends AbstractService<LetteringScript, LetteringScriptRepository> {
    private static final Logger logger = LogManager.getLogger(LetteringScriptService.class);

    public LetteringScriptService(LetteringScriptRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing
    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(LetteringScript.LABEL, "nid", nid);
    }

    public boolean compareName(UUID letteringScriptUuid, String name) {
        return comparePropertyValue(letteringScriptUuid, "name", name, String.class);
    }

    public void setName(UUID letteringScriptUuid, String name) {
        setPropertyStringValue(letteringScriptUuid, "name", name);
    }

    // End: Methods for Numista parsing

    public LetteringScript findByNid(String nid, String name) {
        LetteringScript letteringScript = repository.findByNid(nid);
        if (letteringScript != null) {
            if (!letteringScript.getName().equals(name)) {
                logger.warn("Trying to find LetteringScript with nid=" + nid + " and name=" + name
                        + ". But there is a LetteringScript with the same nid and other name = "
                        + letteringScript.getName() + " in DB already.");
                logger.warn("LetteringScript.name was updated.");
                letteringScript.setName(name);
            }
        } else {
            logger.info("New LetteringScript with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new LetteringScript(nid, name));
        }
        return letteringScript;
    }

}
