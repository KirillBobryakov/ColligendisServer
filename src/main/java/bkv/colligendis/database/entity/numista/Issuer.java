package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

@Node("ISSUER")
public class Issuer extends AbstractEntity {

    private String code;
    private String name;

    public Issuer() {
    }

    public Issuer(String name) {
        this.name = name;
    }

    public Issuer(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
