package bkv.colligendis.rest.catalogue;

import lombok.Data;

@Data
public class ParsingNumistaData {
    String topCollectableType;
    String issuerCode;

    public ParsingNumistaData(String topCollectableType, String issuerCode) {
        this.topCollectableType = topCollectableType;
        this.issuerCode = issuerCode;
    }

}
