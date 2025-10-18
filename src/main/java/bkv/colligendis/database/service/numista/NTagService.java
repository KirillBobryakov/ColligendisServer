package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.NTag;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class NTagService extends AbstractService<NTag, NTagRepository> {
    private static final Logger logger = LogManager.getLogger(NTagService.class);

    public NTagService(NTagRepository repository) {
        super(repository);
    }

    public NTag findByNid(String nid, String name) {
        NTag nTag = repository.findByNid(nid);
        if (nTag != null) {
            if (!nTag.getName().equals(name)) {
                logger.warn("Trying to find NTag with nid=" + nid + " and name=" + name
                        + ". But there is a NTag with the same nid and other name = " + nTag.getName()
                        + " in DB already.");
                logger.warn("NTag.name was updated.");
                nTag.setName(name);
                return repository.save(nTag);
            }
        } else {
            logger.info("New NTag with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new NTag(nid, name));
        }
        return nTag;
    }

}
