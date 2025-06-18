//package bkv.colligendis.database.entity.numista;
//
//import bkv.colligendis.database.entity.AbstractEntity;
//import bkv.colligendis.database.entity.features.Year;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import org.springframework.data.neo4j.core.schema.Node;
//import org.springframework.data.neo4j.core.schema.Relationship;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * Dynasty, house, extended period, or any other group of ruling authorities
// *
// * Information takes from <a href="https://en.numista.com/help/add-or-modify-a-ruling-authority-in-the-catalogue-192.html">Numista</a>
// */
//@Node("PERIOD")
//@Data
//@EqualsAndHashCode(callSuper=false)
//public class Period extends AbstractEntity {
//
//
//    public static final String FROM = "FROM";
//    public static final String TILL = "TILL";
//
//    @Relationship(type = FROM, direction = Relationship.Direction.OUTGOING)
//    private Year startYear;
//
//    @Relationship(type = TILL, direction = Relationship.Direction.OUTGOING)
//    private Year endYear;
//
//    public Period() {
//    }
//
//    public Period(Year startYear, Year endYear) {
//        this.startYear = startYear;
//        this.endYear = endYear;
//    }
//}
