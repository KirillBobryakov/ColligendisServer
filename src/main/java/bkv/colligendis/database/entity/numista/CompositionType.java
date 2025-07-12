package bkv.colligendis.database.entity.numista;

import org.springframework.data.neo4j.core.schema.Node;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Node("COMPOSITION_TYPE")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class CompositionType extends AbstractEntity {

    private String code;
    private String name;

    /**
     * @param code - code from Numista
     * @param name - name from Numista
     */
    public CompositionType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // plain("Single material"),
    // plated("Plated metal"),
    // clad("Clad metal"),
    // bimetallic("Bimetallic"),
    // bimetallic_plated("Bimetallic with plated metal centre"),
    // bimetallic_plated_ring("Bimetallic with plated metal ring"),
    // bimetallic_plated_plated("Bimetallic with plated centre and ring"),
    // bimetallic_clad("Bimetallic with clad metal centre"),
    // trimetallic("Trimetallic"),
    // other("Other");

}
