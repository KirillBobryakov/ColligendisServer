package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.neo4j.core.schema.Node;

@Node("CALENDAR")
@Data
@EqualsAndHashCode(callSuper = true)
public class Calendar extends AbstractEntity {
    public static final String LABEL = "CALENDAR";

    public static final String GREGORIAN_CODE = "gregorien";
    public static final String ISLAMI_HIJRI = "musulman";
    public static final String IRANIAN_PERSIAN = "persan";

    private String code;
    private String name;

    private Integer toGregorianShift;

    public Calendar(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
