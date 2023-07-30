package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectService extends AbstractService<Subject, SubjectRepository> {
    public SubjectService(SubjectRepository repository) {
        super(repository);
    }

    public Subject findOrCreate(String name){
        Subject subject = repository.findByName(name).block();
        if (subject == null) {
            DebugUtil.showInfo(this, "New Subject with name=" + name + " was created.");
            return repository.save(new Subject(name)).block();
        }
        return subject;
    }



    public List<Subject> findByNameFilter(String nameFilter){
        return repository.findByNameContainingIgnoreCase(nameFilter).collectList().blockOptional().orElse(new ArrayList<>());
    }

    public List<Subject> findByCountryEid(String countryEid){
        return repository.findByCountryEid(countryEid).collectList().blockOptional().orElse(new ArrayList<>());
    }


    @Override
    public Subject setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
