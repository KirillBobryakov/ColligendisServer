package bkv.colligendis.services;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import bkv.colligendis.utils.DebugUtil;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractService<E extends AbstractEntity, R extends AbstractNeo4jRepository<E>> {

    protected final R repository;

    protected AbstractService(R repository) {
        this.repository = repository;
    }

    public E save(E entity){
        return repository.save(entity).block();
    }

    public E findByEid(String eid) {return repository.findByEid(eid).block(); }

    public List<E> findAll(){
        return repository.findAll().toStream().collect(Collectors.toList());
    }

    public void delete(String eid) {
        repository.deleteWithDetach(eid);
    }

    public Long countRelationships(String eid) { return repository.countRelationships(eid);}

    public abstract E setPropertyValue(Long id, String name, String value);


}
