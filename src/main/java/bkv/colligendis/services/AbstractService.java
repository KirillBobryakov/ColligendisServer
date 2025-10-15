package bkv.colligendis.services;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class AbstractService<E extends AbstractEntity, R extends AbstractNeo4jRepository<E>> {

    protected final R repository;

    protected AbstractService(R repository) {
        this.repository = repository;
    }

    public E save(E entity) {
        final E saved = repository.save(entity);
        return saved;
    }

    public E findEntityByUuid(UUID uuid) {
        return repository.findByUuid(uuid.toString());
    }

    public List<E> findAll() {
        return repository.findAll();
    }

    public UUID findUuidByPropertyStringValue(String entityLabel, String propertyName, String propertyValue) {
        String uuid = repository.findUuidByPropertyStringValue(entityLabel, propertyName, propertyValue);
        return uuid != null ? UUID.fromString(uuid) : null;
    }

    public void deleteEntityByUuidWithDetach(UUID uuid) {
        repository.deleteEntityByUuidWithDetach(uuid.toString());
    }

    public Long countRelationships(String eid) {
        return repository.countRelationships(eid);
    }

    public <T> T getPropertyValue(UUID uuid, String propertyName, Class<T> type) {
        return switch (type.getSimpleName()) {
            case "String" -> type.cast(repository.getStringValue(uuid.toString(), propertyName));
            case "Boolean" -> type.cast(repository.getBooleanValue(uuid.toString(), propertyName));
            case "Integer" -> type.cast(repository.getIntValue(uuid.toString(), propertyName));
            case "Float" -> type.cast(repository.getFloatValue(uuid.toString(), propertyName));
            default -> null;
        };
    }

    protected boolean hasSingleRelationshipToNode(UUID fromEntityUuid, UUID toEntityUuid, String relationshipType) {
        return repository.hasSingleRelationshipToNode(fromEntityUuid.toString(), toEntityUuid.toString(),
                relationshipType);
    }

    protected <T> boolean comparePropertyValue(UUID uuid, String propertyName, Object propertyValue, Class<T> type) {
        return switch (propertyValue) {
            case String s -> {
                String existingValue = repository.getStringValue(uuid.toString(), propertyName);
                yield existingValue != null && existingValue.equals(s);
            }
            case Boolean b -> {
                Boolean existingValue = repository.getBooleanValue(uuid.toString(), propertyName);
                yield existingValue != null && existingValue.equals(b);
            }
            case Integer i -> {
                Integer existingValue = repository.getIntValue(uuid.toString(), propertyName);
                yield existingValue != null && existingValue.equals(i);
            }
            case Float f -> {
                Double existingValue = repository.getFloatValue(uuid.toString(), propertyName);
                yield existingValue != null && Math.abs(existingValue - f) < 0.0001;
            }
            case null, default -> false;
        };
    }

    protected boolean setPropertyStringValue(UUID uuid, String propertyName, String propertyValue) {
        repository.setStringValue(uuid.toString(), propertyName, propertyValue);
        return true;
    }

    public boolean setPropertyBooleanValue(UUID uuid, String propertyName, Boolean propertyValue) {
        repository.setBooleanValue(uuid.toString(), propertyName, propertyValue);
        return true;
    }

    public boolean setPropertyIntValue(UUID uuid, String propertyName, Integer propertyValue) {
        repository.setIntValue(uuid.toString(), propertyName, propertyValue);
        return true;
    }

    public boolean setPropertyFloatValue(UUID uuid, String propertyName, Float propertyValue) {
        repository.setFloatValue(uuid.toString(), propertyName, propertyValue.doubleValue());
        return true;
    }

    public UUID getSingleRelatedNodeUUID(UUID fromNodeUuid, String relationshipType, String secondEntityLabel) {
        String uuid = repository.getSingleRelatedNodeUUID(fromNodeUuid.toString(), relationshipType, secondEntityLabel);
        return uuid != null ? UUID.fromString(uuid) : null;
    }

    public List<UUID> getAllOutgoingRelatedNodesUUIDs(UUID fromNodeUuid, String relationshipType,
            String secondEntityLabel) {
        return repository.getAllOutgoingRelatedNodesUUIDs(fromNodeUuid.toString(), relationshipType, secondEntityLabel)
                .stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }

    public List<UUID> getAllIncomingRelatedNodesUUIDs(UUID fromNodeUuid, String relationshipType,
            String secondEntityLabel) {
        return repository.getAllIncomingRelatedNodesUUIDs(fromNodeUuid.toString(), relationshipType, secondEntityLabel)
                .stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }

    public void setSingleOutgoingRelationshipToNode(UUID fromNodeUuid, UUID toNodeUuid, String relationshipType,
            String secondEntityLabel) {
        if (repository.hasAnyRelationshipWithType(fromNodeUuid.toString(), relationshipType, secondEntityLabel)) {
            repository.detachAllOutgoingRelationshipsWithRelationshipTypeAndSecondEntityLabel(fromNodeUuid.toString(),
                    relationshipType, secondEntityLabel);
        }

        repository.createSingleRelationshipToNode(fromNodeUuid.toString(), toNodeUuid.toString(), relationshipType);
    }

    public void addSingleOutgoingRelationshipToNode(UUID fromNodeUuid, UUID toNodeUuid, String relationshipType) {
        repository.createSingleRelationshipToNode(fromNodeUuid.toString(), toNodeUuid.toString(), relationshipType);
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

    /**
     * Delete all relationships with {@code  relationshipType} from an Entity with
     * {@code entityUuid} to any Entity
     * 
     * @param entityUuid       Entity's uuid
     * @param relationshipType Relationship's type
     */
    public void detachAllEntityWithRelationshipType(UUID entityUuid, String relationshipType) {
        repository.detachAllEntityWithRelationshipType(entityUuid.toString(), relationshipType);
    }

    protected boolean equateFistListToSecondList(List<UUID> first, List<UUID> second,
            BiFunction<UUID, UUID, Boolean> detachFunction, BiFunction<UUID, UUID, Boolean> addFunction,
            UUID mainNodeUuid) {

        Set<UUID> firstSet = new HashSet<>(first);
        Set<UUID> secondSet = new HashSet<>(second);

        AtomicBoolean wasChanged = new AtomicBoolean(false);

        firstSet.stream()
                .filter(firstUuid -> !secondSet.contains(firstUuid))
                .forEach(firstUuid -> {
                    wasChanged.set(true);
                    detachFunction.apply(mainNodeUuid, firstUuid);
                });
        secondSet.stream()
                .filter(secondUuid -> !firstSet.contains(secondUuid))
                .forEach(secondUuid -> {
                    wasChanged.set(true);
                    addFunction.apply(mainNodeUuid, secondUuid);
                });

        return wasChanged.get();

    }
}
