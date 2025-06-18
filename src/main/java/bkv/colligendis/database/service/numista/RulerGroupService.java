package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RulerService extends AbstractService<Ruler, RulerRepository> {

    public RulerService(RulerRepository repository) {
        super(repository);
    }

    @Override
    public Ruler setPropertyValue(Long id, String name, String value) {
        return null;
    }

    /**
     *  Find Ruler's {@code eid} UUID by {@code nid}.
     *
     * @param nid Ruler's nid (Numista Ruler id)
     * @return If Ruler with {@code nid} exists, then return Ruler's eid like UUID
     */
    public UUID findByNid(String nid){
        String rulerEid = repository.findByNid(nid);
        return rulerEid != null ? UUID.fromString(rulerEid) : null;
    }

    public Ruler update(Ruler ruler, String nid, String name){
        if(ruler == null || !ruler.getNid().equals(nid)) {
//            ruler = repository.findByNid(nid);
        }
        if (ruler != null) {
            if(!ruler.getName().equals(name)){
                DebugUtil.showServiceMessage(this, "Trying to find Ruller with nid=" + nid + " and name=" + name
                        + ". But there is a Ruller with the same nid and other name = " + ruler.getName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Ruler.name was updated.");
                ruler.setName(name);
                return repository.save(ruler);
            }
        } else {
            DebugUtil.showInfo(this, "New Ruler with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new Ruler(nid, name));
        }
        return ruler;
    }


}
