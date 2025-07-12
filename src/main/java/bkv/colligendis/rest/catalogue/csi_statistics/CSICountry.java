package bkv.colligendis.rest.catalogue.csi_statistics;

import lombok.Data;

@Data
public class CSICountry {

    String name;
    String numistaCode;

    public CSICountry(String name, String numistaCode) {
        this.name = name;
        this.numistaCode = numistaCode;
    }
}