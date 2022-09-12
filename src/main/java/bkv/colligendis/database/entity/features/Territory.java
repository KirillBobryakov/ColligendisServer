package bkv.colligendis.database.entity.features;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("TERRITORY")
public class Territory extends AbstractEntity {

    public static final String SUB_TERRITORY = "SUB_TERRITORY";

    private String name;

    @Relationship(type = SUB_TERRITORY, direction = Relationship.Direction.INCOMING)
    private Territory parentTerritory;

    @Relationship(type = SUB_TERRITORY, direction = Relationship.Direction.OUTGOING)
    private Set<Territory> cildrenTerritories = new HashSet<>();

    public Territory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Territory getParentTerritory() {
        return parentTerritory;
    }

    public void setParentTerritory(Territory parentTerritory) {
        this.parentTerritory = parentTerritory;
    }

    public Set<Territory> getCildrenTerritories() {
        return cildrenTerritories;
    }

    public void setCildrenTerritories(Set<Territory> cildrenTerritories) {
        this.cildrenTerritories = cildrenTerritories;
    }
}
