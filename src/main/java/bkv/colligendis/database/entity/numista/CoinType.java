package bkv.colligendis.database.entity.numista;

public enum CoinType {

    common_coin("Standard circulation coin"),
    commemorative_coin("Circulating commemorative coin"),
    non_circulating_coin("Non-circulating coin"),
    pattern("Pattern"),
    token("Token");

    public final String coinType;

    private CoinType(String coinType) {
        this.coinType = coinType;
    }


    }
