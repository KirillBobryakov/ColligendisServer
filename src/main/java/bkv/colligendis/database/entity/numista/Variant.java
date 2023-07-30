package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.entity.item.VariantDifference;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("VARIANT")
public class Variant extends AbstractEntity {


    public static final String WITH_MARK = "WITH_MARK";
    public static final String WITH_SPECIFIED_MINT = "WITH_SPECIFIED_MINT";

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


    @Relationship(type = HAS_DIFFERENCE, direction = Relationship.Direction.OUTGOING)
    private VariantDifference variantDifference;


    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public Boolean getDated() {
        return isDated;
    }

    public void setDated(Boolean dated) {
        isDated = dated;
    }

    public Integer getMaxYear() {
        return maxYear;
    }

    public void setMaxYear(Integer maxYear) {
        this.maxYear = maxYear;
    }

    public Integer getMinYear() {
        return minYear;
    }

    public void setMinYear(Integer minYear) {
        this.minYear = minYear;
    }

    public Integer getGregorianYear() {
        return gregorianYear;
    }

    public void setGregorianYear(Integer gregorianYear) {
        this.gregorianYear = gregorianYear;
    }

    public Integer getDateYear() {
        return dateYear;
    }

    public void setDateYear(Integer dateYear) {
        this.dateYear = dateYear;
    }

    public Integer getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(Integer dateMonth) {
        this.dateMonth = dateMonth;
    }

    public Integer getDateDay() {
        return dateDay;
    }

    public void setDateDay(Integer dateDay) {
        this.dateDay = dateDay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getYearGregorian() {
        return yearGregorian;
    }

    public void setYearGregorian(int yearGregorian) {
        this.yearGregorian = yearGregorian;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Mint getMint() {
        return mint;
    }

    public void setMint(Mint mint) {
        this.mint = mint;
    }

    public SpecifiedMint getSpecifiedMint() {
        return specifiedMint;
    }

    public void setSpecifiedMint(SpecifiedMint specifiedMint) {
        this.specifiedMint = specifiedMint;
    }

    public VariantDifference getVariantDifference() {
        return variantDifference;
    }

    public void setVariantDifference(VariantDifference variantDifference) {
        this.variantDifference = variantDifference;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }

    public int getMintage() {
        return mintage;
    }

    public void setMintage(int mintage) {
        this.mintage = mintage;
    }

//    public Integer getGregorianMaxYear() {
//        return gregorianMaxYear;
//    }

//    public void setGregorianMaxYear(Integer gregorianMaxYear) {
//        this.gregorianMaxYear = gregorianMaxYear;
//    }

//    public Integer getGregorianMinYear() {
//        return gregorianMinYear;
//    }

//    public void setGregorianMinYear(Integer gregorianMinYear) {
//        this.gregorianMinYear = gregorianMinYear;
//    }
}
