package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.SpecifiedMint;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class SpecifiedMintService extends AbstractService<SpecifiedMint, SpecifiedMintRepository> {
    public SpecifiedMintService(SpecifiedMintRepository repository) {
        super(repository);
    }

    public SpecifiedMint findByIdentifierMintMintmark(String identifier, String mintNid, String mintmarkNid){
        SpecifiedMint specifiedMint = null;
        if(mintmarkNid == null){
            specifiedMint = repository.findByIdentifierMintWithoutMintmark(identifier, mintNid);
        } else {
            specifiedMint = repository.findByIdentifierMintMintmark(identifier, mintNid, mintmarkNid);
        }

        return specifiedMint;
    }

}
