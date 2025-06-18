package bkv.colligendis.database.entity.features;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.entity.numista.Calendar;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;


/**
 * Dynasty, house, extended period, or any other group of ruling authorities
 *
 * Information takes from <a href="https://en.numista.com/help/add-or-modify-a-ruling-authority-in-the-catalogue-192.html">Numista</a>
 */
@Node("YEAR")
@Data
@EqualsAndHashCode(callSuper=false)
public class Year extends AbstractEntity {

    public static final String TO_NUMBER_IN = "TO_NUMBER_IN";
    public static final String MATCH_UP_TO = "MATCH_UP_TO";

    private Integer value;

    @Relationship(type = TO_NUMBER_IN, direction = Relationship.Direction.OUTGOING)
    private Calendar calendar;

    @Relationship(type = MATCH_UP_TO, direction = Relationship.Direction.OUTGOING)
    private ArrayList<Year> sameYears = new ArrayList<>();

    public Year(Integer value, Calendar calendar) {
        this.value = value;
        this.calendar = calendar;
    }



}
