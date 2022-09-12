package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Currency;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService extends AbstractService<Currency, CurrencyRepository> {
    public CurrencyService(CurrencyRepository repository) {
        super(repository);
    }

    public Currency findByName(String name){
        return repository.findByName(name).block();
    }
}
