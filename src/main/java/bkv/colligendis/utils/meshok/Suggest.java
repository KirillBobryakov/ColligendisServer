package bkv.colligendis.utils.meshok;

import lombok.Data;

@Data
public class Suggest {
    String text;
    Object[] words;
    Object[] categories;
    Object[] queries;

}
