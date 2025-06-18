package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.entity.item.VariantDifference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Node("VARIANT")
@Data
public class Year extends AbstractEntity {


    public static final String WITH_MARK = "WITH_MARK";
    public static final String WITH_SPECIFIED_MINT = "WITH_SPECIFIED_MINT";
    public static final String MILLESIME_AT = "MILLESIME_AT";

    private String nid;

    private boolean isDated;

//    private Integer gregorianMaxYear;
    //Gregorian max year
    private Integer maxYear;

//    private Integer gregorianMinYear;

    //Gregorian min year
    private Integer minYear;

    private Integer gregorianYear;
    private Integer dateYear;
    private Integer dateMonth;
    private Integer dateDay;


    //mint
    @Relationship(type = WITH_SPECIFIED_MINT, direction = Relationship.Direction.OUTGOING)
    private SpecifiedMint specifiedMint;

    @Relationship(type = WITH_MARK, direction = Relationship.Direction.OUTGOING)
    private List<Mark> marks = new ArrayList<>();

    private int mintage;

    private String comment;


//    public Variant(String nid) {
//        this.nid = nid;
//    }

    public Year(String nid) {
        this.nid = nid;
    }

    public static final String UNDER_MINT = "UNDER_MINT";
    public static final String HAS_DIFFERENCE = "HAS_DIFFERENCE";

    private String date;
    private int yearGregorian;


    @Relationship(type = UNDER_MINT, direction = Relationship.Direction.OUTGOING)
    private Mint mint;


    @Relationship(type = HAS_DIFFERENCE, direction = Relationship.Direction.OUTGOING)
    private VariantDifference variantDifference;


    @Relationship(type = MILLESIME_AT, direction = Relationship.Direction.OUTGOING)
    private bkv.colligendis.database.entity.features.Year year;

    public Boolean getDated() {
        return isDated;
    }

    public void setDated(Boolean dated) {
        isDated = dated;
    }

}
