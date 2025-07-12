package bkv.colligendis.utils.meshok;

import lombok.Data;

@Data
public class Sort {

    public static final int ASCENDING = 0;
    public static final int DESCENDING = 1;
    public static final String END_EARLY = "endDate";
    String field;
    int direction;

    public Sort(String field, int direction) {
        this.field = field;
        this.direction = direction;
    }
}
