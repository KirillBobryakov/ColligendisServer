package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mintmark;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class MintmarkService extends AbstractService<Mintmark, MintmarkRepository> {
    public MintmarkService(MintmarkRepository repository) {
        super(repository);
    }

    public Mintmark findByNid(String nid){
        Mintmark mintmark = repository.findByNid(nid);
        if (mintmark == null) {
            DebugUtil.showInfo(this, "New Mintmark with nid=" + nid + " was created.");
            Mintmark mm = new Mintmark();
            mm.setNid(nid);
            return repository.save(mm);
        }
        return mintmark;
    }
//
//    public SpecifiedMint findByIdentifier(String identifier){
//        SpecifiedMint mintmark = repository.findByIdentifier(identifier);
//        if (mintmark == null) {
//            DebugUtil.showInfo(this, "New Mintmark with identifier=" + identifier + " was created.");
//            return repository.save(new SpecifiedMint(null, identifier));
//        }
//        return mintmark;
//    }

}
