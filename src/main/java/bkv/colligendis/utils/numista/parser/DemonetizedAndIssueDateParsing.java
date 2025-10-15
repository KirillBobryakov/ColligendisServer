package bkv.colligendis.utils.numista.parser;

import org.jsoup.nodes.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DemonetizedAndIssueDateParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(DemonetizedAndIssueDateParsing.class);

    public DemonetizedAndIssueDateParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            Element demonetisationYesElement = pageParser.getNumistaPage()
                    .selectFirst("input[type=radio][name=demonetisation][value=oui]");
            Element demonetisationNoElement = pageParser.getNumistaPage()
                    .selectFirst("input[type=radio][name=demonetisation][value=non]");
            Element demonetisationUnknownElement = pageParser.getNumistaPage()
                    .selectFirst("input[type=radio][name=demonetisation][value=inconnu]");

            if (demonetisationNoElement != null && demonetisationNoElement.attributes().hasKey("checked")) {

                if (!nTypeService.compareDemonetized(pageParser.getNTypeUuid(), "0")) {
                    nTypeService.setDemonetization(pageParser.getNTypeUuid(), "0", null, null, null);
                    result = ParsingResult.CHANGED;
                }

            } else if (demonetisationUnknownElement != null
                    && demonetisationUnknownElement.attributes().hasKey("checked")) {

                if (!nTypeService.compareDemonetized(pageParser.getNTypeUuid(), "2")) {
                    nTypeService.setDemonetization(pageParser.getNTypeUuid(), "2", null, null, null);
                    result = ParsingResult.CHANGED;
                }

            } else if (demonetisationYesElement != null && demonetisationYesElement.attributes().hasKey("checked")) {
                // DebugUtil.showInfo(DemonetizedParser.class, "The Demonetisation of NType is
                // Yes.");

                String year = getAttribute(pageParser.getNumistaPage().selectFirst("#ad"), "value");
                String month = getAttribute(pageParser.getNumistaPage().selectFirst("#md"), "value");
                String day = getAttribute(pageParser.getNumistaPage().selectFirst("#jd"), "value");

                if (!nTypeService.compareDemonetized(pageParser.getNTypeUuid(), "1")) {
                    nTypeService.setDemonetization(pageParser.getNTypeUuid(), "1", year, month, day);
                    result = ParsingResult.CHANGED;
                } else {
                    if (!nTypeService.compareDemonetizationYear(pageParser.getNTypeUuid(), year)) {
                        nTypeService.setDemonetizationYear(pageParser.getNTypeUuid(), year);
                        result = ParsingResult.CHANGED;
                    }
                    if (!nTypeService.compareDemonetizationMonth(pageParser.getNTypeUuid(), month)) {
                        nTypeService.setDemonetizationMonth(pageParser.getNTypeUuid(), month);
                        result = ParsingResult.CHANGED;
                    }
                    if (!nTypeService.compareDemonetizationDay(pageParser.getNTypeUuid(), day)) {
                        nTypeService.setDemonetizationDay(pageParser.getNTypeUuid(), day);
                        result = ParsingResult.CHANGED;
                    }
                }

            }

            String yearIssueDate = getAttribute(pageParser.getNumistaPage().selectFirst("input[name=year_issue_date]"),
                    "value");
            String monthIssueDate = getAttribute(
                    pageParser.getNumistaPage().selectFirst("input[name=month_issue_date]"),
                    "value");
            String dayIssueDate = getAttribute(pageParser.getNumistaPage().selectFirst("input[name=day_issue_date]"),
                    "value");

            if (!nTypeService.compareYearIssueDate(pageParser.getNTypeUuid(), yearIssueDate)) {
                nTypeService.setYearIssueDate(pageParser.getNTypeUuid(), yearIssueDate);
                result = ParsingResult.CHANGED;
            }
            if (!nTypeService.compareMonthIssueDate(pageParser.getNTypeUuid(), monthIssueDate)) {
                nTypeService.setMonthIssueDate(pageParser.getNTypeUuid(), monthIssueDate);
                result = ParsingResult.CHANGED;
            }
            if (!nTypeService.compareDayIssueDate(pageParser.getNTypeUuid(), dayIssueDate)) {
                nTypeService.setDayIssueDate(pageParser.getNTypeUuid(), dayIssueDate);
                result = ParsingResult.CHANGED;
            }

            return result;
        });

        this.partName = "Demonetized and Issue Date";
    }

    /*
     * Select the appropriate option:
     * 
     * Unknown: for coins that were never in circulation, such as patterns, and for
     * coins with an uncertain legal tender status.
     * No: for coins that are currently accepted as legal tender
     * Yes: for coins that are no longer legal tender.
     * 
     * 
     * Date: for demonetized coins, record the date of the withdrawal of the legal
     * tender status as yyyy-mm-dd. Note that this date may be different from the
     * date of the retirement from circulation. Should the precise day not be known,
     * “00” can be used:
     * 
     * 2001-12-31
     * 1875-00-00
     */

    /*
     * 
     * Enter the date when the banknote was issued. The date of issue can be the
     * date when a banknote started to enter circulation or the date when a
     * commemorative banknote started to be available for sale. Use the official
     * date when it exists. If different varieties of the banknote were issued at
     * different date, enter the first date; you can specify the other dates in the
     * comments.
     * 
     * The date should be entered in yyyy-mm-dd format. Should the precise day or
     * the precise day and month not be known, “00” can be used:
     * 2001-12-31
     * 1875-00-00
     * 
     * If the banknote was never issued, for example in case of change of currency
     * before the banknote was released, check “Never issued”.
     * 
     * For items that are not intended to be issued, the field should be blank.
     */

}
