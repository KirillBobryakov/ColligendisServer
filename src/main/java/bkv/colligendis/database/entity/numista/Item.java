package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.google.gson.annotations.SerializedName;

@Node("ITEM")
@Data
@EqualsAndHashCode(callSuper = true)
public class Item extends AbstractEntity {

    public static final String RELATED_TO_TYPE = "RELATED_TO_TYPE";
    public static final String RELATED_TO_VARIANT = "RELATED_TO_VARIANT";

    // 'paper', 'round'
    // public String round_or_paper;

    // item
    @SerializedName("id")
    public String nid;

    private String createdAt;

    public int quantity;

    @SerializedName("type")
    @Relationship(type = RELATED_TO_TYPE, direction = Relationship.Direction.OUTGOING)
    private NType nType;

    @SerializedName("issue")
    @Relationship(type = RELATED_TO_VARIANT, direction = Relationship.Direction.OUTGOING)
    private Variant variant;

    // 1 - yes, 0 - no
    public int forSwap;

    // grade
    @SerializedName("grade")
    public GradeType gradeType;

    // comment
    @SerializedName("private_comment")
    public String privateComment;

    @SerializedName("price_value")
    public Float priceValue;

    @SerializedName("price_currency")
    public String priceCurrency;

    @SerializedName("storage_location")
    private String storageLocation;

    @SerializedName("acquisition_place")
    public String acquisitionPlace;

    // date(YYYY-MM-DD)
    @SerializedName("acquisition_date")
    public String acquisitionDate;

    @SerializedName("serial_number")
    public String serialNumber;

    @SerializedName("internal_id")
    public String internalId;

    // grams (format: 10.435; 4.55; 6.6; 10)
    private Float weight;

    // millimetres (format: 4.55; 6.6; 10)
    private Float size;

    // 1,2,3,4,5,6,7,8,9,10,11,12
    private int axis;

    // swapComment
    public String publicComment;

    @Relationship(type = Grading.HAS_GRADING, direction = Relationship.Direction.OUTGOING)
    private Grading grading;

    //
    private String obverseImage;

    private String reverseImage;

    private List<String> additionalImages;

    // onclick="collec_modal_new('paper', 225462, 842893, 70280827, 1, null, 0,
    // '1234.00', '', '', 0, [], null, null, '&quot;&quot;', '', null, '', 'ww',
    // null, '', '', null, null, null, ); return false;"
    // collec_modal_new(round_or_paper, coinId, selected_version, item, quantity,
    // grade, forSwap, value, comment, swapComment, section, pictures,
    // gradingService, gradingMark, gradeDetails, slabNumber, cacSticker,
    // storageLocation, acquisitionPlace, acquisitionDate, serialNumber, internalId,
    // size, weight, axis)
}
