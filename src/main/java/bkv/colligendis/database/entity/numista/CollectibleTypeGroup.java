package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;


@Node("COLLECTIBLE_TYPE_GROUP")
public class CollectibleTypeGroup extends AbstractEntity {

    @Setter
    @Getter
    private String name;


    public CollectibleTypeGroup(String name) {
        this.name = name;
    }


}
