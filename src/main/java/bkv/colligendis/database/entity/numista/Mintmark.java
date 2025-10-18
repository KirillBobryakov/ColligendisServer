package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.neo4j.core.schema.Node;

@Node("MINTMARK")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class Mintmark extends AbstractEntity {
    public static final String LABEL = "MINTMARK";

    private String nid;
    private String picture;

}
