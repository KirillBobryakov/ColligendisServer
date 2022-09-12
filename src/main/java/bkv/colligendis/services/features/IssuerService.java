package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Issuer;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class IssuerService extends AbstractService<Issuer, IssuerRepository> {
    public IssuerService(IssuerRepository repository) {
        super(repository);
    }

    public Issuer findByName(String name){
        return repository.findByName(name).block();
    }
}
