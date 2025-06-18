package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("GRADING_MARK")
@Data
@EqualsAndHashCode(callSuper=true)
public class GradingMark extends AbstractEntity {

    public static final String RELATE_TO_GRADING_MARK = "RELATE_TO_GRADING_MARK";

    //value
    public String nid;


    @Relationship(type = GradingService.RELATE_TO_GRADING_SERVICE, direction = Relationship.Direction.OUTGOING)
    private GradingService gradingService;

    private String value;


}
