package bkv.colligendis.database.service;

import bkv.colligendis.database.entity.AbstractEntity;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.UUID;

public interface AbstractNeo4jRepository<E extends AbstractEntity> extends Neo4jRepository<E, UUID> {

    E findByUuid(String uuid);

    @Query("MATCH (n) WHERE $entityLabel IN labels(n) AND n[$propertyName] = $propertyValue RETURN n.uuid")
    String findUuidByPropertyStringValue(String entityLabel, String propertyName, String propertyValue);

    @Query("MATCH (n)-[r]-() WHERE n.uuid=$uuid RETURN count(r)")
    long countRelationships(String uuid);

    @Query("MATCH (n {uuid:$uuid}) DETACH DELETE n")
    void deleteEntityByUuidWithDetach(String uuid);

    @Query("MATCH (fr {uuid:$fromNodeUuid})-[r]->(to) WHERE type(r) = $relationshipType AND $secondEntityLabel IN labels(to) RETURN to.uuid")
    List<String> getAllOutgoingRelatedNodesUUIDs(String fromNodeUuid, String relationshipType,
            String secondEntityLabel);

    @Query("MATCH (fr {uuid:$fromNodeUuid})<-[r]-(to) WHERE type(r) = $relationshipType AND $secondEntityLabel IN labels(to) RETURN to.uuid")
    List<String> getAllIncomingRelatedNodesUUIDs(String fromNodeUuid, String relationshipType,
            String secondEntityLabel);

    @Query("MATCH (from {uuid:$fromNodeUuid})-[r]->(to) WHERE type(r) = $relationshipType AND $secondEntityLabel IN labels(to) RETURN to.uuid")
    String getSingleRelatedNodeUUID(String fromNodeUuid, String relationshipType, String secondEntityLabel);

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
    @Query("MATCH (f {uuid:$fromNodeUuid})-[r]->(t {uuid:$toNodeUuid}) WITH count(r) > 0 AS cond CALL apoc.do.when(cond, 'RETURN true as res', 'MATCH (f {uuid:$fromNodeUuid}), (t {uuid:$toNodeUuid}) CALL apoc.create.relationship(f, $relationshipType, {}, t) YIELD rel RETURN count(rel) > 0 AS res', {fromNodeUuid:$fromNodeUuid, toNodeUuid:$toNodeUuid, relationshipType:$relationshipType}) YIELD value RETURN value.res")
    boolean createSingleRelationshipToNode(String fromNodeUuid, String toNodeUuid, String relationshipType);

    @Query("MATCH (f {uuid:$fromNodeUuid})-[r]->(t {uuid:$toNodeUuid}) WHERE type(r)=$relationshipType return COALESCE(count(r), 0) > 0")
    Boolean hasSingleRelationshipToNode(String fromNodeUuid, String toNodeUuid, String relationshipType);

    @Query("MATCH (from {uuid:$fromNodeUuid})-[r]->(to) WHERE type(r) = $relationshipType AND $toNodeLabel IN labels(to) AND to[$propertyName] = $propertyValue return COALESCE(count(r), 0)>0")
    Boolean hasSingleRelationshipToNodeWithPropertyValue(String fromNodeUuid, String toNodeLabel, String propertyName,
            String propertyValue, String relationshipType);

    /**
     * Check has any relationship with type (@code relationshipType) from Entity
     * with {@code fromNodeUuid} to any Entity
     * 
     * @param fromNodeUuid     from Entity's uuid
     * @param relationshipType Relationship's type
     * @return {@code true} if has any relationship, {@code false} - if not
     */
    @Query("MATCH (f {uuid:$fromNodeUuid})-[r]->(to) WHERE type(r)=$relationshipType AND $secondEntityLabel IN labels(to) return COALESCE(count(r), 0)>0")
    boolean hasAnyRelationshipWithType(String fromNodeUuid, String relationshipType, String secondEntityLabel);

    @Query("MATCH (n {uuid:$uuid}) RETURN n[$propertyName]")
    String getStringValue(String uuid, String propertyName);

    @Query("MATCH (n {uuid:$uuid}) SET n[$propertyName] = $propertyValue")
    void setStringValue(String uuid, String propertyName, String propertyValue);

    @Query("MATCH (n {uuid:$uuid}) RETURN n[$propertyName]")
    Boolean getBooleanValue(String uuid, String propertyName);

    @Query("MATCH (n {uuid:$uuid}) SET n[$propertyName] = toBoolean($propertyValue)")
    void setBooleanValue(String uuid, String propertyName, Boolean propertyValue);

    @Query("MATCH (n {uuid:$uuid}) RETURN n[$propertyName]")
    Integer getIntValue(String uuid, String propertyName);

    @Query("MATCH (n {uuid:$uuid}) SET n[$propertyName] = $propertyValue")
    void setIntValue(String uuid, String propertyName, Integer propertyValue);

    @Query("MATCH (n {uuid:$uuid}) RETURN n[$propertyName]")
    Double getFloatValue(String uuid, String propertyName);

    @Query("MATCH (n {uuid:$uuid}) SET n[$propertyName] = $propertyValue")
    void setFloatValue(String uuid, String propertyName, Double propertyValue);

    /**
     * Delete all relationships with {@code  relationshipType} between two Entities
     * 
     * @param firstEntityUuid  First Entity's uuid
     * @param secondEntityUuid Second Entity's uuid
     * @param relationshipType Relationship's type
     */
    @Query("MATCH (f {uuid:$firstEntityUuid})-[r]-(s {uuid:$secondEntityUuid}) WHERE type(r) = $relationshipType DELETE r")
    void detachEntityFromAnotherEntityWithRelationshipType(String firstEntityUuid, String secondEntityUuid,
            String relationshipType);

    @Query("MATCH (from {uuid:$entityUuid})-[r]->(s) WHERE type(r) = $relationshipType AND $secondEntityLabel IN labels(s) DELETE r")
    void detachAllOutgoingRelationshipsWithRelationshipTypeAndSecondEntityLabel(String entityUuid,
            String relationshipType,
            String secondEntityLabel);

    @Query("MATCH (from {uuid:$entityUuid})-[r]-(s) WHERE type(r) = $relationshipType DELETE r")
    void detachAllEntityWithRelationshipType(String entityUuid, String relationshipType);

}
