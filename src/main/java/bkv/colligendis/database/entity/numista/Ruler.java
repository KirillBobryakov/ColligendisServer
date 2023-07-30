package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Arrays;
import java.util.List;

@Node("RULLER")
public class Ruler extends AbstractEntity {

    public static final List<String> RULERS = Arrays.asList(
            "Period", "Archduke", "Ban", "Bishop", "Caesar", "Camerlengo", "Doge", "Duchess", "Duke", "Emir", "Emperor", "Empress",
            "Grandmaster", "Grand duchess", "Grand duke", "Grand Prince",
            "Khan", "King", "Landgrave", "Lord", "Margrave", "Margravine", "Master",
            "Pope", "President", "Prime minister", "Prince", "Prince-archbishop", "Prince elector", "Prince-bishop",
            "Queen" , "Regent", "Ruling authority", "Shah", "Sultan", "Tsar", "Voivode");

    private String nid;
    private String name;
    private String rulerType;

    public Ruler(String nid, String name) {
        this.nid = nid;
        this.name = name;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRulerType() {
        return rulerType;
    }

    public void setRulerType(String rulerType) {
        this.rulerType = rulerType;
    }
}
