package bkv.colligendis.database.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.UUID;

public abstract class AbstractEntity2 {

//    @Id
//    @GeneratedValue
//    private Long id;

    @Id
    @GeneratedValue()
    private final UUID eid = UUID.randomUUID();

    public UUID getEid() {
        return eid;
    }

    @Override
    public int hashCode() {
        return eid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity2 other)) {
            return false; // null or other class
        }

        if (eid != null) {
            return eid.equals(other.eid);
        }
        return super.equals(other);
    }

    public long getId() {
        return 0;
    }

    public void setId(long id) {
//        this.id = id;
    }


}
