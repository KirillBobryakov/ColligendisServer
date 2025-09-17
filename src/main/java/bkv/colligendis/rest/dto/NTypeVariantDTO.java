package bkv.colligendis.rest.dto;

import bkv.colligendis.database.entity.features.Year;
import lombok.Data;

@Data
public class NTypeVariantDTO {
    private String nid;
    private Boolean isDated;
    private Year year;
    private Year yearFrom;
    private Year yearTill;
    private Integer mintage;
    private String comment;
    private Integer itemsCount;

}
