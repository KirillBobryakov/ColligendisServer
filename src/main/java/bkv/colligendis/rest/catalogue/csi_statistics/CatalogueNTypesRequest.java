package bkv.colligendis.rest.catalogue.csi_statistics;

import lombok.Data;

@Data
public class CatalogueNTypesRequest {
    private String csiType;
    private String csiCode;
    private String denominationNid;
    private String issuerCode;
    private String subjectNumistaCode;
    private String collectibleTypeCode;
    private String textFilter;
}
