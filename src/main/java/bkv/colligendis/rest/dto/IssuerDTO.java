package bkv.colligendis.rest.dto;

import java.util.List;

import lombok.Data;

@Data
public class IssuerDTO {

    private String code;
    private String name;
    private List<String> ruAlternativeNames;

}
