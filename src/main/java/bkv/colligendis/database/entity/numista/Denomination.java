package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("DENOMINATION")
@Data
@EqualsAndHashCode(callSuper = true)
public class Denomination extends AbstractEntity {

    public static final String UNDER_CURRENCY = "UNDER_CURRENCY";

    private String nid;
    private String fullName;
    private String name;

    private Float numericValue;

    private Boolean isActual;

    @Relationship(type = UNDER_CURRENCY, direction = Relationship.Direction.OUTGOING)
    private Currency currency;

}
