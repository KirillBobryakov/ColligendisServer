package bkv.colligendis.database.entity.features;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Arrays;
import java.util.List;

@Node("RULER")
public class Ruler extends AbstractEntity {

    public static final List<String> RULERS = Arrays.asList(
            "Period", "Archduke", "Ban", "Bishop", "Caesar", "Camerlengo", "Doge", "Duchess", "Duke", "Emir", "Emperor", "Empress",
            "Grandmaster", "Grand duchess", "Grand duke", "Grand Prince",
            "Khan", "King", "Landgrave", "Lord", "Margrave", "Margravine", "Master",
            "Pope", "President", "Prime minister", "Prince", "Prince-archbishop", "Prince elector", "Prince-bishop",
            "Queen" , "Regent", "Ruling authority", "Shah", "Sultan", "Tsar", "Voivode");

    private String name;

    private String rulerType;
}
