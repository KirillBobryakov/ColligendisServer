package bkv.colligendis.rest.catalogue;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * CSI - Country, Subject, Issuer
 */
@Data
public class CSIListItem {

    enum Label {
        COUNTRY, SUBJECT, ISSUER
    }

    Label label;

    String code;
    String name;

    List<String> enAlternativeNames = new ArrayList<String>();
    List<String> ruAlternativeNames = new ArrayList<String>();

    Integer subjectsCount;
    Integer issuersCount;
    Integer nTypesCount;

    public CSIListItem(Label label, String code, String name, List<String> enAlternativeNames,
            List<String> ruAlternativeNames, int subjectsCount, int issuersCount, int nTypesCount) {
        this.code = code;
        this.name = name;
        this.label = label;
        this.enAlternativeNames = enAlternativeNames;
        this.ruAlternativeNames = ruAlternativeNames;
        this.subjectsCount = subjectsCount;
        this.issuersCount = issuersCount;
        this.nTypesCount = nTypesCount;
    }

}