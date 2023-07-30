package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("SPECIFIED_MINT")
public class SpecifiedMint extends AbstractEntity {


    public static final String WITH_MINTMARK = "WITH_MINTMARK";
    public static final String WITH_MINT = "WITH_MINT";

    private String identifier;

    @Relationship(type = WITH_MINT, direction = Relationship.Direction.OUTGOING)
    private Mint mint;

    @Relationship(type = WITH_MINTMARK, direction = Relationship.Direction.OUTGOING)
    private Mintmark mintmark;

    private String nid;
    private String picture;


    //old properties start
    private String name;
    private String description;
    private String photoSymbol;
    private String numistaURL;

    //old properties end

    public SpecifiedMint() {
    }

    public SpecifiedMint(String name) {
        this.name = name;
    }

    public SpecifiedMint(String nid, String identifier) {
        this.nid = nid;
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Mint getMint() {
        return mint;
    }

    public void setMint(Mint mint) {
        this.mint = mint;
    }

    public Mintmark getMintmark() {
        return mintmark;
    }

    public void setMintmark(Mintmark mintmark) {
        this.mintmark = mintmark;
    }
}
