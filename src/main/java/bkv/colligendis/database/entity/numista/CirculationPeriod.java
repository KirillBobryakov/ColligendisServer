package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;


/**
 * This Class use for circulation period of Currency
 * If {@code endGregorianYear} is null - the currency still is in circulation
 */
@Node("CIRCULATION_PERIOD")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CirculationPeriod extends AbstractEntity {

    private Integer startGregorianYear;
    private Integer endGregorianYear;

}
