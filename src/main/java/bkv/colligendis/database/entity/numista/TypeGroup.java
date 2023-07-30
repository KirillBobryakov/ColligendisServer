package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;


@Node("TYPE_GROUP")
public class TypeGroup extends AbstractEntity {

    private String name;

    public TypeGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
