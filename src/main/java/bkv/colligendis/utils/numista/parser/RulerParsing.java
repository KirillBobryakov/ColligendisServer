package bkv.colligendis.utils.numista.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.database.entity.numista.RulerGroup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RulerParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(RulerParsing.class);

    public RulerParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            ArrayList<Map<String, String>> rulerMapArray = new ArrayList<>();

            int index = 0;
            do {
                Map<String, String> rulerMap = getAttributeWithTextSingleOption(pageParser.getNumistaPage(),
                        "#ruler" + index,
                        "value");
                if (rulerMap == null)
                    break;

                rulerMapArray.add(rulerMap);
                index++;
            } while (true);

            List<UUID> foundRulersUUIDs = rulerMapArray.stream()
                    .map(map -> {
                        String rulerNid = map.get("value");
                        UUID rulerUUID = rulerService.findUuidByNid(rulerNid);
                        if (rulerUUID == null) {
                            parseRulersByIssuerCodeFromPHPRequest(pageParser.getIssuerUuid());
                        }
                        rulerUUID = rulerService.findUuidByNid(rulerNid);
                        assert rulerUUID != null;

                        return rulerUUID;
                    })
                    .collect(Collectors.toList());

            if (nTypeService.equateRulers(pageParser.getNTypeUuid(), foundRulersUUIDs)) {
                result = ParsingResult.CHANGED;
            }

            return result;
        });

        this.partName = "Ruler";
    }

    public static final String RULERS_BY_ISSUER_PREFIX = "https://en.numista.com/catalogue/get_rulers.php?country=";

    /**
     * Get all rulers by Issuer's Code via PHP request
     *
     * @param issuer Issuer
     */
    public static boolean parseRulersByIssuerCodeFromPHPRequest(UUID issuerUuid) {

        assert issuerUuid != null;

        final String issuerCode = issuerService.getCode(issuerUuid);

        Document currenciesPHPDocument = loadPageByURL(RULERS_BY_ISSUER_PREFIX + issuerCode, false);

        if (currenciesPHPDocument == null) {
            logger.error("Can't get PHP request with URL" + RULERS_BY_ISSUER_PREFIX + issuerCode);
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
        UUID rulerGroupUuid = null;

        List<UUID> rulerUUIDs = issuerService.getRulers(issuerUuid);
        rulerUUIDs.forEach(rulerUUID -> rulerService.detachIssuer(rulerUUID, issuerUuid));

        for (Element option : options) {
            String nid = getAttribute(option, "value");

            if (nid == null) {
                logger.error("Can't find a nid for parsed Ruler");
                return false;
            }

            String fullName = option.text();

            if (nid.startsWith("g")) { // All ruler's groups nid starts with "g" symbol. Option with RulerGroup
                rulerGroupUuid = rulerGroupService.findUuidByName(fullName);
                // rulerGroup = rulerGroupService.findRulerGroupByNid(nid);

                if (rulerGroupUuid == null) {
                    rulerGroupUuid = rulerGroupService.save(new RulerGroup(nid, fullName)).getUuid();
                }
            } else { // Option with Ruler

                // If option's name doesn't contain " " this means that the ruler without group
                if (!fullName.contains(" ")) {
                    rulerGroupUuid = null;
                }

                String rulerName = "";

                if (fullName.contains("(")) { // If Period exists for current Ruler, then get only name without Period
                                              // and first char '8199' symbol
                    rulerName = fullName.substring(0, fullName.indexOf("(") - 1).trim().replace(" ", "");
                } else {
                    rulerName = fullName.trim();
                }

                UUID rulerUuid = rulerService.findUuidByNid(nid);
                // Ruler ruler = rulerService.findByNid(nid);
                // Ruler ruler = null;
                if (rulerUuid == null) {
                    rulerUuid = rulerService.save(new Ruler(nid, rulerName)).getUuid();
                } else { // Need to check Ruler's name in Graph and name from request
                    rulerService.setName(rulerUuid, rulerName);
                }

                if (rulerGroupUuid != null) {
                    rulerService.setRulerGroup(rulerUuid, rulerGroupUuid);
                }

                // String testName = " Ferdinand I (1887-1918), (1918-1948)";
                // String testName = " Ferdinand I (1887)";
                // String testName = " Ferdinand I (1887-date)";

                Pair<List<UUID>, List<UUID>> periods = parseYearPeriods(fullName);

                rulerService.detachRulesFromYears(rulerUuid);
                rulerService.detachRulesTillYears(rulerUuid);

                if (periods != null) {
                    for (UUID fromYear : periods.getLeft()) {
                        rulerService.addRuleFromYear(rulerUuid, fromYear);
                    }

                    for (UUID tillYear : periods.getRight()) {
                        rulerService.addRuleTillYear(rulerUuid, tillYear);
                    }
                }

                rulerService.setActual(rulerUuid);
                rulerService.setIssuer(rulerUuid, issuerUuid);
            }

        }

        return true;
    }
}
