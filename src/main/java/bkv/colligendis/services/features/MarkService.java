//package bkv.colligendis.services.features;
//
//import bkv.colligendis.database.entity.numista.Mintmark;
//import bkv.colligendis.services.AbstractService;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class MarkService extends AbstractService<Mintmark, MarkRepository> {
//    public MarkService(MarkRepository repository) {
//        super(repository);
//    }
//
//
//    public Mintmark findByNumistaURL(String numistaURL){
//        return repository.findByNumistaURL(numistaURL).block();
//    }
//    public Mintmark findByName(String name){
//        return repository.findByName(name).block();
//    }
//
//    public List<Mintmark> findByNameFilter(String nameFilter){
//        return repository.findByNameContainingIgnoreCase(nameFilter).collectList().block();
//    }
//
//    public Integer relationCount(Long markId){
//        return repository.countRelations(markId).block();
//    }
//
//    @Override
//    public Mintmark setPropertyValue(Long id, String name, String value) {
//        return null;
//    }
//}
