package bkv.colligendis.rest.statistics;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NTypesStatistics {

    private Integer coinsNTypesCount;
    private Integer coinsVariantsCount;
    private Integer banknotesNTypesCount;
    private Integer banknotesVariantsCount;
    private Integer paperExonumiasNTypesCount;
    private Integer paperExonumiasVariantsCount;
    private Integer medalsNTypesCount;
    private Integer medalsVariantsCount;
    private Integer tokensNTypesCount;
    private Integer tokensVariantsCount;

}
