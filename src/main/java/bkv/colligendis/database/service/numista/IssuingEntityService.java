package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.IssuingEntity;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssuingEntityService extends AbstractService<IssuingEntity, IssuingEntityRepository> {
    public IssuingEntityService(IssuingEntityRepository repository) {
        super(repository);
    }


    public void deleteAll(){
        repository.deleteAll();
    }

    /**
     * Find an IssuingEntity by code
     * @param code IssuingEntity's code
     * @return IssuingEntity, or null
     */
    public IssuingEntity findIssuingEntityByCode(String code){
        return repository.findIssuingEntityByCode(code);
    }


    public List<IssuingEntity> findIssuingEntitiesByIssuer(Issuer issuer){
        return repository.findByIssuerCode(issuer.getCode());
    }



    public IssuingEntity update(IssuingEntity issuingEntity, String name){
        if(issuingEntity == null || !issuingEntity.getName().equals(name)) {
            issuingEntity = repository.findByName(name);
        }
        if (issuingEntity == null) {
            DebugUtil.showInfo(this, "New IssuingEntity with name=" + name + " was created.");
//            return repository.save(new IssuingEntity(name));
        }
        return issuingEntity;
    }



}
