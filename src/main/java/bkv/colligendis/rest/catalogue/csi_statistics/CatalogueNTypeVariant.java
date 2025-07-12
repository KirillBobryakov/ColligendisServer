package bkv.colligendis.rest.catalogue.csi_statistics;

import lombok.Data;

@Data
public class CatalogueNTypeVariant {

    private String nid;
    private String comment;
    private Integer year;
    private Integer yearFrom;
    private Integer itemsCount;

    public CatalogueNTypeVariant(String nid, String comment, Integer year, Integer yearFrom,
            Integer itemsCount) {
        this.nid = nid;
        this.comment = comment;
        this.year = year;
        this.yearFrom = yearFrom;
        this.itemsCount = itemsCount;
    }
}
