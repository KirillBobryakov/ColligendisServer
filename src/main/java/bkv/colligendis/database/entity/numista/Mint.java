package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("MINT")
@Data
@EqualsAndHashCode(callSuper = true)
public class Mint extends AbstractEntity {

    public static final String HAS_MINTMARK = "HAS_MINTMARK";

    private String nid;
    private String fullName;

    private String latitude;
    private String longitude;

    @Relationship(type = HAS_MINTMARK, direction = Relationship.Direction.OUTGOING)
    private List<Mintmark> mintmarks = new ArrayList<>();

    // old properties start
    private String name;
    private String place;
    private int operationStartYear;
    private int operationEndYear;
    private String website;
    private String photoSymbol;
    private String numistaURL;
    // old properties end

    public Mint() {
    }

    public Mint(String name, String numistaURL) {
        this.name = name;
        this.numistaURL = numistaURL;
    }

    // Explicit getters/setters for latitude and longitude (Lombok may not generate
    // them properly)
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
