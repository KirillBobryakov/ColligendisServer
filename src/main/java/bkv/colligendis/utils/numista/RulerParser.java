package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.database.entity.numista.RulerGroup;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RulerParser extends NumistaPartParser {

    public static final String RULERS_BY_ISSUER_PREFIX = "https://en.numista.com/catalogue/get_rulers.php?country=";

    public RulerParser() {
        super((page, nType) -> {
            ParseEvent result = ParseEvent.NOT_CHANGED;

            ArrayList<Map<String, String>> rulerMapArray = new ArrayList<>();

            int index = 0;
            do {
                Map<String, String> rulerMap = getAttributeWithTextSingleOption(page, "#ruler" + index, "value");
                if (rulerMap == null)
                    break;

                rulerMapArray.add(rulerMap);
                index++;
            } while (true);

            int countRulers = nType.getRulers().size();

            // Find mood Rulers in NType
            nType.getRulers().removeIf(ruler -> rulerMapArray.stream()
                    .noneMatch(stringStringMap -> stringStringMap.get("value").equals(ruler.getNid())));

            if (nType.getRulers().size() != countRulers)
                result = ParseEvent.CHANGED;

            // Add new Rulers to NType
            for (Map<String, String> rulerMap : rulerMapArray) {
                final String rulerNid = rulerMap.get("value");
                final String rulerName = rulerMap.get("text");

                if (nType.getRulers().stream().noneMatch(ruler -> ruler.getNid().equals(rulerNid))) {
                    Ruler ruler = N4JUtil.getInstance().numistaService.rulerService.findByNid(rulerNid);

                    if (ruler == null || !ruler.getIsActual() || !ruler.getName().equals(rulerName)) {
                        if (!parseRulersByIssuerCodeFromPHPRequest(nType.getIssuer())) {
                            DebugUtil.showError(RulerParser.class,
                                    "Can't parse Rulers by PHP request with URL " + RULERS_BY_ISSUER_PREFIX
                                            + nType.getIssuer().getCode()
                                            + " , while loading Rulers for Ntype with nid : " + nType.getNid());
                            return ParseEvent.ERROR;
                        }
                        ruler = N4JUtil.getInstance().numistaService.rulerService.findByNid(rulerNid);
                        assert ruler != null;

                    }

                    nType.getRulers().add(ruler);
                    result = ParseEvent.CHANGED;
                }
            }

            return result;
        });

        this.partName = "Ruler";
    }

    /**
     * Get all rulers by Issuer's Code via PHP request
     *
     * @param issuer Issuer
     */
    public static boolean parseRulersByIssuerCodeFromPHPRequest(Issuer issuer) {

        assert issuer != null;

        final String issuerCode = issuer.getCode();

        Document currenciesPHPDocument = loadPageByURL(RULERS_BY_ISSUER_PREFIX + issuerCode, false);

        if (currenciesPHPDocument == null) {
            DebugUtil.showError(RulerParser.class,
                    "Can't get PHP request with URL" + RULERS_BY_ISSUER_PREFIX + issuerCode);
            return false;
        }
        /*
         * <option value="g681" style="font-weight:bold">House of Battenberg</option>
         * <option value="2051"> Alexander I (1879-1886)</option>
         * <option value="g682" style="font-weight:bold">House of Saxe-Coburg and
         * Gotha-Koháry</option>
         * <option value="2052"> Ferdinand I (1887-1918)</option>
         * <option value="2053"> Boris III (1918-1943)</option>
         * <option value="11690"> Simeon II (1943-1946)</option>
         * <option value="2054">People's Republic (1946-1990)</option>
         * <option value="2055">Republic (1990-date)</option>
         */

        Elements options = currenciesPHPDocument.select("option");
        RulerGroup rulerGroup = null;

        List<Ruler> rulerList = N4JUtil.getInstance().numistaService.rulerService.findRulesByIssuer(issuer);
        rulerList.forEach(ruler -> ruler.setIssuer(null));

        for (Element option : options) {
            String nid = getAttribute(option, "value");

            if (nid == null) {
                DebugUtil.showError(RulerParser.class, "Can't find a nid for parsed Ruler");
                return false;
            }

            String fullName = option.text();

            if (nid.startsWith("g")) { // All ruler's groups nid starts with "g" symbol. Option with RulerGroup
                rulerGroup = N4JUtil.getInstance().numistaService.rulerGroupService.findRulerGroupByNid(nid);

                if (rulerGroup == null) {
                    rulerGroup = new RulerGroup(nid, fullName);
                    rulerGroup = N4JUtil.getInstance().numistaService.rulerGroupService.save(rulerGroup);
                }
            } else { // Option with Ruler

                // If option's name doesn't contain " " this means that the ruler without group
                if (!fullName.contains(" ")) {
                    rulerGroup = null;
                }

                String rulerName = "";

                if (fullName.contains("(")) { // If Period exists for current Ruler, then get only name without Period
                                              // and first char '8199' symbol
                    rulerName = fullName.substring(0, fullName.indexOf("(") - 1).trim().replace(" ", "");
                } else {
                    rulerName = fullName.trim();
                }

                Ruler ruler = N4JUtil.getInstance().numistaService.rulerService.findByNid(nid);
                if (ruler == null) {
                    ruler = new Ruler(nid, rulerName);
                } else { // Need to check Ruler's name in Graph and name from request
                    if (!ruler.getName().equals(rulerName)) {
                        DebugUtil.showWarning(RulerParser.class, "Ruler's Name in Graph = " + ruler.getName()
                                + " is not equal to another one from PHP Request = " + rulerName);
                        ruler.setName(rulerName);
                    }

                    // if (ruler.getIsActual() != null && ruler.getIsActual()) {
                    // continue;
                    // }
                }

                ruler.setRulerGroup(rulerGroup);

                // String testName = " Ferdinand I (1887-1918), (1918-1948)";
                // String testName = " Ferdinand I (1887)";
                // String testName = " Ferdinand I (1887-date)";

                Pair<List<Year>, List<Year>> periods = parseYearPeriods(fullName);

                if (periods != null) {
                    for (Year fromYear : periods.getLeft()) {
                        if (!ruler.getRulesFromYears().contains(fromYear)) {
                            ruler.getRulesFromYears().add(fromYear);
                        }
                    }

                    for (Year tillYear : periods.getRight()) {
                        if (!ruler.getRulesTillYears().contains(tillYear)) {
                            ruler.getRulesTillYears().add(tillYear);
                        }
                    }
                }

                ruler.setIsActual(true);
                ruler.setIssuer(issuer);
                N4JUtil.getInstance().numistaService.rulerService.save(ruler);
            }

        }

        return true;
    }

}
