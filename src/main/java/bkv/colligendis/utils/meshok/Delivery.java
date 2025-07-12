package bkv.colligendis.utils.meshok;

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
