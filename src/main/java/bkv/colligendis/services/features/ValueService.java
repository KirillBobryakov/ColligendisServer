package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Value;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class ValueService extends AbstractService<Value, ValueRepository> {
    public ValueService(ValueRepository repository) {
        super(repository);
    }

    public Value findByName(String name){
        return repository.findByName(name).block();
    }
}
