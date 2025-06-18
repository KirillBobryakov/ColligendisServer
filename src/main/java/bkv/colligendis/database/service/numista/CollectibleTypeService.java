package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.entity.numista.CollectibleTypeGroup;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class TypeService extends AbstractService<CollectibleType, TypeRepository> {

    public TypeService(TypeRepository repository) {
        super(repository);
    }



    public CollectibleType findByCode(String code){
        return repository.findByCode(code);
    }


    public CollectibleType update(CollectibleType collectibleType, String code, String name, CollectibleTypeGroup collectibleTypeGroup){
        if(collectibleType == null || !collectibleType.getCode().equals(code)) {
            collectibleType = repository.findByCode(code);
        }
        if (collectibleType != null) {
            if(!collectibleType.getName().equals(name)){
                DebugUtil.showServiceMessage(this, "Trying to find CollectibleType with code=" + code + " and name=" + name
                        + ". But there is an CollectibleType with the same code and other name= " + collectibleType.getName() + "in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Type.name was updated.");
                collectibleType.setName(name);
            }

            if(collectibleTypeGroup != null && !collectibleTypeGroup.equals(collectibleType.getCollectibleTypeGroup())){
                DebugUtil.showServiceMessage(this, "Trying to find CollectibleType with code=" + code + " and name=" + name
                        + ". But there is an CollectibleTypeGroup " + collectibleTypeGroup + " with the same code and other group= " + collectibleType.getCollectibleTypeGroup() + "in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                collectibleType.setCollectibleTypeGroup(collectibleTypeGroup);
            }

            return repository.save(collectibleType);
        } else {
            DebugUtil.showInfo(this, "New CollectibleType with code=" + code + " and name=" + name + " in CollectibleTypeGroup=" + collectibleTypeGroup + " was created.");
            return repository.save(new CollectibleType(code, name, collectibleTypeGroup));
        }

    }


}
