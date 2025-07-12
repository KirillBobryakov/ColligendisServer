package bkv.colligendis.rest.catalogue.csi_statistics;

import lombok.Data;

@Data
public class CSIDenomination {
    String name;
    String nid;

    public CSIDenomination(String name, String nid) {
        this.name = name;
        this.nid = nid;
    }
}