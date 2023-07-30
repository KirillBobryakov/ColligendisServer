package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("TYPE")
public class Type extends AbstractEntity {

    //Coin
//    String common_coin = "Standard circulation coin";
//    String commemorative_coin = "Circulating commemorative coin";
//    String non_circulating_coin = "Non-circulating coin";
//    String pattern = "Pattern";
//    String token = "Token";

    //Banknote
//    String common_note = "Standard banknote";
//    String commemorative_note = "Commemorative note";
//    String local_note = "Local banknote";
//    String trial_note = "Trial banknote";


    //Exonumias
//      <optgroup label="Automatic tokens">
//          <option value="access_token">Access token</option>
//          <option value="car_wash_token">Car wash token</option>
//          <option value="deposit_token">Deposit token</option><
//          option value="dispenser_token">Dispenser token</option>
//          <option value="game_token">Game token</option>
//          <option value="locker_token">Locker token</option>
//          <option value="telecommunication_token">Telecommunication token</option>
//          <option value="parking_token">Parking token</option>
//          <option value="phonograph_token">Phonograph token</option>
//          <option value="ride_token">Ride token</option>
//          <option value="transit_token">Transit token</option>
//      </optgroup>
//          <optgroup label="Confinement tokens">
//          <option value="internment_token">Internment or prison token</option>
//          <option value="leper_colony_token">Leper colony token</option>
//          <option value="military_token">Military token</option>
//          <option value="school_token">School token</option>
//          <option value="work_camp_token">Work encampment token</option>
//      </optgroup>
//      <optgroup label="Event tokens">
//          <option value="shooting_festival_token">Shooting festival token</option>
//          <option value="festival_token">Festival token</option>
//      </optgroup>
//      <optgroup label="Ration tokens">
//          <option value="food_ration_token">Food ration token</option>
//          <option value="fuel_token">Fuel token</option>
//          <option value="utility_token">Utilities token</option>
//      </optgroup>
//      <optgroup label="Trade tokens">
//          <option value="business_token">Business token</option>
//          <option value="cooperative_token">Co-operative token</option>
//          <option value="local_token">Local administration token</option>
//          <option value="taxation_token">Taxation token</option>
//          <option value="wage_token">Wage token</option>
//      </optgroup>
//      <optgroup label="Miscellaneous tokens">
//          <option value="religious_token">Religious token</option>
//          <option value="ball_token">Dance token</option>
//          <option value="brothel_token">Brothel token</option>
//          <option value="casino_token">Casino chip or token</option>
//      </optgroup>
//      <optgroup label="Award medals">
//          <option value="cultural_award">Cultural award</option>
//          <option value="industrial_agricultural_award">Industrial or agricultural award</option>
//          <option value="military_award">Military award</option>
//          <option value="misc_award">Miscellaneous award</option>
//          <option value="scholar_award">Scholastic or academic award</option>
//          <option value="scientific_award">Scientific award</option>
//          <option value="sport_award">Sporting award</option>
//      </optgroup>
//      <optgroup label="Commemorative medals">
//          <option value="achievement_medal">Achievement medal</option>
//          <option value="company_medal">Company medal</option>
//          <option value="event_medal">Event medal</option>
//          <option value="historical_medal">Historical medal</option>
//          <option value="inauguration_medal">Inauguration medal</option>
//          <option value="institution_medal">Institution medal</option>
//          <option value="misc_medal" selected>Military medal</option>
//          <option value="personality_medal">Personality or portrait medal</option>
//          <option value="religious_medal">Religious medal</option>
//          <option value="satirical_medal">Satirical medal</option>
//          <option value="society_medal">Society medal</option>
//      </optgroup>
//      <optgroup label="Promotional items">
//          <option value="advertising_token">Advertising token</option>
//          <option value="campaign_token">Campaign token</option>
//          <option value="membership_token">Membership token</option>
//      </optgroup>
//      <optgroup label="Souvenir medals">
//          <option value="art_medal">Art medal</option>
//          <option value="tourist_souvenir">Tourist souvenir</option>
//          <option value="elongated">Elongated coin</option>
//          <option value="commeorative_souvenir">Commemorative souvenir</option>
//          <option value="event_souvenir">Event souvenir</option>
//          <option value="replica">Replica</option>
//      </optgroup>
//      <optgroup label="Bullion">
//          <option value="bullion_rounds">Round</option>
//          <option value="bullion_bar">Bar</option>
//      </optgroup>
//      <optgroup label="Coin patterns">
//          <option value="mint_token">Mint token</option>
//          <option value="coin_pattern">Coin pattern</option>
//      </optgroup>
//      <optgroup label="Contemporary counterfeits">
//          <option value="contemporary_counterfeit">Contemporary counterfeit</option>
//      </optgroup>
//      <optgroup label="Utility items">
//          <option value="counter_token">Counter token</option>
//          <option value="coin_weight">Coin weight</option>
//      </optgroup>
//      <optgroup label="Fantasy items">
//          <option value="fantasy_currency">Fantasy currency</option>
//          <option value="pre_euro">ECU &amp; Pre 1999 Euro</option>
//          <option value="collector_currency">Official collector currency</option>
//          <option value="fantasy_place">Fantasy item of a fictional place</option>
//          <option value="play_money">Play money</option>
//      </optgroup>
//      <optgroup label="Primitive money">
//          <option value="primitive_money">Primitive money</option>
//      </optgroup>
//      <optgroup label="Unclassified exonumia">
//          <option value="other_token">Miscellaneous token</option>
//      </optgroup>

    public static final String UNDER_TYPE_GROUP = "UNDER_TYPE_GROUP";

    private String code;

    private String name;

    @Relationship(type = UNDER_TYPE_GROUP, direction = Relationship.Direction.OUTGOING)
    private TypeGroup group;

    public Type(String code, String name, TypeGroup group) {
        this.code = code;
        this.name = name;
        this.group = group;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeGroup getGroup() {
        return group;
    }

    public void setGroup(TypeGroup group) {
        this.group = group;
    }
}
