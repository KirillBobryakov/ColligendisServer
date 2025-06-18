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


    public void deleteAll(){
        repository.deleteAll();
    }

    /**
     * Find a Denomination's UUID by nid
     * @param nid Denomination's nid
     * @return Denomination's Eid in UUID value, or null
     */
    public UUID findUuidByCode(String nid){
        String eid = repository.findEidByCode(nid);
        return eid != null ? UUID.fromString(eid) : null;
    }

    /**
     * Find Denomination's nid by Denomination's uuid
     * @param uuid Denomination's uuid
     * @return Denomination's nid
     */
    public String findDenominationNidByUuid(UUID uuid){
        return repository.findDenominationNidByUuid(uuid.toString());
    }

    /**
     * Find Denomination by Denomination's nid
     * @param nid Denomination's nid
     * @return Denomination
     */
    public Denomination findDenominationByNid(String nid){
        return repository.findByNid(nid);
    }

    public List<Denomination> findDenominationsByCurrency(Currency currency){
        return repository.findByCurrency_Nid(currency.getNid());
    }


}
