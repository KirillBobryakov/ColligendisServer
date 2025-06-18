package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;


/**
 * Dynasty, house, extended period, or any other group of ruling authorities
 *
 * Information takes from <a href="https://en.numista.com/help/add-or-modify-a-ruling-authority-in-the-catalogue-192.html">Numista</a>
 */
@Node("RULER_GROUP")
@Data
@EqualsAndHashCode(callSuper=false)
public class RulerGroup extends AbstractEntity {

    private String nid;
    private String name;

//    @Relationship(type = Ruler.GROUP_BY, direction = Relationship.Direction.INCOMING)
//    private List<Ruler> rulers = new ArrayList<>();


    public RulerGroup(String nid, String name) {
        this.nid = nid;
        this.name = name;
    }


}
