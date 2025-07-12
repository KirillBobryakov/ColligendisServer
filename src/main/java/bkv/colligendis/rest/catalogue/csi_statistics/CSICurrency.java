package bkv.colligendis.rest.catalogue.csi_statistics;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CSICurrency {
    String name;
    String nid;

    List<CSIDenomination> denominations = new ArrayList<>(0);

    public CSICurrency(String name, String nid) {
        this.name = name;
        this.nid = nid;
    }
}