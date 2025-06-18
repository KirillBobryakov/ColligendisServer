package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("GRADING")
@Data
public class Grading extends AbstractEntity {

    public static final String HAS_GRADING = "HAS_GRADING";


    @Relationship(type = GradingService.RELATE_TO_GRADING_SERVICE, direction = Relationship.Direction.OUTGOING)
    private GradingService gradingService;

    @Relationship(type = GradingMark.RELATE_TO_GRADING_MARK, direction = Relationship.Direction.OUTGOING)
    private GradingMark gradingMark;

    @Relationship(type = GradingDesignation.RELATE_TO_GRADING_DESIGNATION, direction = Relationship.Direction.OUTGOING)
    private GradingDesignation gradingDesignation;

    public String slabNumber;

    // Green, Gold
    public String cacSticker;

    public String strike;

    public String surface;


}
