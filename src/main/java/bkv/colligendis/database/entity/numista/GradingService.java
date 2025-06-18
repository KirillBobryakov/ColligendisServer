package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.neo4j.core.schema.Node;

@Node("GRADING_SERVICE")
@Data
@EqualsAndHashCode(callSuper=true)
public class GradingService extends AbstractEntity {

    public static final String RELATE_TO_GRADING_SERVICE = "RELATE_TO_GRADING_SERVICE";

    //value
    public String nid;

    public String value;

}
