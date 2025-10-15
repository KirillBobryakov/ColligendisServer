package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("CATALOGUE_REFERENCE")
@Data
@EqualsAndHashCode(callSuper = true)
public class CatalogueReference extends AbstractEntity {

    public static final String LABEL = "CATALOGUE_REFERENCE";

    public static final String REFERENCE_FROM = "REFERENCE_FROM";

    @Relationship(type = REFERENCE_FROM, direction = Relationship.Direction.OUTGOING)
    private Catalogue catalogue;
    private String number;

    public CatalogueReference() {
    }

    public CatalogueReference(Catalogue catalogue, String number) {
        this.catalogue = catalogue;
        this.number = number;
    }

}
