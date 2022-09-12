package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.Mint;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class MintService extends AbstractService<Mint, MintRepository> {
    public MintService(MintRepository repository) {
        super(repository);
    }


    public Mint findByNumistaURL(String numistaURL){
        return repository.findByNumistaURL(numistaURL).block();
    }

}
