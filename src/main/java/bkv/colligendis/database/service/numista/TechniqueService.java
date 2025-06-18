package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Catalogue;
import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.database.entity.numista.Technique;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class TechniqueService extends AbstractService<Technique, TechniqueRepository> {
    public TechniqueService(TechniqueRepository repository) {
        super(repository);
    }

    public Technique findByNid(String nid, String name){
        Technique technique = repository.findByNid(nid);
        if (technique != null) {
            if(!technique.getName().equals(name)){
                DebugUtil.showServiceMessage(this, "Trying to find Technique with nid=" + nid + " and name=" + name
                        + ". But there is a Technique with the same nid and other name = " + technique.getName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Technique.name was updated.");
                technique.setName(name);
            }
        } else {
            DebugUtil.showInfo(this, "New Technique with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new Technique(nid, name));
        }
        return technique;
    }


}
