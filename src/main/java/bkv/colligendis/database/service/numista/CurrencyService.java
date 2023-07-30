package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService extends AbstractService<Currency, CurrencyRepository> {
    public CurrencyService(CurrencyRepository repository) {
        super(repository);
    }


    public Currency findByName(String name){
        return repository.findByName(name).block();
    }


    public Currency update(Currency currency, String nid, String fullName){
        if(currency == null || !currency.getNid().equals(nid)) {
            currency = repository.findByNid(nid).block();
        }
        if (currency != null) {
            if(!currency.getFullName().equals(fullName)){
                DebugUtil.showServiceMessage(this, "Trying to find Currency with nid=" + nid + " and fullName=" + fullName
                        + ". But there is a Currency with the same nid and other fullName= " + currency.getFullName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Currency.fullName was updated.");
                currency.setName(fullName);
                return repository.save(currency).block();
            }
        } else {
            DebugUtil.showInfo(this, "New Currency with nid=" + nid + " and fullName=" + fullName + " was created.");
            return repository.save(new Currency(nid, fullName)).block();
        }
        return currency;
    }


    @Override
    public Currency setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
