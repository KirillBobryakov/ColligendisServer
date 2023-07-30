package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mintmark;
import bkv.colligendis.database.entity.numista.SpecifiedMint;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class SpecifiedMintService extends AbstractService<SpecifiedMint, SpecifiedMintRepository> {
    public SpecifiedMintService(SpecifiedMintRepository repository) {
        super(repository);
    }

    public SpecifiedMint findByIdentifierMintMintmark(String identifier, String mintNid, String mintmarkNid){
        String specifiedMintEid = null;
        if(mintmarkNid == null){
            specifiedMintEid = repository.findByIdentifierMint(identifier, mintNid).block();
        } else {
            specifiedMintEid = repository.findByIdentifierMintMintmark(identifier, mintNid, mintmarkNid).block();
        }
        if(specifiedMintEid != null) {
            SpecifiedMint specifiedMint = repository.findByEid(specifiedMintEid).block();
            return specifiedMint;
        }

        return null;
    }

    @Override
    public SpecifiedMint setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
