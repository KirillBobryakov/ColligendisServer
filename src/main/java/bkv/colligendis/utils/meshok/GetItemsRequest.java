package bkv.colligendis.utils.meshok;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Data;

@Data
public class GetItemsRequest {

    boolean sellerMode = false;
    Filter filter = new Filter();
    Includes includes = new Includes(true, false);

    boolean saveSearchRequest = false;
    boolean featuredLotsFirst = true;
    boolean onlyWithPicture = true;

    public String toJson() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(this);
    }

}