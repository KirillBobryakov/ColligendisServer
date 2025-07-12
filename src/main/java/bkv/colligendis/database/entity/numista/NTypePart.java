package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.entity.features.LocalImage;
import bkv.colligendis.utils.numista.PART_TYPE;
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

    public static final String WRITE_ON_SCRIPT = "WRITE_ON_SCRIPT";

    private PART_TYPE partType;

    public NTypePart(PART_TYPE partType) {
        this.partType = partType;
    }

    private List<String> engravers = new ArrayList<>();
    private List<String> designers = new ArrayList<>();

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
