package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.database.entity.numista.IssuingEntity;
import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class IssuingEntityService extends AbstractService<IssuingEntity, IssuingEntityRepository> {
    public IssuingEntityService(IssuingEntityRepository repository) {
        super(repository);
    }

    public IssuingEntity update(IssuingEntity issuingEntity, String name){
        if(issuingEntity == null || !issuingEntity.getName().equals(name)) {
            issuingEntity = repository.findByName(name).block();
        }
        if (issuingEntity == null) {
            DebugUtil.showInfo(this, "New IssuingEntity with name=" + name + " was created.");
            return repository.save(new IssuingEntity(name)).block();
        }
        return issuingEntity;
    }


    @Override
    public IssuingEntity setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
