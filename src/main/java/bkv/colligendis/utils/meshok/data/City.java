package bkv.colligendis.utils.meshok.data;

import lombok.Data;

@Data
public class City {

    String country;
    int countryId;
    int id;
    String name;
    int popularity;
    String region;
    int regionId;
    Boolean isNameUnique;

}
