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

    public static final String LABEL = "SPECIFIED_MINT";

    public static final String WITH_MINTMARK = "WITH_MINTMARK";
    public static final String WITH_MINT = "WITH_MINT";

    private String identifier;

    @Relationship(type = WITH_MINT, direction = Relationship.Direction.OUTGOING)
    private Mint mint;

    @Relationship(type = WITH_MINTMARK, direction = Relationship.Direction.OUTGOING)
    private Mintmark mintmark;

    public SpecifiedMint(String identifier) {
        this.identifier = identifier;
    }

}
