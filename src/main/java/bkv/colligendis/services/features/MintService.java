//package bkv.colligendis.services.features;
//
//import bkv.colligendis.database.entity.numista.Mint;
//import bkv.colligendis.services.AbstractService;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class MintService extends AbstractService<Mint, MintRepository> {
//    public MintService(MintRepository repository) {
//        super(repository);
//    }
//
//
//    public Mint findByNumistaURL(String numistaURL){
//        return repository.findByNumistaURL(numistaURL).block();
//    }
//    public Mint findByName(String name){
//        return repository.findByName(name).block();
//    }
//
//    public List<Mint> findByNameFilter(String nameFilter){
//        return repository.findByNameContainingIgnoreCase(nameFilter).collectList().block();
//    }
//
//    public Integer relationCount(Long mintId){
//        return repository.countRelations(mintId).block();
//    }
//
//    @Override
//    public Mint setPropertyValue(Long id, String name, String value) {
//        return null;
//    }
//}
