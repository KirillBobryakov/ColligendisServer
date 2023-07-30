package bkv.colligendis.database.entity.item;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.entity.features.*;
import bkv.colligendis.database.entity.features.Value;
import bkv.colligendis.database.entity.numista.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("ITEM")
public class Item extends AbstractEntity {

    public static final String HAS_COMM_ISSUE_SERIES = "HAS_COMM_ISSUE_SERIES";
    public static final String HAS_OBVERSE = "HAS_OBVERSE";
    public static final String HAS_REVERSE = "HAS_REVERSE";
    public static final String HAS_EDGE = "HAS_EDGE";

    public static final String HAS_MINTS = "HAS_MINTS"; //todo change in HAS_MINT
    public static final String HAS_MARK = "HAS_MARK";
    public static final String HAS_VARIANT = "HAS_VARIANT";
    public static final String HAS_COMMENT = "HAS_COMMENT";


    private ITEM_TYPE itemType;

    private String name;


    // Properties START

    @Relationship(type = "TO_TERRITORY", direction = Relationship.Direction.OUTGOING)
    private Territory territory;

    @Relationship(type = "TO_ISSUER", direction = Relationship.Direction.OUTGOING)
    private Issuer issuer;

    @Relationship(type = "TO_PERIOD", direction = Relationship.Direction.OUTGOING)
    private Period period;

    @Relationship(type = "TO_RULER", direction = Relationship.Direction.OUTGOING)
    private Ruler ruler;


    private int startYear;
    private int startYearGregorian;
    private int endYear;
    private int endYearGregorian;

    private String calendar;

    @Relationship(type = "TO_VALUE", direction = Relationship.Direction.OUTGOING)
    private Value value;

    @Relationship(type = "TO_CURRENCY", direction = Relationship.Direction.OUTGOING)
    private Currency currency;

    private String composition;
    private String weight;
    private String diameter;

    private String thickness;
    private String shape;
    private String technique;
    private String orientation;

    private String demonetized;

    private String numistaNumber;
    private String references;


    private String commIssueName;
    @Relationship(type = HAS_COMM_ISSUE_SERIES, direction = Relationship.Direction.OUTGOING)
    private CommIssueSeries commIssueSeries;

    // Properties END


    @Relationship(type = HAS_OBVERSE, direction = Relationship.Direction.OUTGOING)
    private ItemSide obverse;
    @Relationship(type = HAS_REVERSE, direction = Relationship.Direction.OUTGOING)
    private ItemSide reverse;
    @Relationship(type = HAS_EDGE, direction = Relationship.Direction.OUTGOING)
    private ItemSide edge;

    @Relationship(type = HAS_MINTS, direction = Relationship.Direction.OUTGOING)
    private Set<Mint> mints = new HashSet<>();

    @Relationship(type = HAS_MARK, direction = Relationship.Direction.OUTGOING)
    private Set<SpecifiedMint> mintmarks = new HashSet<>();

//    @Relationship(type = HAS_COMMENT, direction = Relationship.Direction.OUTGOING)
//    private Set<CoinComment> coinComments = new HashSet<>();

    private String htmlComments;

    @Relationship(type = HAS_VARIANT, direction = Relationship.Direction.OUTGOING)
    private Set<Variant> variants = new HashSet<>();


    public ITEM_TYPE getItemType() {
        return itemType;
    }

    public void setItemType(ITEM_TYPE itemType) {
        this.itemType = itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Territory getTerritory() {
        return territory;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Ruler getRuler() {
        return ruler;
    }

    public void setRuler(Ruler ruler) {
        this.ruler = ruler;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getStartYearGregorian() {
        return startYearGregorian;
    }

    public void setStartYearGregorian(int startYearGregorian) {
        this.startYearGregorian = startYearGregorian;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getEndYearGregorian() {
        return endYearGregorian;
    }

    public void setEndYearGregorian(int endYearGregorian) {
        this.endYearGregorian = endYearGregorian;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getTechnique() {
        return technique;
    }

    public void setTechnique(String technique) {
        this.technique = technique;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getDemonetized() {
        return demonetized;
    }

    public void setDemonetized(String demonetized) {
        this.demonetized = demonetized;
    }

    public String getNumistaNumber() {
        return numistaNumber;
    }

    public void setNumistaNumber(String numistaNumber) {
        this.numistaNumber = numistaNumber;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getCommIssueName() {
        return commIssueName;
    }

    public void setCommIssueName(String commIssueName) {
        this.commIssueName = commIssueName;
    }

    public CommIssueSeries getCommIssueSeries() {
        return commIssueSeries;
    }

    public void setCommIssueSeries(CommIssueSeries commIssueSeries) {
        this.commIssueSeries = commIssueSeries;
    }

    public ItemSide getObverse() {
        return obverse;
    }

    public void setObverse(ItemSide obverse) {
        this.obverse = obverse;
    }

    public ItemSide getReverse() {
        return reverse;
    }

    public void setReverse(ItemSide reverse) {
        this.reverse = reverse;
    }

    public ItemSide getEdge() {
        return edge;
    }

    public void setEdge(ItemSide edge) {
        this.edge = edge;
    }

    public Set<Mint> getMints() {
        return mints;
    }

    public void setMints(Set<Mint> mints) {
        this.mints = mints;
    }

    public Set<SpecifiedMint> getMarks() {
        return mintmarks;
    }

    public void setMarks(Set<SpecifiedMint> mintmarks) {
        this.mintmarks = mintmarks;
    }

    //    public Set<CoinComment> getCoinComments() {
//        return coinComments;
//    }
//
//    public void setCoinComments(Set<CoinComment> coinComments) {
//        this.coinComments = coinComments;
//    }


    public String getHtmlComments() {
        return htmlComments;
    }

    public void setHtmlComments(String htmlComments) {
        this.htmlComments = htmlComments;
    }

    public Set<Variant> getVariants() {
        return variants;
    }

    public void setVariants(Set<Variant> variants) {
        this.variants = variants;
    }
}
