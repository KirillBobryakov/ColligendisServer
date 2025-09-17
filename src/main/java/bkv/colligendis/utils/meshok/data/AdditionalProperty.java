package bkv.colligendis.utils.meshok.data;

import java.util.List;

import lombok.Data;

@Data
public class AdditionalProperty {

    int categoryId;
    String name;
    int propertyId;
    List<Value> values;

}
