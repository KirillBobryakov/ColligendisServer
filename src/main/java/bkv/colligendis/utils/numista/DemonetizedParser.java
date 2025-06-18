package bkv.colligendis.utils.numista;

import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.jsoup.nodes.Element;

import java.util.Objects;

public class DemonetizedParser extends NumistaPartParser {


    /*
            Select the appropriate option:

        Unknown: for coins that were never in circulation, such as patterns, and for coins with an uncertain legal tender status.
        No: for coins that are currently accepted as legal tender
        Yes: for coins that are no longer legal tender.


        Date: for demonetized coins, record the date of the withdrawal of the legal tender status as yyyy-mm-dd. Note that this date may be different from the date of the retirement from circulation. Should the precise day not be known, “00” can be used:

        2001-12-31
        1875-00-00
     */

    public DemonetizedParser() {
        super((page, nType) -> {

            Element demonetisationYesElement = page.selectFirst("input[type=radio][name=demonetisation][value=oui]");
            Element demonetisationNoElement = page.selectFirst("input[type=radio][name=demonetisation][value=non]");
            Element demonetisationUnknownElement = page.selectFirst("input[type=radio][name=demonetisation][value=inconnu]");

            if(demonetisationNoElement != null && demonetisationNoElement.attributes().hasKey("checked")){
                DebugUtil.showInfo(DemonetizedParser.class, "The Demonetisation of NType is NO.");
                if(nType.getDemonetized() != null && nType.getDemonetized().equals("0")){
                    return ParseEvent.NOT_CHANGED;
                } else {
                    nType.setDemonetized("0");
                    nType.setDemonetizationDay(null);
                    nType.setDemonetizationMonth(null);
                    nType.setDemonetizationYear(null);

                    return ParseEvent.CHANGED;
                }
            } else if(demonetisationUnknownElement != null && demonetisationUnknownElement.attributes().hasKey("checked")){
                DebugUtil.showInfo(DemonetizedParser.class, "The Demonetisation of NType is Unknown.");
                if(nType.getDemonetized() != null && nType.getDemonetized().equals("2")){
                    return ParseEvent.NOT_CHANGED;
                } else {
                    nType.setDemonetized("2");
                    nType.setDemonetizationDay(null);
                    nType.setDemonetizationMonth(null);
                    nType.setDemonetizationYear(null);

                    return ParseEvent.CHANGED;
                }
            } else if(demonetisationYesElement != null && demonetisationYesElement.attributes().hasKey("checked")){
//                DebugUtil.showInfo(DemonetizedParser.class, "The Demonetisation of NType is Yes.");

                String year = getAttribute(page.selectFirst("#ad"), "value");
                String month = getAttribute(page.selectFirst("#md"), "value");
                String day = getAttribute(page.selectFirst("#jd"), "value");

                if(nType.getDemonetized() != null && nType.getDemonetized().equals("1")) {
                    return ParseEvent.NOT_CHANGED;
                } else {
                    nType.setDemonetized("1");
                    nType.setDemonetizationYear(year);
                    nType.setDemonetizationMonth(month);
                    nType.setDemonetizationDay(day);

                    return ParseEvent.CHANGED;
                }

            }

            DebugUtil.showError(DemonetizedParser.class, "The Demonetisation of NType can't be parsed.");

            return ParseEvent.ERROR;
        });

        this.partName = "Demonetized";
    }
}
