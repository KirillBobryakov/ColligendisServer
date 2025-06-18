package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CurrencyService extends AbstractService<Currency, CurrencyRepository> {
    public CurrencyService(CurrencyRepository repository) {
        super(repository);
    }


    public void deleteAll(){
        repository.deleteAll();
    }

    public Currency findByName(String name){
        return repository.findByName(name);
    }



    /**
     If there is no a relationship between CURRENCY (with UUID currencyUuid) and DENOMINATION (with UUID denominationUuid), then create one with a label - HAS_DENOMINATION.

     @param currencyUuid CURRENCY's UUID
     @param denominationUuid DENOMINATION's UUID

     @return {@code true} If a relationship was presented, or was created; {@code false} There was not a relationship, and it was not created
     */
    public Boolean setHasDenomination(UUID currencyUuid, UUID denominationUuid){
//        return repository.createSingleRelationshipToNode(currencyUuid.toString(), denominationUuid.toString(), Currency.HAS_DENOMINATION);
        return null;
    }


    /**
     *  Check a Relationship between CURRENCY and DENOMINATION
     * @param currencyUuid CURRENCY's UUID
     * @param denominationUuid DENOMINATION's UUID
     * @return {@code true} If a relationship was presented, return {@code false} if there isn't a relationship
     */
    public Boolean hasSingleRelationshipToNode(UUID currencyUuid, UUID denominationUuid){
//        return repository.hasSingleRelationshipToNode(currencyUuid.toString(), denominationUuid.toString(), Currency.HAS_DENOMINATION);
        return null;
    }


    /**
     * Find Currency's nid by Currency's uuid
     * @param uuid Currency's uuid
     * @return Currency's nid
     */
    public String findCurrencyNidByUuid(UUID uuid){
        return repository.findCurrencyNidByUuid(uuid.toString());
    }

    /**
     * Find Currency's uuid by Currency's nid
     * @param nid Currency's nid
     * @return Currency's uuid
     */
    public Currency findCurrencyByNid(String nid){
        return repository.findCurrencyByNid(nid);
    }


    public List<Currency> findCurrencyByIssuer(Issuer issuer){
        return repository.findByIssuerCode(issuer.getCode());
    }

//
//    public Currency update(Currency currency, String nid, String fullName){
//        if(currency == null || !currency.getNid().equals(nid)) {
//            currency = repository.findByNid(nid);
//        }
//        if (currency != null) {
//            if(!currency.getFullName().equals(fullName)){
//                DebugUtil.showServiceMessage(this, "Trying to find Currency with nid=" + nid + " and fullName=" + fullName
//                        + ". But there is a Currency with the same nid and other fullName= " + currency.getFullName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
//                DebugUtil.showWarning(this, "Currency.fullName was updated.");
//                currency.setName(fullName);
//                return repository.save(currency);
//            }
//        } else {
//            DebugUtil.showInfo(this, "New Currency with nid=" + nid + " and fullName=" + fullName + " was created.");
//            return repository.save(new Currency(nid, fullName));
//        }
//        return currency;
//    }




}
