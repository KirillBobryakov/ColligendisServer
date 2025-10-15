package bkv.colligendis.database.entity.numista;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

@Node("TECHNIQUE")
public class Technique extends AbstractEntity {
    public static final String LABEL = "TECHNIQUE";

    private String nid;
    private String name;

    public Technique(String nid, String name) {
        this.nid = nid;
        this.name = name;

    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Coins, Tokens, Medals
    // <option value="1" data-select2-id="141">Cast</option>
    // <option value="36" data-select2-id="142">Coloured</option>
    // <option value="5" data-select2-id="143">Countermarked</option>
    // <option value="6" data-select2-id="144">Counterstamped</option>
    // <option value="7" data-select2-id="145">Cut</option>
    // <option value="4" data-select2-id="146">Engraved</option>
    // <option value="2" data-select2-id="147">Hammered</option>
    // <option value="24" data-select2-id="148">Hammered (bean)</option>
    // <option value="20" data-select2-id="149">Hammered (bracteate)</option>
    // <option value="25" data-select2-id="150">Hammered (bullet)</option>
    // <option value="23" data-select2-id="151">Hammered (cob)</option>
    // <option value="21" data-select2-id="152">Hammered (scyphate)</option>
    // <option value="22" data-select2-id="153">Hammered (wire)</option>
    // <option value="11" data-select2-id="154">Incuse</option>
    // <option value="41" data-select2-id="155">Injection moulding</option>
    // <option value="38" data-select2-id="156">Inlaid</option>
    // <option value="12" data-select2-id="157">Klippe</option>
    // <option value="3" data-select2-id="17">Milled</option>
    // <option value="39" data-select2-id="158">Milled (high relief)</option>
    // <option value="10" data-select2-id="159">Roller milled</option>

    // Paper, Paper Exonumia

    // <option value="34" data-select2-id="152">Cut</option>
    // <option value="29" data-select2-id="153">Intaglio</option>
    // <option value="30" data-select2-id="154">Letterpress</option>
    // <option value="32" data-select2-id="155">Lithography</option>
    // <option value="37" data-select2-id="156">Offset</option>
    // <option value="33" data-select2-id="157">Overprinted</option>
    // <option value="35" data-select2-id="158">Perforated</option>
    // <option value="31" data-select2-id="159">Screen printing</option>
}
