package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;


/**
 * Dynasty, house, extended period, or any other group of ruling authorities
 *
 * Information takes from <a href="https://en.numista.com/help/add-or-modify-a-ruling-authority-in-the-catalogue-192.html">Numista</a>
 */
@Node("PERIOD_YEAR")
@Data
@EqualsAndHashCode(callSuper=false)
public class PeriodYear extends AbstractEntity {

    private String name;

    @Relationship(type = Ruler.GROUP_BY, direction = Relationship.Direction.INCOMING)
    private List<Ruler> rulers = new ArrayList<>();


    public PeriodYear(String name) {
        this.name = name;
    }


}
