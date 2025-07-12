package bkv.colligendis.services;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

import java.util.List;
import java.util.UUID;

public abstract class AbstractService<E extends AbstractEntity, R extends AbstractNeo4jRepository<E>> {

    protected final R repository;

    protected List<E> cash;
    protected boolean isCashed = false;

    protected AbstractService(R repository) {
        this.repository = repository;
    }

    private void initCash() {
        if (isCashed) {
            cash = findAll();
        }
    };

    public void setCashed() {
        this.isCashed = true;
        initCash();
    }

    public E save(E entity) {
        final E saved = repository.save(entity);
        // if(isCashed){
        // cash.stream().filter(e -> e.getUuid() ==
        // saved.getUuid()).findFirst().ifPresent(cashed -> cash.remove(cashed));
        // cash.add(saved);
        // }
        return saved;
    }

    /**
     * Find all Nids of NTypes
     * 
     * @return List of Nids
     */
    public List<String> findAllNidsOfNTypes() {
        return repository.findAllNidsOfNTypes();
    }

    /**
     * Find an Entity by unique field {@code uuid}
     * Try to avoid this method
     * 
     * @param uuid Entity's uuid
     * @return Entity
     */
    public E findEntityByUuid(UUID uuid) {
        if (isCashed) {
            E cashed = cash.stream().filter(e -> e.getUuid().equals(uuid)).findFirst().orElse(null);
            if (cashed != null) {
                return cashed;
            } else {
                initCash();
                return findEntityByUuid(uuid);
            }
        }

        // return repository.findEntityByUuid(uuid.toString());
        return repository.findEntityByUuid(uuid.toString());
    }

    public List<E> findAll() {
        return repository.findAll();
    }

    public void deleteEntityByUuidWithDetach(UUID uuid) {
        if (isCashed) {
            cash.stream().filter(e -> e.getUuid().equals(uuid)).findFirst().ifPresent(cashed -> cash.remove(cashed));
        }
        repository.deleteEntityByUuidWithDetach(uuid.toString());
    }

    public Long countRelationships(String eid) {
        return repository.countRelationships(eid);
    }

    /**
     * Set a property String {@code value} with a name = {@code propertyName} of the
     * Entity with the uuid = {@code uuid}
     * 
     * @param uuid          Entity's uuid
     * @param propertyName  property's name
     * @param propertyValue property's String value
     */
    public void setPropertyStringValue(UUID uuid, String propertyName, String propertyValue) {
        repository.setStringValue(uuid.toString(), propertyName, propertyValue);
    }

    /**
     * Get a property String value with a name = {@code propertyName} of the Entity
     * with the uuid = {@code uuid}
     * 
     * @param uuid         Entity's uuid
     * @param propertyName property's name
     * @return property's String value
     */
    public String getPropertyStringValue(UUID uuid, String propertyName) {
        return repository.getStringValue(uuid.toString(), propertyName);
    }

    /**
     * Set a property Boolean {@code value} with a name = {@code propertyName} of
     * the Entity with the uuid = {@code uuid}
     * 
     * @param uuid          Entity's uuid
     * @param propertyName  property's name
     * @param propertyValue property's Boolean value
     */
    public void setPropertyBooleanValue(UUID uuid, String propertyName, Boolean propertyValue) {
        repository.setBooleanValue(uuid.toString(), propertyName, propertyValue);
    }

    /**
     * Get a property Boolean value with a name = {@code propertyName} of the Entity
     * with the uuid = {@code uuid}
     * 
     * @param uuid         Entity's uuid
     * @param propertyName property's name
     * @return property's Boolean value
     */
    public Boolean getPropertyBooleanValue(UUID uuid, String propertyName) {
        return repository.getBooleanValue(uuid.toString(), propertyName);
    }

    /**
     * Delete all relationships with {@code  relationshipType} between two Entities
     * 
     * @param firstEntityUuid  First Entity's uuid
     * @param secondEntityUuid Second Entity's uuid
     * @param relationshipType Relationship's type
     */
    public void detachEntityFromAnotherEntityWithRelationshipType(UUID firstEntityUuid, UUID secondEntityUuid,
            String relationshipType) {
        repository.detachEntityFromAnotherEntityWithRelationshipType(firstEntityUuid.toString(),
                secondEntityUuid.toString(), relationshipType);
    }

    // /**
    // * Find an Entity's UUID by nid
    // * @param nid Entity's nid
    // * @return Entity's Eid in UUID value, or null
    // */
    // public abstract UUID findUuidByNid(String nid);
    // String eid = repository.findUuidByNid(nid);
    // return eid != null ? UUID.fromString(eid) : null;

    // /**
    // * Find an Entity's Nid (Numista id) by UUID (Eid field)
    // * @param uuid Entity's UUID (Eid field)
    // * @return Entity's Nid, or null
    // */
    // public abstract String findNidByUuid(UUID uuid);
    // return repository.findNidByEid(uuid.toString());
    // }

}
