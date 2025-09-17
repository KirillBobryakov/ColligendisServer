package bkv.colligendis.utils.meshok.data;

import lombok.Data;

@Data
public class LotBid {

    Integer autoBid;
    Float bid;

    Buyer buyer;

    String currencyCode;

    String date;

    int id;

    int lotId;

}