package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.service.numista.CompositionPartType;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Node("COMPOSITION")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Composition extends AbstractEntity {

    public static final String HAS_COMPOSITION_TYPE = "HAS_COMPOSITION_TYPE";
    public static final String HAS_PART1_FROM = "HAS_PART1_FROM";
    public static final String HAS_PART2_FROM = "HAS_PART2_FROM";
    public static final String HAS_PART3_FROM = "HAS_PART3_FROM";
    public static final String HAS_PART4_FROM = "HAS_PART4_FROM";

    @Relationship(value = "HAS_COMPOSITION_TYPE", direction = Relationship.Direction.OUTGOING)
    private CompositionType compositionType;

    @Relationship(value = HAS_PART1_FROM, direction = Relationship.Direction.OUTGOING)
    private Metal part1Metal;
    private CompositionPartType part1Type;
    private String part1MetalFineness;

    @Relationship(value = HAS_PART2_FROM, direction = Relationship.Direction.OUTGOING)
    private Metal part2Metal;
    private CompositionPartType part2Type;
    private String part2MetalFineness;

    @Relationship(value = HAS_PART3_FROM, direction = Relationship.Direction.OUTGOING)
    private Metal part3Metal;
    private CompositionPartType part3Type;
    private String part3MetalFineness;

    @Relationship(value = HAS_PART4_FROM, direction = Relationship.Direction.OUTGOING)
    private Metal part4Metal;
    private CompositionPartType part4Type;
    private String part4MetalFineness;

    private String compositionAdditionalDetails;

}
