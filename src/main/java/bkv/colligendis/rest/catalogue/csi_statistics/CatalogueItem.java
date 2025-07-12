package bkv.colligendis.rest.catalogue.csi_statistics;

import lombok.Data;

@Data
public class CatalogueItem {

    String uuid;
    String createdAt;
    Integer quantity;
    String gradeType;
    Float priceValue;
    String priceCurrency;
    String acquisitionDate;
    String variantNid;

    public CatalogueItem() {
    }

    public CatalogueItem(String uuid, String createdAt, Integer quantity, String gradeType, Float priceValue,
            String priceCurrency, String acquisitionDate, String variantNid) {
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.quantity = quantity;
        this.gradeType = gradeType;
        this.priceValue = priceValue;
        this.priceCurrency = priceCurrency;
        this.acquisitionDate = acquisitionDate;
        this.variantNid = variantNid;
    }

}
