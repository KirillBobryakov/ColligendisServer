package bkv.colligendis.database.entity.item;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

@Node("COMM_ISSUE_SERIES")
public class CommIssueSeries extends AbstractEntity {

    private String name;
    private String numistaURL;

    public CommIssueSeries(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumistaURL() {
        return numistaURL;
    }

    public void setNumistaURL(String numistaURL) {
        this.numistaURL = numistaURL;
    }
}
