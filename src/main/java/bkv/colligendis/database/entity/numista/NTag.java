package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.ArrayList;
import java.util.List;

@Node("NTAG")
public class NTag extends AbstractEntity {

    private String nid;

    private String name;

    private NTag parent;

    private List<NTag> children = new ArrayList<>();


    public NTag(String nid, String name) {
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

    public NTag getParent() {
        return parent;
    }

    public void setParent(NTag parent) {
        this.parent = parent;
    }

    public List<NTag> getChildren() {
        return children;
    }

    public void setChildren(List<NTag> children) {
        this.children = children;
    }
}
