package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("CATALOGUE_REFERENCE")
public class CatalogueReference extends AbstractEntity {

    public static final String REFERENCE_TO = "REFERENCE_TO";

    @Relationship(type = REFERENCE_TO, direction = Relationship.Direction.OUTGOING)
    private Catalogue catalogue;
    private String number;

    public CatalogueReference(Catalogue catalogue, String number) {
        this.catalogue = catalogue;
        this.number = number;
    }

    public Catalogue getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(Catalogue catalogue) {
        this.catalogue = catalogue;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
