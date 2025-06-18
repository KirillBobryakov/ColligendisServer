package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("COUNTRY")
@Data
@EqualsAndHashCode(callSuper = true)
public class Country extends AbstractEntity {

    public static final String CONTAINS_CHILD_SUBJECT = "CONTAINS_CHILD_SUBJECT";
    public static final String CONTAINS_ISSUER = "CONTAINS_ISSUER";
    public static final String PARENT_SUBJECT = "PARENT_SUBJECT";
    /**
     * Only English name of countries. Unique field.
     */
    private String name;

    private String numistaCode;

    private List<String> ruAlternativeNames = new ArrayList<>();

    @JsonIgnore
    @Relationship(type = CONTAINS_CHILD_SUBJECT, direction = Relationship.Direction.OUTGOING)
    private ArrayList<Subject> subjects = new ArrayList<>();

    @JsonIgnore
    @Relationship(type = CONTAINS_ISSUER, direction = Relationship.Direction.OUTGOING)
    private ArrayList<Issuer> issuers = new ArrayList<>();


    @Relationship(value = PARENT_SUBJECT, direction = Relationship.Direction.OUTGOING)
    private Subject parentSubject;

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    public Country(String numistaCode, String name) {
        this.numistaCode = numistaCode;
        this.name = name;
    }

}
