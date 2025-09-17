package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.entity.features.Year;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Node("VARIANT")
@Data
public class Variant extends AbstractEntity {

    public static final String WITH_MARK = "WITH_MARK";
    public static final String WITH_SPECIFIED_MINT = "WITH_SPECIFIED_MINT";
    public static final String MILLESIME_AT = "MILLESIME_AT";
    public static final String MILLESIME_FROM = "MILLESIME_FROM";
    public static final String MILLESIME_TILL = "MILLESIME_TILL";

    private String nid;

    private boolean isDated;

    // private Integer gregorianMaxYear;
    // Gregorian max year
    private Integer maxYear;

    // private Integer gregorianMinYear;

    // Gregorian min year
    private Integer minYear;

    private Integer gregorianYear;
    private Integer dateYear;
    private Integer dateMonth;
    private Integer dateDay;

    // Use this field for mark the variant as a stale
    private boolean deletedOnNumista;

    // mint
    @Relationship(type = WITH_SPECIFIED_MINT, direction = Relationship.Direction.OUTGOING)
    private SpecifiedMint specifiedMint;

    @Relationship(type = WITH_MARK, direction = Relationship.Direction.OUTGOING)
    private List<Mark> marks = new ArrayList<>();

    private int mintage;

    private String comment;

    public Variant() {
    }

    public Variant(String nid) {
        this.nid = nid;
    }

    public static final String UNDER_MINT = "UNDER_MINT";
    public static final String HAS_DIFFERENCE = "HAS_DIFFERENCE";

    private String date;
    private int yearGregorian;

    @Relationship(type = UNDER_MINT, direction = Relationship.Direction.OUTGOING)
    private Mint mint;

    // @Relationship(type = HAS_DIFFERENCE, direction =
    // Relationship.Direction.OUTGOING)
    // private VariantDifference variantDifference;

    @Relationship(type = MILLESIME_AT, direction = Relationship.Direction.OUTGOING)
    private Year year;

    @Relationship(type = MILLESIME_FROM, direction = Relationship.Direction.OUTGOING)
    private Year yearFrom;

    @Relationship(type = MILLESIME_TILL, direction = Relationship.Direction.OUTGOING)
    private Year yearTill;

    private String mintLetter;

    @Relationship(type = NType.HAS_REFERENCE, direction = Relationship.Direction.OUTGOING)
    private List<CatalogueReference> catalogueReferences = new ArrayList<>();

}
