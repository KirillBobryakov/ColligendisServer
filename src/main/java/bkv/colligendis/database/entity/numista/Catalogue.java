package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.data.neo4j.core.schema.Node;

@Node("CATALOGUE")
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Catalogue extends AbstractEntity {

    private String nid;
    private String code;
    private String bibliography;

//    private String code;
//    private String authors;
//    private String title;
//    private String edition;
//    private String publisher;
//    private String publicationLocation;
//    private String publicationYear;
//    private String ISBN10;
//    private String ISBN13;


    public Catalogue() {
    }

    public Catalogue(String nid, String code) {
        this.nid = nid;
        this.code = code;
    }

    public Catalogue(String nid, String code, String bibliography) {
        this.nid = nid;
        this.code = code;
        this.bibliography = bibliography;
    }
}
