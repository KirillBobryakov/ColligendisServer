package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("MINT")
public class Mint extends AbstractEntity {

    public static final String HAS_MINTMARK = "HAS_MINTMARK";


    private String nid;
    private String fullName;

    @Relationship(type = HAS_MINTMARK, direction = Relationship.Direction.OUTGOING)
    private List<Mintmark> mintmarks = new ArrayList<>();


    //old properties start
    private String name;
    private String place;
    private int operationStartYear;
    private int operationEndYear;
    private String website;
    private String photoSymbol;
    private String numistaURL;
    //old properties end


    public Mint() {
    }

    public Mint(String name, String numistaURL) {
        this.name = name;
        this.numistaURL = numistaURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getOperationStartYear() {
        return operationStartYear;
    }

    public void setOperationStartYear(int operationStartYear) {
        this.operationStartYear = operationStartYear;
    }

    public int getOperationEndYear() {
        return operationEndYear;
    }

    public void setOperationEndYear(int operationEndYear) {
        this.operationEndYear = operationEndYear;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhotoSymbol() {
        return photoSymbol;
    }

    public void setPhotoSymbol(String photoSymbol) {
        this.photoSymbol = photoSymbol;
    }

    public String getNumistaURL() {
        return numistaURL;
    }

    public void setNumistaURL(String numistaURL) {
        this.numistaURL = numistaURL;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Mintmark> getMintmarks() {
        return mintmarks;
    }

    public void setMintmarks(List<Mintmark> mintmarks) {
        this.mintmarks = mintmarks;
    }
}
