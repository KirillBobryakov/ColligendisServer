package bkv.colligendis.utils.meshok;

import java.util.HashMap;

import lombok.Data;

@Data
public class Location {

    public static final HashMap<Integer, Location> locations = new HashMap<>() {
        {
            put(32, new Location(32, "all")); // Москва
        }
    };

    int cityId;
    String option;
    Boolean freeDelivery = false;
    Boolean economyDelivery = false;
    Boolean pickup = false;

    public Location(int cityId, String option) {
        this.cityId = cityId;
        this.option = option;
    }

    public Location(int cityId, String option, boolean economyDelivery) {
        this.cityId = cityId;
        this.option = option;
        this.economyDelivery = economyDelivery;
    }

}
