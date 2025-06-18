package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("GRADING_DESIGNATION")
@Data
public class GradingDesignation extends AbstractEntity {


    public static final String RELATE_TO_GRADING_DESIGNATION = "RELATE_TO_GRADING_DESIGNATION";

    //value
    public String nid;


    //data-select2-id
    public String data_select2_id;


    public String value;

    @Relationship(type = GradingService.RELATE_TO_GRADING_SERVICE, direction = Relationship.Direction.OUTGOING)
    private GradingService gradingService;


}
