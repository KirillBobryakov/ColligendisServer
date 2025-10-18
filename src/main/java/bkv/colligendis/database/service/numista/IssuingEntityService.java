package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.IssuingEntity;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class IssuingEntityService extends AbstractService<IssuingEntity, IssuingEntityRepository> {
    private static final Logger logger = LogManager.getLogger(IssuingEntityService.class);

    public IssuingEntityService(IssuingEntityRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing

    public UUID findUuidByCode(String code) {
        return findUuidByPropertyStringValue(IssuingEntity.LABEL, "code", code);
    }

    public void detachIssuer(UUID issuingEntityUuid, UUID issuerUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(issuingEntityUuid, issuerUuid,
                IssuingEntity.ISSUES_WHEN_BEEN);
    }

    public void setIssuer(UUID issuingEntityUuid, UUID issuerUuid) {
        setSingleOutgoingRelationshipToNode(issuingEntityUuid, issuerUuid, IssuingEntity.ISSUES_WHEN_BEEN,
                Issuer.LABEL);
    }

    // End: Methods for Numista parsing

    /**
     * Find an IssuingEntity by code
     * 
     * @param code IssuingEntity's code
     * @return IssuingEntity, or null
     */
    public IssuingEntity findIssuingEntityByCode(String code) {
        return repository.findIssuingEntityByCode(code);
    }

    public List<IssuingEntity> findIssuingEntitiesByIssuer(Issuer issuer) {
        return repository.findByIssuerCode(issuer.getCode());
    }

    public IssuingEntity update(IssuingEntity issuingEntity, String name) {
        if (issuingEntity == null || !issuingEntity.getName().equals(name)) {
            issuingEntity = repository.findByName(name);
        }
        if (issuingEntity == null) {
            logger.info("New IssuingEntity with name=" + name + " was created.");
            // return repository.save(new IssuingEntity(name));
        }
        return issuingEntity;
    }

}
