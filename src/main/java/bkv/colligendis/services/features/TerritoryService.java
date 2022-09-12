package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Territory;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class TerritoryService extends AbstractService<Territory, TerritoryRepository> {

    public TerritoryService(TerritoryRepository repository) {
        super(repository);
    }

    public Territory findByName(String name){
        return repository.findByName(name).block();
    }
}
