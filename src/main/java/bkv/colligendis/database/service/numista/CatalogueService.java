package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Catalogue;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CatalogueService extends AbstractService<Catalogue, CatalogueRepository> {

    private static final Logger logger = LogManager.getLogger(CatalogueService.class);

    public CatalogueService(CatalogueRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing
    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Catalogue.LABEL, "nid", nid);
    }

    // End: Methods for Numista parsing

    public Catalogue findByNid(String nid, String code) {
        Catalogue catalogue = repository.findByNid(nid);
        if (catalogue != null) {
            if (!catalogue.getCode().equals(code)) {
                logger.warn("Trying to find Catalogue with nid=" + nid + " and code=" + code
                        + ". But there is a Catalogue with the same nid and other code = " + catalogue.getCode()
                        + " in DB already.");

                logger.warn("Catalogue.code was updated.");
                return repository.save(catalogue);
            }
        } else {
            logger.info("New Catalogue with nid=" + nid + " and code=" + code + " was created.");
            return repository.save(new Catalogue(nid, code));
        }
        return catalogue;
    }

    public Catalogue create(String nid, String code, String bibliography) {
        Catalogue catalogue = findByNid(nid, code);
        if (catalogue != null) {
            catalogue.setBibliography(bibliography);
            return repository.save(catalogue);
        }
        return null;
    }

}
