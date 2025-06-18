package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CollectibleTypeGroup;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class TypeGroupService extends AbstractService<CollectibleTypeGroup, CollectibleTypeGroupRepository> {

    public TypeGroupService(CollectibleTypeGroupRepository repository) {
        super(repository);
    }


    public CollectibleTypeGroup findByName(String name){
        return repository.findByName(name);
    }

    public CollectibleTypeGroup update(String name){
        CollectibleTypeGroup typeGroup = repository.findByName(name);

        if (typeGroup == null) {
            typeGroup = repository.save(new CollectibleTypeGroup(name));
            DebugUtil.showInfo(this, "New CollectibleTypeGroup with name=" + name + " was created.");
            return typeGroup;
        } else {
            if(!typeGroup.getName().equals(name)){
                DebugUtil.showInfo(this, "CollectibleTypeGroup has stale name=" + name);
                typeGroup.setName(name);
                typeGroup = repository.save(typeGroup);
                DebugUtil.showInfo(this, "CollectibleTypeGroup with a new name=" + name);
                return typeGroup;
            }
        }

        return typeGroup;
    }





}
