package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("SPECIFIED_MINT")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
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




}
