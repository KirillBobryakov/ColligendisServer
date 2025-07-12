package bkv.colligendis.rest.catalogue.csi_statistics;

import lombok.Data;

@Data
public class CsiStatisticsRequest {

    private String csiType;
    private String csiCode;

    public CsiStatisticsRequest(String csiType, String csiCode) {
        this.csiType = csiType;
        this.csiCode = csiCode;
    }

}
