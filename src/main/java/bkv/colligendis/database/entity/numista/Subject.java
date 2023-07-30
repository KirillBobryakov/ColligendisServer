package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;

@Node("SUBJECT")
public class Subject extends AbstractEntity {


    private String name;

    @Relationship(value = Country.CONTAINS_CHILD_SUBJECT, direction = Relationship.Direction.INCOMING)
    private Country country;


    @Relationship(value = Country.CONTAINS_CHILD_SUBJECT, direction = Relationship.Direction.INCOMING)
    private Subject parentSubject;

    @Relationship(value = Country.CONTAINS_CHILD_SUBJECT, direction = Relationship.Direction.OUTGOING)
    private ArrayList<Subject> childSubjects = new ArrayList<>();


    @Relationship(value = Country.CONTAINS_ISSUER, direction = Relationship.Direction.OUTGOING)
    private ArrayList<Issuer> issuers = new ArrayList<>();

    public Subject() {
    }

    public Subject(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Subject getParentSubject() {
        return parentSubject;
    }

    public void setParentSubject(Subject parentSubject) {
        this.parentSubject = parentSubject;
    }

    public ArrayList<Subject> getChildSubjects() {
        return childSubjects;
    }

    public void setChildSubjects(ArrayList<Subject> childSubjects) {
        this.childSubjects = childSubjects;
    }

    public ArrayList<Issuer> getIssuers() {
        return issuers;
    }

    public void setIssuers(ArrayList<Issuer> issuers) {
        this.issuers = issuers;
    }
}
