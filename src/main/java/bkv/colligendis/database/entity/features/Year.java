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
@Node("PERIOD")
@Data
@EqualsAndHashCode(callSuper=false)
public class Period extends AbstractEntity {


    private String name;
    private int startYear;
    private int startYearGregorian;
    private int endYear;
    private int endYearGregorian;

    private Integer fromYear;
    private Integer tillYear;

    @Relationship(type = Ruler.DURING_PERIOD, direction = Relationship.Direction.INCOMING)
    private Ruler ruler = new ArrayList<>();


}
