package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.service.numista.CompositionPartType;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Node("COMPOSITION")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Composition extends AbstractEntity {
    public static final String LABEL = "COMPOSITION";

    public static final String HAS_COMPOSITION_TYPE = "HAS_COMPOSITION_TYPE";
    public static final String PART1_IS_MADE_OF = "PART1_IS_MADE_OF";
    public static final String PART2_IS_MADE_OF = "PART2_IS_MADE_OF";
    public static final String PART3_IS_MADE_OF = "PART3_IS_MADE_OF";
    public static final String PART4_IS_MADE_OF = "PART4_IS_MADE_OF";

    @Relationship(value = HAS_COMPOSITION_TYPE, direction = Relationship.Direction.OUTGOING)
    private CompositionType compositionType;

    @Relationship(value = PART1_IS_MADE_OF, direction = Relationship.Direction.OUTGOING)
    private Metal part1Metal;
    private CompositionPartType part1Type;
    private String part1MetalFineness;

    @Relationship(value = PART2_IS_MADE_OF, direction = Relationship.Direction.OUTGOING)
    private Metal part2Metal;
    private CompositionPartType part2Type;
    private String part2MetalFineness;

    @Relationship(value = PART3_IS_MADE_OF, direction = Relationship.Direction.OUTGOING)
    private Metal part3Metal;
    private CompositionPartType part3Type;
    private String part3MetalFineness;

    @Relationship(value = PART4_IS_MADE_OF, direction = Relationship.Direction.OUTGOING)
    private Metal part4Metal;
    private CompositionPartType part4Type;
    private String part4MetalFineness;

    private String compositionAdditionalDetails;

}
