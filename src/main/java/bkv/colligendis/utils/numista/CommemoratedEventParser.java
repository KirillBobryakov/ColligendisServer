package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.NumistaEditPageUtil;
import org.jsoup.nodes.Element;

public class TitleParser extends NumistaPartParser {

    public TitleParser() {
        super((page, nType) -> {
            String designation = getAttribute(page.selectFirst("#designation"), "value");
            DebugUtil.printProperty("title", designation, true, true, true);

            if (nType.getTitle().equals(designation)) {
                DebugUtil.showInfo(TitleParser.class, "The Title of existing NType is equal with Title on the page.");

                return ParseEvent.NOT_CHANGED;
            }

            DebugUtil.showError(TitleParser.class, "The Title of existing NType (" + nType.getTitle() + ") is not equal with Title on the page (" + designation + ")");
            nType.setTitle(designation);
            return ParseEvent.CHANGED;
        });
    }
}
