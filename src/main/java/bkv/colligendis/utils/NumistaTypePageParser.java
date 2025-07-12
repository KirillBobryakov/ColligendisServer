package bkv.colligendis.utils;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.service.numista.NTypeService;
import bkv.colligendis.utils.numista.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public class NumistaTypePageParser {

    Document page;

    private static final Boolean showTimeMetrics = true;

    public static Boolean parse(String nid) {

        long timeMetricStartParsing = System.currentTimeMillis();

        final NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;

        DebugUtil.showInfo(NumistaTypePageParser.class, "Parsing page nid=" + nid);

        Document page = NumistaPartParser.loadPageByURL(NumistaPartParser.TYPE_PAGE_PREFIX + nid, false);
        System.out.println("Page loading" + " takes " + (System.currentTimeMillis() - timeMetricStartParsing) / 1000
                + " sec " + (System.currentTimeMillis() - timeMetricStartParsing) % 1000 + " millis");

        if (page == null)
            return false;

        boolean exists = nTypeService.existsByNid(nid);

        if (exists) { // Only informs that NType is in Graph
            DebugUtil.showWarning(NumistaTypePageParser.class, "Parsing existing Numista Type with nid=" + nid);
        } else { // Create new NType with nid and title - unique fields
            DebugUtil.showError(NumistaTypePageParser.class, "Parsing new Numista Type with nid=" + nid);
            // todo временное решение до заполнения базы через списки всех монет, банкнот и
            // экзонимии
            String designation = getAttribute(page.selectFirst("#designation"), "value");
            nTypeService.save(new NType(nid, designation));
        }

        NType nType = nTypeService.findByNid(nid);

        boolean changed = false;

        // // Parse category (coin, banknote, exonumia)
        // switch (new CategoryParser().parseWithMetric(page, nType)){
        // case ERROR -> {
        // return false;
        // }
        // case CHANGED -> {
        // changed = true;
        // DebugUtil.showInfo(nType, "Category was changed");
        // }
        // }

        // Title (designation) | title
        switch (new TitleParser().parseWithMetric(page, nType)) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showWarning(nType, "Title was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        // Issuing authority | issuer
        switch (new IssuerParser().parseWithMetric(page, nType)) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "Issuer was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        // Ruling authority | ruler
        switch (new RulerParser().parseWithMetric(page, nType)) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "Ruler was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        // Issuing Entities
        switch (new IssuingEntityParser().parseWithMetric(page, nType)) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "Issuing Entity was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        // Currency
        switch (new CurrencyParser().parseWithMetric(page, nType)) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "Currency was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        // Denomination
        switch (new DenominationParser().parseWithMetric(page, nType)) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "Denomination was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        switch ((new CollectibleTypeParser().parseWithMetric(page, nType))) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "CollectibleType was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        switch ((new CommemoratedEventParser().parseWithMetric(page, nType))) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "CommemoratedEvent was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        switch ((new SeriesParser().parseWithMetric(page, nType))) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "Series was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        switch ((new DemonetizedParser().parseWithMetric(page, nType))) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "Demonetized was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        switch ((new ReferenceNumberParser().parseWithMetric(page, nType))) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "ReferenceNumber was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        switch ((new MintageParser().parseWithMetric(page, nType))) {
            case ERROR -> {
                return false;
            }
            case CHANGED -> {
                changed = true;
                DebugUtil.showInfo(nType, "Mintage was changed");
            }
            case NOT_CHANGED -> {
            }
        }

        if (changed) {
            long time = System.currentTimeMillis();
            N4JUtil.getInstance().numistaService.nTypeService.save(nType);
            if (showTimeMetrics) {
                DebugUtil.showInfo(
                        NumistaTypePageParser.class,
                        composeTimeInfo("nid: " + nType.getNid() + " Saving NType takes: ", time));
            }
        } else {
            DebugUtil.showInfo(NumistaTypePageParser.class, "nid: " + nType.getNid() + "NType without saving.");
        }

        if (showTimeMetrics) {
            DebugUtil.showInfo(
                    NumistaTypePageParser.class,
                    composeTimeInfo("nid: " + nType.getNid() + " Parsing totally takes: ", timeMetricStartParsing));
        }

        return true;
    }

    private static String composeTimeInfo(String message, long time) {
        return message + " takes " + (System.currentTimeMillis() - time) / 1000 + " sec "
                + (System.currentTimeMillis() - time) % 1000 + " millis";
    }

    private static String getAttribute(Element element, String key) {
        if (element != null && !element.attributes().get(key).isEmpty()) {
            return element.attributes().get(key);
        }
        return null;
    }

    private static Map<String, String> getAttributeWithTextSingleOption(Document page, String searchQuery, String key) {
        Element element = page.selectFirst(searchQuery);

        if (element == null) {
            DebugUtil.showWarning(NumistaTypePageParser.class, "Can't find " + searchQuery + " on the page");
            return null;
        }

        Element option = element.selectFirst("option");

        if (option == null) {
            DebugUtil.showWarning(
                    NumistaTypePageParser.class, "Can't find <option> tag in " + searchQuery + " on the page");
            return null;
        }

        if (option.text().isEmpty()) {
            DebugUtil.showWarning(NumistaTypePageParser.class, "The " + searchQuery + " name is empty on the page");
            return null;
        }

        if (option.attributes().get(key).isEmpty()) {
            DebugUtil.showWarning(
                    NumistaTypePageParser.class, "The " + searchQuery + " " + key + " is empty on the page");
            return null;
        }

        return Map.of(key, option.attributes().get(key), "text", option.text());
    }

    /**
     * Find in Map {@code hashMap} values with keys ("value", "text") and check on
     * {@code null} and {@code empty}
     *
     * @param hashMap Map with elements with "value" and "text" keys
     * @return {@code true} if values by "value" and "text" keys in Map is not null
     *         and is not empty, else - {@code false}.
     */
    private static boolean isValueAndTextNotNullAndNotEmpty(HashMap<String, String> hashMap) {
        return hashMap.get("value") != null && !hashMap.get("value").isEmpty() && hashMap.get("text") != null
                && !hashMap.get("text").isEmpty();
    }

}
