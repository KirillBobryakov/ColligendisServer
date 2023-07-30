package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("NTYPE")
public class NType extends AbstractEntity {

    public static final String UNDER_CATEGORY = "UNDER_CATEGORY";
    public static final String ISSUED_BY = "ISSUED_BY";
    public static final String DURING_OF_RULER = "DURING_OF_RULER";
    public static final String PROVIDED_BY = "PROVIDED_BY";
    public static final String UNDER_CURRENCY = "UNDER_CURRENCY";
    public static final String HAS_TYPE = "HAS_TYPE";
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



    @Relationship(type = UNDER_CATEGORY, direction = Relationship.Direction.OUTGOING)
    private Category category;
    private String nid;


    //Start Overall information
    private String title;

    @Relationship(type = ISSUED_BY, direction = Relationship.Direction.OUTGOING)
    private Issuer issuer;

    @Relationship(type = DURING_OF_RULER, direction = Relationship.Direction.OUTGOING)
    private List<Ruler> rulers = new ArrayList<>();

    @Relationship(type = PROVIDED_BY, direction = Relationship.Direction.OUTGOING)
    private IssuingEntity issuingEntity;

    private String valueText;
    private String valueNumeric;

    @Relationship(type = UNDER_CURRENCY, direction = Relationship.Direction.OUTGOING)
    private Currency currency;

    @Relationship(type = HAS_TYPE, direction = Relationship.Direction.OUTGOING)
    private Type type;


    @Relationship(type = COMMEMORATE_FOR, direction = Relationship.Direction.OUTGOING)
    private CommemoratedEvent commemoratedEvent;

    @Relationship(type = HAS_SERIES, direction = Relationship.Direction.OUTGOING)
    private Series series;

    private Boolean isDemonetized;
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


    // Start Obverse (head)
    @Relationship(type = HAS_OBVERSE, direction = Relationship.Direction.OUTGOING)
    private NTypePart obverse;
    // End Obverse (head)

    // Start Reverse (back)
    @Relationship(type = HAS_REVERSE, direction = Relationship.Direction.OUTGOING)
    private NTypePart reverse;
    // End Reverse (back)

    // Start Edge
    @Relationship(type = HAS_EDGE, direction = Relationship.Direction.OUTGOING)
    private NTypePart edge;
    // End Edge

    // Start Mint(s)
    @Relationship(type = HAS_SPECIFIED_MINT, direction = Relationship.Direction.OUTGOING)
    private List<SpecifiedMint> specifiedMints = new ArrayList<>();

    // End Mint(s)

    // Start Watermark
    @Relationship(type = HAS_WATERMARK, direction = Relationship.Direction.OUTGOING)
    private NTypePart watermark;
    // End Watermark

    // Start Printer(s)

    @Relationship(type = PRINTED_BY, direction = Relationship.Direction.OUTGOING)
    private List<Printer> printers = new ArrayList<>();

    // End Printer(s)

    // Start Comments
    private String comments;
    // End Comments

    // Start References

    @Relationship(type = REFERENCED_WITH, direction = Relationship.Direction.OUTGOING)
    private List<NTag> nTags = new ArrayList<>();

    private String links;

    // End References

    // Start Mintage | Varieties

    @Relationship(type = DATED_WITHIN, direction = Relationship.Direction.OUTGOING)
    private Calendar calendar;

    private List<Variant> variants = new ArrayList<>();

    // End Mintage | Varieties


    public NType(String nid) {
        this.nid = nid;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public List<Ruler> getRulers() {
        return rulers;
    }

    public void setRulers(List<Ruler> rulers) {
        this.rulers = rulers;
    }

    public IssuingEntity getIssuingEntity() {
        return issuingEntity;
    }

    public void setIssuingEntity(IssuingEntity issuingEntity) {
        this.issuingEntity = issuingEntity;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public String getValueNumeric() {
        return valueNumeric;
    }

    public void setValueNumeric(String valueNumeric) {
        this.valueNumeric = valueNumeric;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public CommemoratedEvent getCommemoratedEvent() {
        return commemoratedEvent;
    }

    public void setCommemoratedEvent(CommemoratedEvent commemoratedEvent) {
        this.commemoratedEvent = commemoratedEvent;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Boolean getDemonetized() {
        return isDemonetized;
    }

    public void setDemonetized(Boolean demonetized) {
        isDemonetized = demonetized;
    }

    public String getDemonetizationYear() {
        return demonetizationYear;
    }

    public void setDemonetizationYear(String demonetizationYear) {
        this.demonetizationYear = demonetizationYear;
    }

    public String getDemonetizationMonth() {
        return demonetizationMonth;
    }

    public void setDemonetizationMonth(String demonetizationMonth) {
        this.demonetizationMonth = demonetizationMonth;
    }

    public String getDemonetizationDay() {
        return demonetizationDay;
    }

    public void setDemonetizationDay(String demonetizationDay) {
        this.demonetizationDay = demonetizationDay;
    }

    public List<CatalogueReference> getCatalogueReferences() {
        return catalogueReferences;
    }

    public void setCatalogueReferences(List<CatalogueReference> catalogueReferences) {
        this.catalogueReferences = catalogueReferences;
    }

    public Composition getComposition() {
        return composition;
    }

    public void setComposition(Composition composition) {
        this.composition = composition;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public String getShapeAdditionalDetails() {
        return shapeAdditionalDetails;
    }

    public void setShapeAdditionalDetails(String shapeAdditionalDetails) {
        this.shapeAdditionalDetails = shapeAdditionalDetails;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize2() {
        return size2;
    }

    public void setSize2(String size2) {
        this.size2 = size2;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public List<Technique> getTechniques() {
        return techniques;
    }

    public void setTechniques(List<Technique> techniques) {
        this.techniques = techniques;
    }

    public String getTechniqueAdditionalDetails() {
        return techniqueAdditionalDetails;
    }

    public void setTechniqueAdditionalDetails(String techniqueAdditionalDetails) {
        this.techniqueAdditionalDetails = techniqueAdditionalDetails;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public NTypePart getObverse() {
        return obverse;
    }

    public void setObverse(NTypePart obverse) {
        this.obverse = obverse;
    }

    public NTypePart getReverse() {
        return reverse;
    }

    public void setReverse(NTypePart reverse) {
        this.reverse = reverse;
    }

    public NTypePart getEdge() {
        return edge;
    }

    public void setEdge(NTypePart edge) {
        this.edge = edge;
    }

    public NTypePart getWatermark() {
        return watermark;
    }

    public void setWatermark(NTypePart watermark) {
        this.watermark = watermark;
    }

    public List<SpecifiedMint> getSpecifiedMints() {
        return specifiedMints;
    }

    public void setSpecifiedMints(List<SpecifiedMint> specifiedMints) {
        this.specifiedMints = specifiedMints;
    }

    public List<Printer> getPrinters() {
        return printers;
    }

    public void setPrinters(List<Printer> printers) {
        this.printers = printers;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<NTag> getnTags() {
        return nTags;
    }

    public void setnTags(List<NTag> nTags) {
        this.nTags = nTags;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }
}
