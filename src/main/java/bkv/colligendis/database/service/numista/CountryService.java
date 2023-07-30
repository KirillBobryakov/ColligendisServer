package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CountryService extends AbstractService<Country, CountryRepository> {
    public CountryService(CountryRepository repository) {
        super(repository);
    }

    public Country findByName(String name){
        return repository.findByName(name).block();
    }

    public Country findOrCreate(String name){
        Country country = repository.findByName(name).block();
        if (country == null) {
            DebugUtil.showInfo(this, "New Country with name=" + name + " was created.");
            return repository.save(new Country(name)).block();
        }
        return country;
    }

    public List<Country> findAllWithSort(){
        return repository.findAllWithSort().collectList().blockOptional().orElse(new ArrayList<>());
    }


    public List<Country> findByNameFilter(String nameFilter){
        return repository.findByNameContainingIgnoreCase(nameFilter).collectList().blockOptional().orElse(new ArrayList<>());
    }


    @Override
    public Country setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
