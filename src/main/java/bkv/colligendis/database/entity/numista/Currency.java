package bkv.colligendis.database.entity.numista;


import bkv.colligendis.database.entity.AbstractEntity;
import bkv.colligendis.database.entity.features.Year;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

/*
 * Definition
 *
 * A currency is a system of monetary units with fixed relative values. Currencies consist of one main unit and, optionally, fractional subunits or super units. The main currency unit is usually defined by law, by circulation prevalence, or by common accounting practice.
 *
 * When known, the relative value of the subunits and super units is described in reference to the main currency unit. The main unit is used to specify the face values in figure form of all items within that currency :
 * 1 Denarius = 2 Silver Quinarii = 4 Sestertii = 8 Dupondii = 16 Asses = 32 Semisses = 64 Quadrantes • 1 Aureus = 2 Gold Quinarii = 25 Denarii
 * Splitting currencies
 *
 * One issuer may have multiple currencies when redenominations occur. Redenominations may not necessarily be accompanied by a change of unit names. Redenominations may occur due to inflation, decimalisation, currency unions, monetary reforms, etc.
 * One issuer may also have multiple currencies when unrelated systems of currency units exist in parallel with a fluctuating exchange rate (for example, early thalers and ducats).
 * Currencies are not created for:
 * Series (by date, subject, etc)
 * Types of currency (for example, reserve notes, silver certificates, trials, patterns, etc.)
 * Minor name changes (for example, “new Turkish lira” became “Turkish lira” in 2009)
 * Changes, additions, or withdrawals of certain denominations (for example, withdrawal of the 500 euro banknote)
 * Debasement, changes in exchange rates, appreciation and depreciation, inflation and deflation, devaluation and revaluation
 * All currencies are listed in English in the database, according to the main listings (not alternative forms) of Oxford English Dictionary and Wiktionary.com.
 * How to request the addition of a new currency?
 *
 * Please post a request on the forum indicating:
 * the name of the currency in English (and if possible also in French and Spanish)
 * the issuer where this currency was used, and the corresponding date range
 * if known, the relative value of the subunits and super units described in reference to the main currency unit
 * if there are no entries yet on Numista, the link to an auction site or a reference catalogue presenting at least one coin or banknote using this currency
 * if they already exist, the link of one or more Numista records that you want to classify under this currency
 *
 * It is not required to provide all the information above. However, complete requests can be verified and added to the database quicker by the catalogue admins.
 *
 * Information takes from https://en.numista.com/help/add-or-modify-a-currency-in-the-catalogue-194.html
 */
@Node("CURRENCY")
@Data
@EqualsAndHashCode(callSuper = true)
public class Currency extends AbstractEntity {

//    public static final String HAS_DENOMINATION = "HAS_DENOMINATION";
    public static final String CIRCULATE_DURING = "CIRCULATE_DURING";

    public static final String CIRCULATED_FROM = "CIRCULATED_FROM";
    public static final String CIRCULATED_TILL = "CIRCULATED_TILL";

    private String nid;
    private String fullName;
    private String name;

    /**
     * Kind of currency like as
     * notgeld - Mark (notgeld, 1914-1924)
     * Occupation currency - Mark (Occupation currency, 1918), Rouble (Occupation currency, 1916)
     */
    private String kind;


    @Relationship(type = Issuer.CONTAINS_CURRENCY, direction = Relationship.Direction.INCOMING)
    private Issuer issuer;


    @Relationship(type = CIRCULATED_FROM, direction = Relationship.Direction.OUTGOING)
    private ArrayList<Year> circulatedFromYears = new ArrayList<>();

    @Relationship(type = CIRCULATED_TILL, direction = Relationship.Direction.OUTGOING)
    private ArrayList<Year> circulatedTillYears = new ArrayList<>();


    private Boolean isActual;

    public Currency() {
    }

    public Currency(String name) {
        this.name = name;
    }

    public Currency(String nid, String fullName) {
        this.nid = nid;
        this.fullName = fullName;
    }


}
