package bkv.colligendis.utils.meshok.data;

import lombok.Data;

@Data
public class Delivery {

    int abroadDelivery;
    float countryPrice;
    int localDelivery;
    float localPrice;
    boolean soloDelivery;
    float worldPrice;

}
