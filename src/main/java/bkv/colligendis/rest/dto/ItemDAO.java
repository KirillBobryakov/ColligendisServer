package bkv.colligendis.rest.dto;

import lombok.Data;

@Data
public class ItemDAO {

    private String uuid;
    private String createdAt;
    private Integer quantity;
    private String gradeType;
    private Float priceValue;
    private String priceCurrency;
    private String acquisitionDate;
    private String variantNid;
    private String userUuid;
}
