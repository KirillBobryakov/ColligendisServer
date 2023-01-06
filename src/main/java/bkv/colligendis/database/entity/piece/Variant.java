package bkv.colligendis.database.entity.piece;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("VARIANT")
public class Variant extends AbstractEntity {

    public static final String UNDER_MINT = "UNDER_MINT";

    @Relationship(type = Item.HAS_VARIANT, direction = Relationship.Direction.INCOMING)
    private Item item;

    private int year;
    private String date;
    private int yearGregorian;
    private String tirage;
    private String comment;


    //Link to image with differences in variant
    private String linkToDifference;

    @Relationship(type = UNDER_MINT, direction = Relationship.Direction.OUTGOING)
    private Mint mint;


    public Item getCoinInformation() {
        return item;
    }

    public void setCoinInformation(Item item) {
        this.item = item;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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

    public String getTirage() {
        return tirage;
    }

    public void setTirage(String tirage) {
        this.tirage = tirage;
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

    public String getLinkToDifference() {
        return linkToDifference;
    }

    public void setLinkToDifference(String linkToDifference) {
        this.linkToDifference = linkToDifference;
    }
}
