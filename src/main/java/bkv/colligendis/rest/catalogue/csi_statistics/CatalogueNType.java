package bkv.colligendis.rest.catalogue.csi_statistics;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CatalogueNType {

    private String nid;
    private String title;
    private String collectibleType;
    private String topCollectibleType;
    private CSIIssuer csiIssuer;
    private List<CatalogueNTypeVariant> variants;

    public CatalogueNType(String nid, String title, String collectibleType, String topCollectibleType,
            CSIIssuer csiIssuer) {
        this.nid = nid;
        this.title = title;
        this.collectibleType = collectibleType;
        this.topCollectibleType = topCollectibleType;
        this.csiIssuer = csiIssuer;
        this.variants = new ArrayList<>();
    }
}
