package bkv.colligendis.database.service;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.UUID;

public interface AbstractNeo4jRepository<E extends AbstractEntity> extends Neo4jRepository<E, UUID> {

    @Query("MATCH (n:NTYPE) RETURN n.nid")
    List<String> findAllNidsOfNTypes();

    /**
     * Find an Entity by unique field {@code uuid}
     * 
     * @param uuid Entity's uuid
     * @return Entity
     */
    // @Query("MATCH (n) WHERE n.uuid = $uuid RETURN n")
    // E findEntityByUuid(String uuid);
    E findByUuid(String uuid);

    // /**
    // * Find an Entity's Eid by nid
    // * @param nid Entity's nid
    // * @return Entity's Eid in String value, or null
    // */
    // @Query("MATCH (n {nid:$nid}) RETURN n.eid")
    // String findUuidByNid(String nid);

    // /**
    // * Find an Entity's Nid (Numista id) by Eid
    // * @param eid Entity's eid
    // * @return Entity's Nid, or null
    // */
    // @Query("MATCH (n {eid:$eid}) RETURN n.nid")
    // String findNidByEid(String eid);

    @Query("MATCH (n)-[r]-() WHERE n.uuid=$uuid RETURN count(r)")
    long countRelationships(String uuid);

    @Query("MATCH (n) WHERE n.uuid = $uuid DETACH DELETE n")
    void deleteEntityByUuidWithDetach(String uuid);

    /**
     * Check a relationship from a node with an uuid = {@code fromNodeUuid} to a
     * node with an uuid = {@code toNodeUuid}. If there is not a relationship
     * between nodes, create it with relationship's Type = {@code relationshipLabel}
     *
     * @param fromNodeUuid     String value of start node's uuid
     * @param toNodeUuid       String value of end node's uuid
     * @param relationshipType Type of creating relationship
     * @return {@code true} If a relationship was presented, or was created;
     *         {@code false} There was not a relationship, and it was not created
     */
    @Query("MATCH (f)-[r]->(t) WHERE f.uuid=$fromNodeUuid AND t.uuid=$toNodeUuid WITH count(r) > 0 AS cond CALL apoc.do.when(cond, 'RETURN true as res', 'MATCH (f), (t) WHERE f.uuid=$fromNodeUuid AND t.uuid=$toNodeUuid CALL apoc.create.relationship(f, $relationshipType, {}, t) YIELD rel RETURN count(rel) > 0 AS res', {fromNodeUuid:$fromNodeUuid, toNodeUuid:$toNodeUuid, relationshipType:$relationshipType}) YIELD value RETURN value.res")
    Boolean createSingleRelationshipToNode(String fromNodeUuid, String toNodeUuid, String relationshipType);

    @Query("MATCH (f)-[r]->(t) WHERE f.uuid=$fromNodeUuid and t.uuid=$toNodeUuid and type(r)=$relationshipType return COALESCE(count(r), 0)>0")
    Boolean hasSingleRelationshipToNode(String fromNodeUuid, String toNodeUuid, String relationshipType);

    /**
     * Check has any relationship with type (@code relationshipType) from Entity
     * with {@code fromNodeUuid} to any Entity
     * 
     * @param fromNodeUuid     from Entity's uuid
     * @param relationshipType Relationship's type
     * @return {@code true} if has any relationship, {@code false} - if not
     */
    @Query("MATCH (f)-[r]->() WHERE f.uuid=$fromNodeUuid and type(r)=$relationshipType return COALESCE(count(r), 0)>0")
    Boolean hasAnyRelationshipWithType(String fromNodeUuid, String relationshipType);

    /**
     * Get a property String value with a name = {@code propertyName} of the Entity
     * with the uuid = {@code uuid}
     * 
     * @param uuid         Entity's uuid
     * @param propertyName property's name
     * @return property's String value
     */
    @Query("MATCH (n) WHERE n.uuid = $uuid RETURN n.$propertyName")
    String getStringValue(String uuid, String propertyName);

    /**
     * Set a property String {@code value} with a name = {@code propertyName} of the
     * Entity with the uuid = {@code uuid}
     * 
     * @param uuid          Entity's uuid
     * @param propertyName  property's name
     * @param propertyValue property's String value
     */
    @Query("MATCH (n) WHERE n.uuid = $uuid SET n.$propertyName = $propertyValue")
    void setStringValue(String uuid, String propertyName, String propertyValue);

    /**
     * Get a property Boolean value with a name = {@code propertyName} of the Entity
     * with the uuid = {@code uuid}
     * 
     * @param uuid         Entity's uuid
     * @param propertyName property's name
     * @return property's Boolean value
     */
    @Query("MATCH (n) WHERE n.uuid = $uuid RETURN n.$propertyName")
    Boolean getBooleanValue(String uuid, String propertyName);

    /**
     * Set a property Boolean {@code value} with a name = {@code propertyName} of
     * the Entity with the uuid = {@code uuid}
     * 
     * @param uuid          Entity's uuid
     * @param propertyName  property's name
     * @param propertyValue property's Boolean value
     */
    @Query("MATCH (n) WHERE n.uuid = $uuid SET n.$propertyName = toBoolean($propertyValue)")
    void setBooleanValue(String uuid, String propertyName, Boolean propertyValue);

    /**
     * Delete all relationships with {@code  relationshipType} between two Entities
     * 
     * @param firstEntityUuid  First Entity's uuid
     * @param secondEntityUuid Second Entity's uuid
     * @param relationshipType Relationship's type
     */
    @Query("MATCH (f)-[r]-(s) WHERE f.uuid = $firstEntityUuid AND s.uuid = $secondEntityUuid AND type(r)=$relationshipType DELETE r")
    void detachEntityFromAnotherEntityWithRelationshipType(String firstEntityUuid, String secondEntityUuid,
            String relationshipType);

}
