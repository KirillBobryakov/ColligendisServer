package bkv.colligendis.rest.catalogue;

import lombok.Data;

/**
 * CSI - Country, Subject, Issuer
 */
@Data
public class CSIItem {

    public enum Label {
        COUNTRY, SUBJECT, ISSUER
    }

    Label label;

    String code;
    String name;

    public CSIItem(Label label, String code, String name) {
        this.code = code;
        this.name = name;
        this.label = label;
    }

}