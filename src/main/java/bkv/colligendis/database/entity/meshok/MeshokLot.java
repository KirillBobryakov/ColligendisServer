package bkv.colligendis.database.entity.meshok;

import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.entity.numista.Variant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("MESHOK_LOT")
@Data
@EqualsAndHashCode(callSuper = false)
public class MeshokLot extends AbstractEntity {

    private final static String OWNED_BY = "OWNED_BY";

    public final static String RELATED_TO_CATEGORY = "RELATED_TO_CATEGORY";
    public final static String RELATED_TO_VARIANT = "RELATED_TO_VARIANT";

    private int mid;

    private String title;

    private Date beginDate;
    private Date endDate;

    private int bidsCount;

    @Relationship(type = RELATED_TO_CATEGORY, direction = Relationship.Direction.OUTGOING)
    private MeshokCategory category;

    @Relationship(type = RELATED_TO_VARIANT, direction = Relationship.Direction.OUTGOING)
    private Variant variant;

    private String currency;

    private int watchCount;

    private int hitsCount;

    private List<String> pictures;

    private boolean isAntisniperEnabled;
    private boolean isEndDateExtended;

    private boolean blocked;
    private boolean banned;
    private boolean isBargainAvailable;
    private boolean isFeatured;

    private float price;

    private int quantity;
    private int soldQuantity;

    private int status;

    @Relationship(type = OWNED_BY, direction = Relationship.Direction.OUTGOING)
    private MeshokSeller seller;

    private Float startPrice;
    private Float strikePrice;

    // fixedPrice, auction
    private String type;

    private boolean isTemporarilyBlocked;

}
