package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Type;
import bkv.colligendis.database.entity.numista.TypeGroup;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class TypeGroupService extends AbstractService<TypeGroup, TypeGroupRepository> {

    public TypeGroupService(TypeGroupRepository repository) {
        super(repository);
    }

    @Override
    public TypeGroup setPropertyValue(Long id, String name, String value) {
        return null;
    }


    public TypeGroup findByName(String name){
        TypeGroup typeGroup = repository.findByName(name).block();
        if (typeGroup == null) {
            DebugUtil.showInfo(this, "New TypeGroup with name=" + name + " was created.");
            return repository.save(new TypeGroup(name)).block();
        }
        return typeGroup;
    }



}
