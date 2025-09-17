package bkv.colligendis.rest.catalogue.csi_statistics;

import lombok.Data;

@Data
public class YearEntity {

    private Integer value;

    public YearEntity(Integer value) {
        this.value = value;
    }
}
