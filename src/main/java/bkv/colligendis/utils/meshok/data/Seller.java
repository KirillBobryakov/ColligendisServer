package bkv.colligendis.utils.meshok.data;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(of = { "displayName" })
public class Seller {

    // public static final HashMap<Integer, Seller> sellers = new HashMap<>() {{
    // put(336179, new Seller(336179, "_Антиквариус_"));
    // }};

    String avatarURL;
    String avatarThumbnailURL;
    String creationDate;
    String displayName;
    boolean isTrusted;
    int rating;
    int id;
    int subscription;
    // lastSubscriptionEmail
    int lotsTotalAmount;
    int lotsWithBidsAmount;
    int newLotsAmount;
    String newLotsAmountPeriod;
    int endingLotsAmount;

    // String cityName;
    // String countryName;
    boolean hasEconomyDelivery;
    boolean hasDiscounts;
    // "pauseMessage":null,
    boolean isBanned;
    boolean isOnHold;

}
