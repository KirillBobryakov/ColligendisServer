package bkv.colligendis.database.entity.piece;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("COIN_VARIANT")
public class CoinVariant extends AbstractEntity {

    public static final String UNDER_MINT = "UNDER_MINT";

    @Relationship(type = PieceInformation.HAS_VARIANT, direction = Relationship.Direction.INCOMING)
    private PieceInformation pieceInformation;

    private int year;
    private String date;
    private int yearGregorian;
    private String tirage;
    private String comment;

    @Relationship(type = UNDER_MINT, direction = Relationship.Direction.OUTGOING)
    private Mint mint;


    public PieceInformation getCoinInformation() {
        return pieceInformation;
    }

    public void setCoinInformation(PieceInformation pieceInformation) {
        this.pieceInformation = pieceInformation;
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
}
