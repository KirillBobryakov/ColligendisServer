package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.service.numista.CompositionMetalType;
import org.springframework.data.neo4j.core.schema.Node;

@Node("COMPOSITION")
public class Composition extends AbstractEntity {

    private CompositionType compositionType;

    private Metal metal1;
    private CompositionMetalType metal1Type;
    private String metal1Fineness;

    private Metal metal2;
    private CompositionMetalType metal2Type;
    private String metal2Fineness;

    private Metal metal3;
    private CompositionMetalType metal3Type;
    private String metal3Fineness;

    private Metal metal4;
    private CompositionMetalType metal4Type;
    private String metal4Fineness;


    private String compositionAdditionalDetails;



    public CompositionType getCompositionType() {
        return compositionType;
    }

    public void setCompositionType(CompositionType compositionType) {
        this.compositionType = compositionType;
    }

    public Metal getMetal1() {
        return metal1;
    }

    public void setMetal1(Metal metal1) {
        this.metal1 = metal1;
    }

    public CompositionMetalType getMetal1Type() {
        return metal1Type;
    }

    public void setMetal1Type(CompositionMetalType metal1Type) {
        this.metal1Type = metal1Type;
    }

    public String getMetal1Fineness() {
        return metal1Fineness;
    }

    public void setMetal1Fineness(String metal1Fineness) {
        this.metal1Fineness = metal1Fineness;
    }

    public Metal getMetal2() {
        return metal2;
    }

    public void setMetal2(Metal metal2) {
        this.metal2 = metal2;
    }

    public CompositionMetalType getMetal2Type() {
        return metal2Type;
    }

    public void setMetal2Type(CompositionMetalType metal2Type) {
        this.metal2Type = metal2Type;
    }

    public String getMetal2Fineness() {
        return metal2Fineness;
    }

    public void setMetal2Fineness(String metal2Fineness) {
        this.metal2Fineness = metal2Fineness;
    }

    public Metal getMetal3() {
        return metal3;
    }

    public void setMetal3(Metal metal3) {
        this.metal3 = metal3;
    }

    public CompositionMetalType getMetal3Type() {
        return metal3Type;
    }

    public void setMetal3Type(CompositionMetalType metal3Type) {
        this.metal3Type = metal3Type;
    }

    public String getMetal3Fineness() {
        return metal3Fineness;
    }

    public void setMetal3Fineness(String metal3Fineness) {
        this.metal3Fineness = metal3Fineness;
    }

    public Metal getMetal4() {
        return metal4;
    }

    public void setMetal4(Metal metal4) {
        this.metal4 = metal4;
    }

    public CompositionMetalType getMetal4Type() {
        return metal4Type;
    }

    public void setMetal4Type(CompositionMetalType metal4Type) {
        this.metal4Type = metal4Type;
    }

    public String getMetal4Fineness() {
        return metal4Fineness;
    }

    public void setMetal4Fineness(String metal4Fineness) {
        this.metal4Fineness = metal4Fineness;
    }

    public String getCompositionAdditionalDetails() {
        return compositionAdditionalDetails;
    }

    public void setCompositionAdditionalDetails(String compositionAdditionalDetails) {
        this.compositionAdditionalDetails = compositionAdditionalDetails;
    }
}
