package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class CollectibleTypeService extends AbstractService<CollectibleType, TypeRepository> {

    public CollectibleTypeService(TypeRepository repository) {
        super(repository);
    }



    public CollectibleType findByCode(String code){
        return repository.findByCode(code);
    }


    public CollectibleType update(CollectibleType collectibleType, String code, String name, CollectibleType collectibleTypeParent){
        if(collectibleType == null && code != null && !code.isEmpty()){
            collectibleType = repository.findByCode(code);
        }

        if(collectibleType != null){
            if(name != null && !name.isEmpty()){
                if(!collectibleType.getName().equals(name)){
                    DebugUtil.showInfo(CollectibleTypeService.class, "CollectibleType name is changing from " + collectibleType.getName() + " to " + name);
                }
                collectibleType.setName(name);
            }

            collectibleType.setCollectibleTypeParent(collectibleTypeParent);

            return repository.save(collectibleType);
        }


        return repository.save(new CollectibleType(code, name, collectibleTypeParent));
    }
}
