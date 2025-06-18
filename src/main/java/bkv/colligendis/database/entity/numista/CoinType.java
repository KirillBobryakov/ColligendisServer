package bkv.colligendis.database.entity.numista;


/**
 * Assign the coin to an appropriate category.
 *
 * Standard circulation coin: Non-commemorative coin that was issued as legal tender or can be used in regular commercial activities.
 * Circulating commemorative coin: Coin that was issued for a limited time to serve as legal tender and that commemorates a specific subject.
 * Non-circulating coin: Coin that has a face value associated with a circulation currency, but was never intended to be used in regular commercial activities, but rather as investment or collector piece.
 * Pattern: Coin that was produced to evaluate a proposed design. These can be off-metal strikes, piedforts, trials, etc.
 * Token: Coin-like object that was issued privately and could be used in limited commercial activities: for specific products, goods, or services; within a specific institution, organisation or private company; or for a specific event.
 *
 * Note that collector’s and presentation issues with the same design and physical properties (for example, proof strikes and polished die strikes) should be listed as year lines for the circulation types. Patterns and trial strikes should be listed separately from the circulation types.
 *
 * Information takes from https://en.numista.com/help/type-108.html
 */

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
