package bkv.colligendis.utils.meshok;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class Category {

    public static final int COINS = 252;
    public static final int MARKS = 254;
    public static final int BONES = 786;

    public static final HashMap<Integer, Category> categories = new HashMap<>() {
        {
            put(252, new Category(252, "Монеты", 140));
            put(254, new Category(254, "Марки", 140));
            put(786, new Category(786, "Банкноты и Боны", 140));

            put(140, new Category(140, "Коллекционное"));
            put(1605, new Category(1605, "Австралия и Океания", 252));
            put(1592, new Category(1592, "Азия", 252));
            put(1679, new Category(1679, "Америка", 252));
            put(1591, new Category(1591, "Африка", 252));
            put(2160, new Category(2160, "Ближний Восток", 252));
            put(2140, new Category(2140, "Великобритания", 252));
            put(13309, new Category(13309, "Евро", 252));
            put(1256, new Category(1256, "Европа", 252));
            put(15399, new Category(15399, "Россия и СССР", 252));

        }
    };

    List<Integer> childs;
    String extraName;
    int id;
    int level;
    int lotsCount;
    String name;
    int parentId;

    public Category() {
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(int id, String name, int parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

}
