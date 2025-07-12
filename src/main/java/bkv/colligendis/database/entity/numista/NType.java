package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("NTYPE")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class NType extends AbstractEntity {

    public static final String UNDER_CATEGORY = "UNDER_CATEGORY";
    public static final String ISSUED_BY = "ISSUED_BY";
    public static final String DURING_OF_RULER = "DURING_OF_RULER";
    public static final String PROVIDED_BY = "PROVIDED_BY";

    public static final String DENOMINATED_IN = "DENOMINATED_IN";
    public static final String UNDER_CURRENCY = "UNDER_CURRENCY";
    public static final String HAS_COLLECTIBLE_TYPE = "HAS_COLLECTIBLE_TYPE";
    public static final String COMMEMORATE_FOR = "COMMEMORATE_FOR";
    public static final String HAS_SERIES = "HAS_SERIES";

    public static final String HAS_REFERENCE = "HAS_REFERENCE";
    public static final String HAS_COMPOSITION = "HAS_COMPOSITION";
    public static final String HAS_SHAPE = "HAS_SHAPE";
    public static final String WITH_TECHNIQUE = "WITH_TECHNIQUE";

    public static final String HAS_OBVERSE = "HAS_OBVERSE";
    public static final String HAS_REVERSE = "HAS_REVERSE";
    public static final String HAS_EDGE = "HAS_EDGE";
    public static final String HAS_SPECIFIED_MINT = "HAS_SPECIFIED_MINT";
    public static final String HAS_WATERMARK = "HAS_WATERMARK";
    public static final String PRINTED_BY = "PRINTED_BY";
    public static final String REFERENCED_WITH = "REFERENCED_WITH";
    public static final String DATED_WITHIN = "DATED_WITHIN";

    public static final String VARIANTS = "VARIANTS";

    @Relationship(type = UNDER_CATEGORY, direction = Relationship.Direction.OUTGOING)
    private Category category;

    /**
     * Integer unique value from Numista
     * Unique only in Numista+NTYPE
     */
    @ToString.Include
    private String nid;

    // Start Overall information

    /*
     * Title for NType is unique field!
     */
    @ToString.Include
    private String title;

    @Relationship(type = ISSUED_BY, direction = Relationship.Direction.OUTGOING)
    private Issuer issuer;

    @Relationship(type = DURING_OF_RULER, direction = Relationship.Direction.OUTGOING)
    private List<Ruler> rulers = new ArrayList<>();

    @Relationship(type = ISSUED_BY, direction = Relationship.Direction.OUTGOING)
    private List<IssuingEntity> issuingEntities = new ArrayList<>();

    private String valueText;
    private String valueNumeric;

    @Relationship(type = DENOMINATED_IN, direction = Relationship.Direction.OUTGOING)
    private Denomination denomination;

    @Relationship(type = UNDER_CURRENCY, direction = Relationship.Direction.OUTGOING)
    private Currency currency;

    @Relationship(type = HAS_COLLECTIBLE_TYPE, direction = Relationship.Direction.OUTGOING)
    private CollectibleType collectibleType;

    @Relationship(type = COMMEMORATE_FOR, direction = Relationship.Direction.OUTGOING)
    private CommemoratedEvent commemoratedEvent;

    @Relationship(type = HAS_SERIES, direction = Relationship.Direction.OUTGOING)
    private Series series;

    /*
     * Select the appropriate option:
     * Unknown: for coins that were never in circulation, such as patterns, and for
     * coins with an uncertain legal tender status.
     * No: for coins that are currently accepted as legal tender
     * Yes: for coins that are no longer legal tender.
     * 
     * Date: for demonetized coins, record the date of the withdrawal of the legal
     * tender status as yyyy-mm-dd. Note that this date may be different from the
     * date of the retirement from circulation. Should the precise day not be known,
     * “00” can be used:
     * 2001-12-31
     * 1875-00-00
     */
    /*
     * Has only 3 values:
     * 0 - No, Didn't demonetized
     * 1 - Yes, demonetized
     * 2 - Unknown
     */
    private String demonetized;
    private String demonetizationYear;
    private String demonetizationMonth;
    private String demonetizationDay;

    @Relationship(type = HAS_REFERENCE, direction = Relationship.Direction.OUTGOING)
    private List<CatalogueReference> catalogueReferences = new ArrayList<>();

    // End Overall information

    // Start Technical data

    @Relationship(type = HAS_COMPOSITION, direction = Relationship.Direction.OUTGOING)
    private Composition composition;

    @Relationship(type = HAS_SHAPE, direction = Relationship.Direction.OUTGOING)
    private Shape shape;

    private String shapeAdditionalDetails;

    private String weight;

    private String size;
    private String size2;
    private String thickness;

    @Relationship(type = WITH_TECHNIQUE, direction = Relationship.Direction.OUTGOING)
    private List<Technique> techniques = new ArrayList<>();

    private String techniqueAdditionalDetails;

    private String alignment;

    // End Technical data

    @Relationship(type = HAS_OBVERSE, direction = Relationship.Direction.OUTGOING)
    private NTypePart obverse;

    @Relationship(type = HAS_REVERSE, direction = Relationship.Direction.OUTGOING)
    private NTypePart reverse;

    @Relationship(type = HAS_EDGE, direction = Relationship.Direction.OUTGOING)
    private NTypePart edge;

    @Relationship(type = HAS_SPECIFIED_MINT, direction = Relationship.Direction.OUTGOING)
    private List<SpecifiedMint> specifiedMints = new ArrayList<>();

    @Relationship(type = HAS_WATERMARK, direction = Relationship.Direction.OUTGOING)
    private NTypePart watermark;

    @Relationship(type = PRINTED_BY, direction = Relationship.Direction.OUTGOING)
    private List<Printer> printers = new ArrayList<>();

    private String comments;

    @Relationship(type = REFERENCED_WITH, direction = Relationship.Direction.OUTGOING)
    private List<NTag> nTags = new ArrayList<>();

    private String links;

    @Relationship(type = DATED_WITHIN, direction = Relationship.Direction.OUTGOING)
    private Calendar calendar;

    @Relationship(type = VARIANTS, direction = Relationship.Direction.OUTGOING)
    private List<Variant> variants = new ArrayList<>();

    private Boolean isActual;

    public NType(String nid, String title) {
        this.nid = nid;
        this.title = title;
    }

}
