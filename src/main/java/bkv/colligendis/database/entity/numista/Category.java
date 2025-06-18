package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.data.neo4j.core.schema.Node;

@Node("CATEGORY")
@Data
@EqualsAndHashCode(callSuper=false)
public class Category extends AbstractEntity {

    public static final String COIN = "coin";
    public static final String BANKNOTE = "banknote";
    public static final String EXONUMIA = "exonumia";
    public static final String UNKNOWN = "unknown";

    public Category(String name) {
        this.name = name;
    }

    @NonNull
    private String name;


}
