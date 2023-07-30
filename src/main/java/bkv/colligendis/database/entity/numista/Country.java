package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;

@Node("COUNTRY")
public class Country extends AbstractEntity {

    public static final String CONTAINS_CHILD_SUBJECT = "CONTAINS_CHILD_SUBJECT";
    public static final String CONTAINS_ISSUER = "CONTAINS_ISSUER";

    private String name;

    @JsonIgnore
    @Relationship(type = CONTAINS_CHILD_SUBJECT, direction = Relationship.Direction.OUTGOING)
    private ArrayList<Subject> subjects = new ArrayList<>();

    @JsonIgnore
    @Relationship(type = CONTAINS_ISSUER, direction = Relationship.Direction.OUTGOING)
    private ArrayList<Issuer> issuers = new ArrayList<>();


    public Country(String name) {
        this.name = name;
    }

    public Country() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<Subject> subjects) {
        this.subjects = subjects;
    }

    public ArrayList<Issuer> getIssuers() {
        return issuers;
    }

    public void setIssuers(ArrayList<Issuer> issuers) {
        this.issuers = issuers;
    }
}
