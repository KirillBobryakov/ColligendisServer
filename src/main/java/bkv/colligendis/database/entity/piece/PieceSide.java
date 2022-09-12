package bkv.colligendis.database.entity.piece;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

@Node("PIECE_SIDE")
public class PieceSide extends AbstractEntity {

    public static final String OBVERSE = "Obverse";
    public static final String REVERSE = "Reverse";
    public static final String EDGE = "Edge";

    private String sideType;

    private String photoLink;
    private String description;

    private String script;
    private String lettering;
    private String unabridgedLegend;
    private String translation;

    private String engraver;
    private String designer;

    public PieceSide(String sideType) {
        this.sideType = sideType;
    }

    public String getSideType() {
        return sideType;
    }

    public void setSideType(String sideType) {
        if(sideType.equals(OBVERSE) || sideType.equals(REVERSE) || sideType.equals(EDGE)){
            this.sideType = sideType;
        }
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getLettering() {
        return lettering;
    }

    public void setLettering(String lettering) {
        this.lettering = lettering;
    }

    public String getUnabridgedLegend() {
        return unabridgedLegend;
    }

    public void setUnabridgedLegend(String unabridgedLegend) {
        this.unabridgedLegend = unabridgedLegend;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getEngraver() {
        return engraver;
    }

    public void setEngraver(String engraver) {
        this.engraver = engraver;
    }

    public String getDesigner() {
        return designer;
    }

    public void setDesigner(String designer) {
        this.designer = designer;
    }
}
