package bkv.colligendis.database.entity.meshok;

import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.neo4j.core.schema.Node;

@Data
@EqualsAndHashCode(callSuper = false)
@Node("MESHOK_SELLER")
public class MeshokSeller extends AbstractEntity {

    private int mid;

    private String displayName;
    private boolean isTrusted;
    private boolean hasEconomyDelivery;
    private boolean hasDiscounts;

    private String avatarURL;

    private boolean isBanned;
    private boolean isOnHold;

}
