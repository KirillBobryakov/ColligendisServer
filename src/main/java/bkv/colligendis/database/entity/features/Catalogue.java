package bkv.colligendis.database.entity.features;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

@Node("CATALOGUE")
public class Catalogue extends AbstractEntity {

    private String code;
    private String authors;
    private String title;
    private String edition;
    private String publisher;
    private String publicationLocation;
    private String publicationYear;
    private String ISBN10;
    private String ISBN13;


}
