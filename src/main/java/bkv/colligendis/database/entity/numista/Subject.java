package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("SUBJECT")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Subject extends AbstractEntity {

    public static final String PARENT_SUBJECT = "PARENT_SUBJECT";
    public static final String RELATE_TO_COUNTRY = "RELATE_TO_COUNTRY";

    private String numistaCode;
    private String name;

    private List<String> ruAlternativeNames = new ArrayList<>();

    @Relationship(value = RELATE_TO_COUNTRY, direction = Relationship.Direction.OUTGOING)
    private Country country;

    @Relationship(value = PARENT_SUBJECT, direction = Relationship.Direction.OUTGOING)
    private Subject parentSubject;

    @Relationship(value = PARENT_SUBJECT, direction = Relationship.Direction.INCOMING)
    private ArrayList<Subject> childSubjects = new ArrayList<>();

    @Relationship(value = Issuer.PARENT_SUBJECT, direction = Relationship.Direction.INCOMING)
    private ArrayList<Issuer> issuers = new ArrayList<>();

    public Subject() {
    }

    public Subject(String name) {
        this.name = name;
    }

    public Subject(String numistaCode, String name) {
        this.numistaCode = numistaCode;
        this.name = name;
    }

}
