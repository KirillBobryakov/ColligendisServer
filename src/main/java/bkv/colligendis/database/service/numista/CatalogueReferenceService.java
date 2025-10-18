package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Catalogue;
import bkv.colligendis.database.entity.numista.CatalogueReference;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CatalogueReferenceService extends AbstractService<CatalogueReference, CatalogueReferenceRepository> {

    private static final Logger logger = LogManager.getLogger(CatalogueReferenceService.class);

    public CatalogueReferenceService(CatalogueReferenceRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing

    public UUID findUuidByNumberAndCatalogueNid(String numberCatalogueReference, String nidCatalogue) {
        String uuid = repository.findUuidByNumberAndCatalogueNid(numberCatalogueReference, nidCatalogue);
        return uuid != null ? UUID.fromString(uuid) : null;
    }

    public void setNumber(UUID uuid, String number) {
        setPropertyStringValue(uuid, "number", number);
    }

    public void setCatalogue(UUID uuid, UUID catalogueUuid) {
        setSingleOutgoingRelationshipToNode(uuid, catalogueUuid, CatalogueReference.REFERENCE_FROM, Catalogue.LABEL);
    }

    // End: Methods for Numista parsing

    public CatalogueReference findByNumberAndCatalogueNid(String number, Catalogue catalogue) {
        CatalogueReference catalogueReference = repository.findByNumberAndCatalogue_Nid(number, catalogue.getNid());
        if (catalogueReference == null) {
            logger.info("New CatalogueReference with number=" + number + " and references to catalogue="
                    + catalogue + " was created.");
            return repository.save(new CatalogueReference(catalogue, number));
        }
        return catalogueReference;
    }

}
