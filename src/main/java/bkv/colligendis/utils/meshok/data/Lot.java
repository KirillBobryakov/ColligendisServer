package bkv.colligendis.utils.meshok.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Lot {

    int id;
    Long newId;

    List<AdditionalProperty> additionalProperties;

    String beginDate;
    String endDate;

    int bidsCount;
    int categoryId;
    City city;
    int condition;
    String currency;
    Delivery delivery;

    int watchCount;
    int hitsCount;

    List<String> images;
    List<String> thumbnails;

    List<Float> picsRatio;

    List<Picture> pictures;

    boolean isAntisniperEnabled;
    boolean isEndDateExtended;
    boolean blocked;
    boolean banned;
    boolean isBargainAvailable;
    boolean isFeatured;

    int minRating;
    List<String> paymentMethods;

    int picsCount;
    int picsVersion;
    float price;
    float normalizedPrice;
    int quantity;
    int soldQuantity;
    int status;

    Seller seller;

    float startPrice;
    float strikePrice;

    List<String> tags;

    String title;

    String type;

    boolean markedAsBold;
    boolean isTemporarilyBlocked;
    float charityPercent;
    boolean hasReposts;
    Boolean isPremium;
    Integer ageCategory;

    Integer maxBid;
    boolean closed = false;

    Buyer buyer;

    List<LotBid> lotBids = new ArrayList<>();

    // List<InterestPoint> surfObverseInterestPoints = new ArrayList<>();
    // List<InterestPoint> surfReverseInterestPoints = new ArrayList<>();

}
