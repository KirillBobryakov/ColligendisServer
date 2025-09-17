package bkv.colligendis.utils.meshok.data;

import lombok.Data;

@Data
public class Suggest {
    String text;
    Object[] words;
    Object[] categories;
    Object[] queries;

}
