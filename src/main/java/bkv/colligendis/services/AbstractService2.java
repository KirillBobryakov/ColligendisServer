package bkv.colligendis.services;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractService2<E extends AbstractEntity, R extends Neo4jRepository<E, Long>> {

    protected final R repository;


    protected AbstractService2(R repository) {
        this.repository = repository;
    }

    public E save(E entity){
        return repository.save(entity);
    }

    public Optional<E> findById(Long id) {
        return repository.findById(id);
    }

    public List<E> findAll(){
        return repository.findAll();
    }

    public void delete(Long id) {repository.deleteById(id);}

    public long count() { return repository.count();}


}
