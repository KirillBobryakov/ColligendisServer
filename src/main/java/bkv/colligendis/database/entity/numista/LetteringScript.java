package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;


@Node("LETTERING_SCRIPT")
public class LetteringScript extends AbstractEntity {

    private String nid;
    private String name;

    public LetteringScript(String nid, String name) {
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
}
