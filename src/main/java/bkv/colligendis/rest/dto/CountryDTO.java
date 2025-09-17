package bkv.colligendis.rest.dto;

import java.util.List;

import lombok.Data;

@Data
public class CountryDTO {

    String name;
    String numistaCode;
    List<String> ruAlternativeNames;

}
