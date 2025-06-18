package bkv.colligendis.utils.numista;

import bkv.colligendis.utils.DebugUtil;

import java.util.Objects;

public class TitleParser extends NumistaPartParser {

    public TitleParser() {
        super((page, nType) -> {
            String designation = getAttribute(page.selectFirst("#designation"), "value");
            if(designation == null){
                DebugUtil.showError(TitleParser.class, "The Title of NType with nid = " + nType.getNid() + " can't be found. " + Objects.requireNonNull(page.selectFirst("p[class=info_box]")).text());

                return ParseEvent.ERROR;
            }
            DebugUtil.printProperty("title", designation, true, true, true);

            if (nType.getTitle().equals(designation)) {
                return ParseEvent.NOT_CHANGED;
            }

            DebugUtil.showWarning(TitleParser.class, "The Title of existing NType (" + nType.getTitle() + ") is not equal with Title on the page (" + designation + ")");
            nType.setTitle(designation);
            return ParseEvent.CHANGED;
        });

        this.partName = "Title";
    }
}
