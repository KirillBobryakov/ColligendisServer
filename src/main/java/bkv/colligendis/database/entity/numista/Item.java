package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("ITEM")
@Data
@EqualsAndHashCode(callSuper=true)
public class Item extends AbstractEntity {

    public static final String RELATED_TO_TYPE = "RELATED_TO_TYPE";
    public static final String RELATED_TO_VARIANT = "RELATED_TO_VARIANT";

    //'paper', 'round'
    public String round_or_paper;

    //coinId = nType.nid
    @Relationship(type = RELATED_TO_TYPE, direction = Relationship.Direction.OUTGOING)
    private NType nType;

    //selected_version = Variant.nid
    @Relationship(type = RELATED_TO_VARIANT, direction = Relationship.Direction.OUTGOING)
    private Variant variant;

    private String obverseImage;
    private String reverseImage;

    //item
    public String nid;

    public int quantity;

    //grade
    public GradeType gradeType;

    //1 - yes, 0 - no
    public int forSwap;

    public Float value;

    //comment
    public String privateComment;

    //swapComment
    public String publicComment;

    @Relationship(type = Grading.HAS_GRADING, direction = Relationship.Direction.OUTGOING)
    private Grading grading;

    private String storageLocation;
    private String internalId;

    //millimetres (format: 4.55; 6.6; 10)
    private Float size;

    //grams (format: 10.435; 4.55; 6.6; 10)
    private Float weight;

    //1,2,3,4,5,6,7,8,9,10,11,12
    private int axis;


//    onclick="collec_modal_new('paper', 225462, 842893, 70280827, 1, null, 0, '1234.00', '', '', 0, [], null, null, '&quot;&quot;', '', null, '', 'ww', null, '', '', null, null, null, ); return false;"
//    collec_modal_new(round_or_paper, coinId, selected_version, item, quantity, grade, forSwap, value, comment, swapComment, section, pictures,
//    gradingService, gradingMark, gradeDetails, slabNumber, cacSticker,
//    storageLocation, acquisitionPlace, acquisitionDate, serialNumber, internalId, size, weight, axis)
}
