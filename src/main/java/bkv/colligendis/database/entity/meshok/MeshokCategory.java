package bkv.colligendis.database.entity.meshok;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("MESHOK_CATEGORY")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class MeshokCategory extends AbstractEntity {

    private static final String HAS_CHILD = "HAS_CHILD";

    // @Relationship(type = HAS_CHILD, direction = Relationship.Direction.OUTGOING)
    // private List<MeshokCategory> childs = new ArrayList<>();

    String extraName;
    int mid;
    int level;

    @Relationship(type = HAS_CHILD, direction = Relationship.Direction.INCOMING)
    private MeshokCategory parent;

    @NonNull
    private String name;

    public MeshokCategory() {
    }

    public MeshokCategory(String name) {
        this.name = name;
    }

}
