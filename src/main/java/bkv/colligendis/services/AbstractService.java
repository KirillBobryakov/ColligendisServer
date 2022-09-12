package bkv.colligendis.services;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractService<E extends AbstractEntity, R extends ReactiveNeo4jRepository<E, Long>> {

    public final R repository;


    protected AbstractService(R repository) {
        this.repository = repository;
    }

    public E save(E entity){
        return repository.save(entity).block();
    }

    public E findById(Long id) {return repository.findById(id).block(); }

    public List<E> findAll(){
        return repository.findAll().toStream().collect(Collectors.toList());
    }

    public void delete(Long id) {repository.deleteById(id).block();}

    public Long count() { return repository.count().block();}

}
