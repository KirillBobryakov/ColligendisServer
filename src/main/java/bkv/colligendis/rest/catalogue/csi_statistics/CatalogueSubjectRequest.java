package bkv.colligendis.rest.catalogue.csi_statistics;

import lombok.Data;

@Data
public class CatalogueSubjectRequest {

    private String csiType;
    private String csiCode;

    public CatalogueSubjectRequest(String csiType, String csiCode) {
        this.csiType = csiType;
        this.csiCode = csiCode;
    }

}
