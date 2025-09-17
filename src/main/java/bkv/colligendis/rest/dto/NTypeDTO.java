package bkv.colligendis.rest.dto;

import java.util.List;

import lombok.Data;

@Data
public class NTypeDTO {
    private String nid;
    private String title;
    private String collectibleType;
    private String topCollectibleType;
    private IssuerDTO issuer;
    private List<NTypeVariantDTO> variants;
    private DenominationDTO denomination;

}
