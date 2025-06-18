package bkv.colligendis.database.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.UUID;

@Data
public abstract class AbstractEntity {

    @Id
    @GeneratedValue()
    private UUID uuid = UUID.randomUUID();

    @Override
    public int hashCode() {
        if (uuid != null) {
            return uuid.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity other)) {
            return false; // null or other class
        }

        if (uuid != null) {
            return uuid.equals(other.uuid);
        }
        return super.equals(other);
    }

}
