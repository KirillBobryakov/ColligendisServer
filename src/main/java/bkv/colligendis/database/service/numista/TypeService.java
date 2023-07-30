package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.Type;
import bkv.colligendis.database.entity.numista.TypeGroup;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class TypeService extends AbstractService<Type, TypeRepository> {

    public TypeService(TypeRepository repository) {
        super(repository);
    }

    @Override
    public Type setPropertyValue(Long id, String name, String value) {
        return null;
    }

    public Type update(Type type, String code, String name, TypeGroup typeGroup){
        if(type == null || !type.getCode().equals(code)) {
            type = repository.findByCode(code).block();
        }
        if (type != null) {
            if(!type.getName().equals(name)){
                DebugUtil.showServiceMessage(this, "Trying to find Type with code=" + code + " and name=" + name
                        + ". But there is an Type with the same code and other name= " + type.getName() + "in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Type.name was updated.");
                type.setName(name);
            }

            if(typeGroup != null && !typeGroup.equals(type.getGroup())){
                DebugUtil.showServiceMessage(this, "Trying to find Type with code=" + code + " and name=" + name
                        + ". But there is an TypeGroup " + typeGroup + " with the same code and other group= " + type.getGroup() + "in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                type.setGroup(typeGroup);
            }

            return repository.save(type).block();
        } else {
            DebugUtil.showInfo(this, "New Type with code=" + code + " and name=" + name + " in TypeGroup=" + typeGroup + " was created.");
            return repository.save(new Type(code, name, typeGroup)).block();
        }

    }


}
