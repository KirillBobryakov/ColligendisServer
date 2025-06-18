package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.NumistaEditPageUtil;
import org.jsoup.nodes.Element;

public class TitleParser extends NumistaPartParser {

    public TitleParser() {
        super((document, nType) -> {
            String designation = getAttribute(document.selectFirst("#designation"), "value");
            DebugUtil.printProperty("title", designation, true, true, true);

            if (nType.getTitle().equals(designation)) {
                return true;
            }

            DebugUtil.showError(NumistaEditPageUtil.class, "The Title of existing NType is not equals with Title on the page. Ntype's title with nid=" + nType.getNid() + " != " + designation);
            return false;
        });
    }
}
