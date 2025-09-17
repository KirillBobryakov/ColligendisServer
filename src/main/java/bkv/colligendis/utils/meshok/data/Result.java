package bkv.colligendis.utils.meshok.data;

import lombok.Data;

@Data
public class Result {

    Lot[] lots;
    Stats stats;
    Suggest suggest;
    long[] lastSortValues;

}