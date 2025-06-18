package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

/*
 * Definition
 *
 * An issuer is any:
 * organised community (for example, Australia, Commune of Nice, Abbey of Saint Gall, Rauraci tribe),
 * association of such communities (for example, Eurozone, West African States, joint notgeld issuers),
 * with a claimed right to issue currency.
 *
 * An issuer may have different currencies, governments and names throughout its history.
 *
 * An autonomous part of a bigger issuer that issued coins for local usage are also considered as issuers (for example, various Roman provinces).
 *
 * Only when the territory of an issuer suffers a sudden, significant, and long-term change, resulting in a discontinuity of its currency, then the change result in a different issuer. For example the Soviet Union and modern-day Russia are listed as different issuers.
 * How to request the addition of a new issuer?
 *
 * Please post a request on the forum, indicating:
 * the name of the issuer in English (and if possible also in French and Spanish)
 * its Wikidata code (for example, Q854 for Sri Lanka)
 * if there are no entries yet on Numista, the link to an auction site or a reference catalogue presenting at least one coin or banknote from this issuer
 * if they already exist in the catalogue, the link of one or more Numista records that you want to classify under this issuer
 * if necessary, the list of other issuers to be linked by “see also” links (for example the Holy Roman Empire is linked to the Carolingian Empire)
 * if possible, a brief introduction presenting the history of this issuer with any numismatic considerations useful to the reader
 *
 * It is not required to provide all the information above. However, complete requests can be verified and added to the database quicker by the catalogue admins. Also remember to request the creation of the currencies and ruling authorities necessary for this new issuer!
 *
 * Information takes from https://en.numista.com/help/add-or-modify-an-issuing-authority-in-the-catalogue-191.html
 */
@Node("ISSUER")
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Issuer extends AbstractEntity {

    public static final String CONTAINS_ISSUING_ENTITY = "CONTAINS_ISSUING_ENTITY";
    public static final String CONTAINS_RULER = "CONTAINS_RULER";
    public static final String CONTAINS_CURRENCY = "CONTAINS_CURRENCY";

    public static final String PARENT_SUBJECT = "PARENT_SUBJECT";
    public static final String RELATE_TO_COUNTRY = "RELATE_TO_COUNTRY";

    /**
     * Unique String field from Numista
     */
    @ToString.Include
    private String code;
    /**
     * Unique String field from Numista
     */
    @ToString.Include
    private String name;

    private List<String> ruAlternativeNames = new ArrayList<>();

    @Relationship(value = PARENT_SUBJECT, direction = Relationship.Direction.OUTGOING)
    private Subject parentSubject;

    @Relationship(value = RELATE_TO_COUNTRY, direction = Relationship.Direction.OUTGOING)
    private Country country;

//    @Relationship(type = NType.ISSUED_BY, direction = Relationship.Direction.INCOMING)
//    private List<NType> nTypes = new ArrayList<>();

//    @Relationship(type = CONTAINS_ISSUING_ENTITY, direction = Relationship.Direction.OUTGOING)
//    private List<IssuingEntity> issuingEntities = new ArrayList<>();

//    @Relationship(type = CONTAINS_CURRENCY, direction = Relationship.Direction.OUTGOING)
//    private List<Currency> currencies = new ArrayList<>();

//    @Relationship(type = CONTAINS_RULER, direction = Relationship.Direction.OUTGOING)
//    private List<Ruler> rulers = new ArrayList<>();

    public Issuer() {
    }

    public Issuer(String name) {
        this.name = name;
    }


    public Issuer(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
