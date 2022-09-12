package bkv.colligendis.database.entity.features;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

@Node("VALUE")
public class Value extends AbstractEntity {

    private String name;
    private float nominal;
    private float rateToCurrency;

    public Value(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getNominal() {
        return nominal;
    }

    public void setNominal(float nominal) {
        this.nominal = nominal;
    }

    public float getRateToCurrency() {
        return rateToCurrency;
    }

    public void setRateToCurrency(float rateToCurrency) {
        this.rateToCurrency = rateToCurrency;
    }
}
