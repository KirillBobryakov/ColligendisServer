package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

@Node("CALENDAR")
public class Calendar extends AbstractEntity {

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getToGregorianShift() {
        return toGregorianShift;
    }

    public void setToGregorianShift(Integer toGregorianShift) {
        this.toGregorianShift = toGregorianShift;
    }
}
