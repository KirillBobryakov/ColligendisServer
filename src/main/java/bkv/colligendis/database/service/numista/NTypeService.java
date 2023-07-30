package bkv.colligendis.database.service.numista;


import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class NTypeService extends AbstractService<NType, NTypeRepository> {
    public NTypeService(NTypeRepository repository) {
        super(repository);
    }

    public NType findByNid(String nid){
        return repository.findByNid(nid).block();
    }

    public boolean existsByNid(String nid){
        return Boolean.TRUE.equals(repository.existsByNid(nid).block());
    }

    public List<NType> findByTitleFilter(String filter){
        return repository.findByTitleFilter("(?i).*" + filter + ".*").collectList().blockOptional().orElse(new ArrayList<>());
    }

    public List<NType> findByCountryEid(String eid){
        return repository.findByCountryEid(eid).collectList().blockOptional().orElse(new ArrayList<>());
    }

    public List<NType> findByTitleFilterAndCountryEid(String filter, String countryEid){
        return repository.findByTitleFilterAndCountryEid("(?i).*" + filter + ".*", countryEid).collectList().blockOptional().orElse(new ArrayList<>());
    }

    public List<NType> findBySubjectEid(String eid){
        return repository.findBySubjectEid(eid).collectList().blockOptional().orElse(new ArrayList<>());
    }
    public Flux<NType> findBySubjectEidAndYear(String eid, int year){
        return repository.findBySubjectEidAndYear(eid, year);
    }
    public List<NType> findByTitleFilterAndSubjectEid(String filter, String eid){
        return repository.findByTitleFilterAndSubjectEid("(?i).*" + filter + ".*", eid).collectList().blockOptional().orElse(new ArrayList<>());
    }

    public List<NType> findByIssuerEid(String eid){
        return repository.findByIssuerEid(eid).collectList().blockOptional().orElse(new ArrayList<>());
    }

    public Flux<String> findNTypeNidByIssuerEid(String eid){
        return repository.findNTypeNidByIssuerEid(eid);
    }

    public Flux<NType> findByIssuerEidAndYear(String eid, int year){
        return repository.findByIssuerEidAndYear(eid, year);
    }

//    public Flux<MinMaxYears> findByIssuerEidAndYearWithVariantsCount(String eid, int year){
//        return repository.findByIssuerEidAndYearWithVariantsCount(eid, year);
//    }

    public List<NType> findByTitleFilterAndIssuerEid(String filter, String issuerEid){
        return repository.findByTitleFilterAndIssuerEid("(?i).*" + filter + ".*", issuerEid).collectList().blockOptional().orElse(new ArrayList<>());
    }

    public Flux<NType> findByTitleFilterAndIssuerEidAndYear(String filter, String issuerEid, int year){
        return repository.findByTitleFilterAndIssuerEidAndYear("(?i).*" + filter + ".*", issuerEid, year);
    }


    @Override
    public NType setPropertyValue(Long id, String name, String value) {
        return null;
    }


}
