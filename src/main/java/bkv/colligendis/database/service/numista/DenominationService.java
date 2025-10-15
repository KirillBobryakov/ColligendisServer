package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.Denomination;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DenominationService extends AbstractService<Denomination, DenominationRepository> {
    public DenominationService(DenominationRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing

    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Denomination.LABEL, "nid", nid);
    }

    public void detachCurrency(UUID denominationUuid, UUID currencyUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(denominationUuid, currencyUuid,
                Denomination.UNDER_CURRENCY);
    }

    public void setCurrency(UUID denominationUuid, UUID currencyUuid) {
        setSingleOutgoingRelationshipToNode(denominationUuid, currencyUuid, Denomination.UNDER_CURRENCY,
                Currency.LABEL);
    }

    public boolean setFullName(UUID denominationUuid, String fullName) {
        return setPropertyStringValue(denominationUuid, "fullName", fullName);
    }

    public boolean setName(UUID denominationUuid, String name) {
        return setPropertyStringValue(denominationUuid, "name", name);
    }

    public boolean setNumericValue(UUID denominationUuid, Float numericValue) {
        return setPropertyFloatValue(denominationUuid, "numericValue", numericValue);
    }

    public boolean setIsActual(UUID denominationUuid, Boolean isActual) {
        return setPropertyBooleanValue(denominationUuid, "isActual", isActual);
    }

    // End: Methods for Numista parsing

    /**
     * Find a Denomination's UUID by nid
     * 
     * @param nid Denomination's nid
     * @return Denomination's Eid in UUID value, or null
     */
    public UUID findUuidByCode(String nid) {
        String eid = repository.findEidByCode(nid);
        return eid != null ? UUID.fromString(eid) : null;
    }

    /**
     * Find Denomination's nid by Denomination's uuid
     * 
     * @param uuid Denomination's uuid
     * @return Denomination's nid
     */
    public String findDenominationNidByUuid(UUID uuid) {
        return repository.findDenominationNidByUuid(uuid.toString());
    }

    /**
     * Find Denomination by Denomination's nid
     * 
     * @param nid Denomination's nid
     * @return Denomination
     */
    public Denomination findDenominationByNid(String nid) {
        return repository.findByNid(nid);
    }

    public List<Denomination> findDenominationsByCurrency(Currency currency) {
        return repository.findByCurrency_Nid(currency.getNid());
    }

    public List<Denomination> findDenominationsByCurrencyUuid(UUID currencyUuid) {
        return repository.findByCurrency_Uuid(currencyUuid.toString());
    }

}
