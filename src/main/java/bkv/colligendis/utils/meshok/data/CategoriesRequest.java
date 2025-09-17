package bkv.colligendis.utils.meshok.data;

import java.util.List;

import lombok.Data;

@Data
public class CategoriesRequest {

    List<Integer> identifiers;
    int childsLevel;
    boolean includeParents;

}
