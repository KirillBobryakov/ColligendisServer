//package bkv.colligendis.database.entity.coin;
//
//
//import bkv.colligendis.database.entity.AbstractEntity;
//import bkv.colligendis.database.entity.User;
//import bkv.colligendis.database.entity.item.Variant;
//import org.springframework.data.neo4j.core.schema.Node;
//import org.springframework.data.neo4j.core.schema.Relationship;
//
//@Node("COIN")
//public class Coin extends AbstractEntity {
//
//    public static final String WITH_COIN_VARIANT = "WITH_COIN_VARIANT";
//    public static final String HAS_COIN = "HAS_COIN";
//
//    @Relationship(type = WITH_COIN_VARIANT, direction = Relationship.Direction.OUTGOING)
//    private Variant variant;
//
//
//    @Relationship(type = HAS_COIN, direction = Relationship.Direction.INCOMING)
//    private User owner;
//
//}
