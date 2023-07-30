package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IssuerService extends AbstractService<Issuer, IssuerRepository> {
    public IssuerService(IssuerRepository repository) {
        super(repository);
    }

    public Issuer findByName(String name){
        return repository.findByName(name).block();
    }

    public List<Issuer> findByNameFilter(String nameFilter){
        return repository.findByNameContainingIgnoreCase(nameFilter).collectList().blockOptional().orElse(new ArrayList<>());
    }

    public List<Issuer> findByCountryEid(String eid){
        return repository.findByCountryEid(eid).collectList().blockOptional().orElse(new ArrayList<>());
    }

    public List<Issuer> findBySubjectEid(String eid){
        return repository.findBySubjectEid(eid).collectList().blockOptional().orElse(new ArrayList<>());
    }


    public Issuer update(Issuer issuer, String code, String name){
        if(issuer == null || !issuer.getCode().equals(code)) {
            issuer = repository.findByCode(code).block();
        }
        if (issuer != null) {
            if(!issuer.getName().equals(name)){
                DebugUtil.showWarning(this, "Trying to find Issuer with code=" + code + " and name=" + name
                        + ". But there is an Issuer with the same code and other name= " + issuer.getName() + " in DB already.");
                DebugUtil.showWarning(this, "Issuer.name was updated.");
                issuer.setName(name);
                return repository.save(issuer).block();
            }
        } else {
            DebugUtil.showInfo(this, "New Issuer with code=" + code + " and name=" + name + " was created.");
            return repository.save(new Issuer(code, name)).block();
        }
        return issuer;
    }

    public Issuer findOrCreate(String name){
        Issuer issuer = repository.findByName(name).block();
        if (issuer == null) {
            DebugUtil.showInfo(this, "New Issuer with name=" + name + " was created.");
            return repository.save(new Issuer(name)).block();
        }
        return issuer;
    }


    @Override
    public Issuer setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
