package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Composition;
import bkv.colligendis.database.entity.numista.CompositionType;
import bkv.colligendis.database.entity.numista.Metal;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CompositionService extends AbstractService<Composition, CompositionRepository> {
    public CompositionService(CompositionRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing

    public void setCompositionType(UUID compositionUuid, UUID compositionTypeUuid) {
        setSingleOutgoingRelationshipToNode(compositionUuid, compositionTypeUuid, Composition.HAS_COMPOSITION_TYPE,
                CompositionType.LABEL);
    }

    public UUID getCompositionTypeUuid(UUID compositionUuid) {
        return getSingleRelatedNodeUUID(compositionUuid, Composition.HAS_COMPOSITION_TYPE, CompositionType.LABEL);
    }

    // part1

    public UUID getPart1MetalUuid(UUID compositionUuid) {
        return getSingleRelatedNodeUUID(compositionUuid, Composition.PART1_IS_MADE_OF, Metal.LABEL);
    }

    public void setPart1Metal(UUID compositionUuid, UUID metalUuid) {
        setSingleOutgoingRelationshipToNode(compositionUuid, metalUuid, Composition.PART1_IS_MADE_OF, Metal.LABEL);
    }

    public boolean comparePart1Type(UUID compositionUuid, CompositionPartType compositionType) {
        return comparePropertyValue(compositionUuid, "part1Type", compositionType.name(),
                compositionType.name().getClass());
    }

    public boolean setPart1Type(UUID compositionUuid, CompositionPartType compositionType) {
        return setPropertyStringValue(compositionUuid, "part1Type", compositionType.name());
    }

    public boolean comparePart1MetalFineness(UUID compositionUuid, String metalFineness) {
        return comparePropertyValue(compositionUuid, "part1MetalFineness", metalFineness, String.class);
    }

    public boolean setPart1MetalFineness(UUID compositionUuid, String metalFineness) {
        return setPropertyStringValue(compositionUuid, "part1MetalFineness", metalFineness);
    }

    public void clearPart1(UUID compositionUuid) {
        detachAllEntityWithRelationshipType(compositionUuid, Composition.PART1_IS_MADE_OF);
        setPropertyStringValue(compositionUuid, "part1Type", null);
        setPropertyStringValue(compositionUuid, "part1MetalFineness", null);
    }

    // part2

    public UUID getPart2MetalUuid(UUID compositionUuid) {
        return getSingleRelatedNodeUUID(compositionUuid, Composition.PART2_IS_MADE_OF, Metal.LABEL);
    }

    public void setPart2Metal(UUID compositionUuid, UUID metalUuid) {
        setSingleOutgoingRelationshipToNode(compositionUuid, metalUuid, Composition.PART2_IS_MADE_OF, Metal.LABEL);
    }

    public boolean comparePart2Type(UUID compositionUuid, CompositionPartType compositionType) {
        return comparePropertyValue(compositionUuid, "part2Type", compositionType.name(),
                compositionType.name().getClass());
    }

    public boolean setPart2Type(UUID compositionUuid, CompositionPartType compositionType) {
        return setPropertyStringValue(compositionUuid, "part2Type", compositionType.name());
    }

    public boolean comparePart2MetalFineness(UUID compositionUuid, String metalFineness) {
        return comparePropertyValue(compositionUuid, "part2MetalFineness", metalFineness, String.class);
    }

    public boolean setPart2MetalFineness(UUID compositionUuid, String metalFineness) {
        return setPropertyStringValue(compositionUuid, "part2MetalFineness", metalFineness);
    }

    public void clearPart2(UUID compositionUuid) {
        detachAllEntityWithRelationshipType(compositionUuid, Composition.PART2_IS_MADE_OF);
        setPropertyStringValue(compositionUuid, "part2Type", null);
        setPropertyStringValue(compositionUuid, "part2MetalFineness", null);
    }

    // part3

    public UUID getPart3MetalUuid(UUID compositionUuid) {
        return getSingleRelatedNodeUUID(compositionUuid, Composition.PART3_IS_MADE_OF, Metal.LABEL);
    }

    public void setPart3Metal(UUID compositionUuid, UUID metalUuid) {
        setSingleOutgoingRelationshipToNode(compositionUuid, metalUuid, Composition.PART3_IS_MADE_OF, Metal.LABEL);
    }

    public boolean comparePart3Type(UUID compositionUuid, CompositionPartType compositionType) {
        return comparePropertyValue(compositionUuid, "part3Type", compositionType.name(),
                compositionType.name().getClass());
    }

    public boolean setPart3Type(UUID compositionUuid, CompositionPartType compositionType) {
        return setPropertyStringValue(compositionUuid, "part3Type", compositionType.name());
    }

    public boolean comparePart3MetalFineness(UUID compositionUuid, String metalFineness) {
        return comparePropertyValue(compositionUuid, "part3MetalFineness", metalFineness, String.class);
    }

    public boolean setPart3MetalFineness(UUID compositionUuid, String metalFineness) {
        return setPropertyStringValue(compositionUuid, "part3MetalFineness", metalFineness);
    }

    public void clearPart3(UUID compositionUuid) {
        detachAllEntityWithRelationshipType(compositionUuid, Composition.PART3_IS_MADE_OF);
        setPropertyStringValue(compositionUuid, "part3Type", null);
        setPropertyStringValue(compositionUuid, "part3MetalFineness", null);
    }

    // part4

    public UUID getPart4MetalUuid(UUID compositionUuid) {
        return getSingleRelatedNodeUUID(compositionUuid, Composition.PART4_IS_MADE_OF, Metal.LABEL);
    }

    public void setPart4Metal(UUID compositionUuid, UUID metalUuid) {
        setSingleOutgoingRelationshipToNode(compositionUuid, metalUuid, Composition.PART4_IS_MADE_OF, Metal.LABEL);
    }

    public boolean comparePart4Type(UUID compositionUuid, CompositionPartType compositionType) {
        return comparePropertyValue(compositionUuid, "part4Type", compositionType.name(),
                compositionType.name().getClass());
    }

    public boolean setPart4Type(UUID compositionUuid, CompositionPartType compositionType) {
        return setPropertyStringValue(compositionUuid, "part4Type", compositionType.name());
    }

    public boolean comparePart4MetalFineness(UUID compositionUuid, String metalFineness) {
        return comparePropertyValue(compositionUuid, "part4MetalFineness", metalFineness, String.class);
    }

    public boolean setPart4MetalFineness(UUID compositionUuid, String metalFineness) {
        return setPropertyStringValue(compositionUuid, "part4MetalFineness", metalFineness);
    }

    public void clearPart4(UUID compositionUuid) {
        detachAllEntityWithRelationshipType(compositionUuid, Composition.PART4_IS_MADE_OF);
        setPropertyStringValue(compositionUuid, "part4Type", null);
        setPropertyStringValue(compositionUuid, "part4MetalFineness", null);
    }

    public boolean compareCompositionAdditionalDetails(UUID compositionUuid, String compositionAdditionalDetails) {
        return comparePropertyValue(compositionUuid, "compositionAdditionalDetails", compositionAdditionalDetails,
                String.class);
    }

    public boolean setCompositionAdditionalDetails(UUID compositionUuid, String compositionAdditionalDetails) {
        return setPropertyStringValue(compositionUuid, "compositionAdditionalDetails", compositionAdditionalDetails);
    }

    // End: Methods for Numista parsing
}
