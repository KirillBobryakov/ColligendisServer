package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.entity.features.LocalImage;
import bkv.colligendis.utils.numista.parser.PART_TYPE;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Node("NTYPE_PART")
@Data
public class NTypePart extends AbstractEntity {
    public static final String LABEL = "NTYPE_PART";

    public static final String WRITE_ON_SCRIPT = "WRITE_ON_SCRIPT";
    public static final String ENGRAVING_WAS_DONE_BY = "ENGRAVING_WAS_DONE_BY";
    public static final String DESIGN_WAS_DONE_BY = "DESIGN_WAS_DONE_BY";

    private PART_TYPE partType;

    public NTypePart(PART_TYPE partType) {
        this.partType = partType;
    }

    @Relationship(type = ENGRAVING_WAS_DONE_BY, direction = Relationship.Direction.OUTGOING)
    private List<Artist> engravers = new ArrayList<>();

    @Relationship(type = DESIGN_WAS_DONE_BY, direction = Relationship.Direction.OUTGOING)
    private List<Artist> designers = new ArrayList<>();

    private String description;
    private String lettering;

    @Relationship(type = WRITE_ON_SCRIPT, direction = Relationship.Direction.OUTGOING)
    private List<LetteringScript> letteringScripts = new ArrayList<>();

    private String unabridgedLegend;
    private String letteringTranslation;
    private String letteringTranslationRu;
    private String picture;

    private LocalImage localImage;

}
