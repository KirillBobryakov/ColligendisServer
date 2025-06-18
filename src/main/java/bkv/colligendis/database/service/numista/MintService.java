package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MintService extends AbstractService<Mint, MintRepository> {
    public MintService(MintRepository repository) {
        super(repository);
    }

    public Mint findByNid(String nid, String fullName){
        Mint mint = repository.findByNid(nid);
        if (mint != null) {
            if(!mint.getFullName().equals(fullName)){
                DebugUtil.showServiceMessage(this, "Trying to find Mint with nid=" + nid + " and fullName=" + fullName
                        + ". But there is a Mint with the same nid and other fullName = " + mint.getFullName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Mint.fullName was updated.");
                mint.setFullName(fullName);
                return repository.save(mint);
            }
        } else {
            DebugUtil.showInfo(this, "New Mint with nid=" + nid + " and fullName=" + fullName + " was created.");
            Mint m = new Mint();
            m.setNid(nid);
            m.setFullName(fullName);
            return repository.save(m);
        }
        return mint;
    }

    public Mint findByNumistaURL(String numistaURL){
        return repository.findByNumistaURL(numistaURL);
    }
    public Mint findByName(String name){
        return repository.findByName(name);
    }

    public Integer relationCount(String mintId){
        return repository.countRelations(mintId);
    }

    public List<Mint> findByNameFilter(String nameFilter){
        return repository.findByNameContainingIgnoreCase(nameFilter);
    }

}
