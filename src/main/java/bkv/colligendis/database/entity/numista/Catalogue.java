package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

@Node("CATALOGUE")
public class Catalogue extends AbstractEntity {

    private String nid;
    private String code;

//    private String code;
//    private String authors;
//    private String title;
//    private String edition;
//    private String publisher;
//    private String publicationLocation;
//    private String publicationYear;
//    private String ISBN10;
//    private String ISBN13;


    public Catalogue(String nid, String code) {
        this.nid = nid;
        this.code = code;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
