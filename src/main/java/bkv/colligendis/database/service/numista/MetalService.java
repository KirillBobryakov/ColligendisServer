package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Metal;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class MetalService extends AbstractService<Metal, MetalRepository> {
    public MetalService(MetalRepository repository) {
        super(repository);
    }


    public Metal findByNid(String nid, String name){
        Metal metal = repository.findByNid(nid).block();
        if (metal != null) {
            if(!metal.getName().equals(name)){
                DebugUtil.showServiceMessage(this, "Trying to find Metal with nid=" + nid + " and name=" + name
                        + ". But there is a Metal with the same nid and other name= " + metal.getName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Metal.name was updated.");
                metal.setName(name);
                return repository.save(metal).block();
            }
        } else {
            DebugUtil.showInfo(this, "New Metal with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new Metal(nid, name)).block();
        }
        return metal;
    }


    @Override
    public Metal setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
