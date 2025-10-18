package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mintmark;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class MintmarkService extends AbstractService<Mintmark, MintmarkRepository> {
    public MintmarkService(MintmarkRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing
    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Mintmark.LABEL, "nid", nid);
    }
    // End: Methods for Numista parsing

    public Mintmark findByNid(String nid) {
        Mintmark mintmark = repository.findByNid(nid);
        if (mintmark == null) {
            DebugUtil.showInfo(this, "New Mintmark with nid=" + nid + " was created.");
            Mintmark mm = new Mintmark(nid, null);
            return repository.save(mm);
        }
        return mintmark;
    }
    //
    // public SpecifiedMint findByIdentifier(String identifier){
    // SpecifiedMint mintmark = repository.findByIdentifier(identifier);
    // if (mintmark == null) {
    // DebugUtil.showInfo(this, "New Mintmark with identifier=" + identifier + " was
    // created.");
    // return repository.save(new SpecifiedMint(null, identifier));
    // }
    // return mintmark;
    // }

}
