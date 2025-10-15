package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.neo4j.core.schema.Node;

@Node("ARTIST")
@Data
@EqualsAndHashCode(callSuper = true)
public class Artist extends AbstractEntity {

    public static final String LABEL = "ARTIST";

    private String nid;
    private String name;

}
