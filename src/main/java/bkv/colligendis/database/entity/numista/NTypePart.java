package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("NTYPE_PART")
public class NTypePart extends AbstractEntity {

//    public static final String ENGRAVED_BY = "ENGRAVED_BY";
//    public static final String DESIGNED_BY = "DESIGNED_BY";
    public static final String WRITE_ON_SCRIPT = "WRITE_ON_SCRIPT";

//    @Relationship(type = ENGRAVED_BY, direction = Relationship.Direction.OUTGOING)
    private List<String> engravers = new ArrayList<>();

//    @Relationship(type = DESIGNED_BY, direction = Relationship.Direction.OUTGOING)
    private List<String> designers = new ArrayList<>();

    private String description;
    private String lettering;

    @Relationship(type = WRITE_ON_SCRIPT, direction = Relationship.Direction.OUTGOING)
    private List<LetteringScript> letteringScripts = new ArrayList<>();

    private String unabridgedLegend;
    private String letteringTranslation;
    private String picture;



    public List<String> getEngravers() {
        return engravers;
    }

    public void setEngravers(List<String> engravers) {
        this.engravers = engravers;
    }

    public List<String> getDesigners() {
        return designers;
    }

    public void setDesigners(List<String> designers) {
        this.designers = designers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLettering() {
        return lettering;
    }

    public void setLettering(String lettering) {
        this.lettering = lettering;
    }

    public List<LetteringScript> getLetteringScripts() {
        return letteringScripts;
    }

    public void setLetteringScripts(List<LetteringScript> letteringScripts) {
        this.letteringScripts = letteringScripts;
    }

    public String getUnabridgedLegend() {
        return unabridgedLegend;
    }

    public void setUnabridgedLegend(String unabridgedLegend) {
        this.unabridgedLegend = unabridgedLegend;
    }

    public String getLetteringTranslation() {
        return letteringTranslation;
    }

    public void setLetteringTranslation(String letteringTranslation) {
        this.letteringTranslation = letteringTranslation;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
