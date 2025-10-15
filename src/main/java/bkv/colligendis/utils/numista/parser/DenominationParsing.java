package bkv.colligendis.utils.numista.parser;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bkv.colligendis.database.entity.numista.Denomination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DenominationParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(DenominationParsing.class);

    public DenominationParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            Map<String, String> denominationAttr = getAttributeWithTextSingleOption(pageParser.getNumistaPage(),
                    "#denomination",
                    "value");

            if (denominationAttr == null) {
                logger.warn("Can't find Denomination while parsing page with nid: {}", pageParser.getNid());
                return ParsingResult.NOT_CHANGED;
            }

            String denominationNid = denominationAttr.get("value");

            UUID foundDenominationUuid = denominationService.findUuidByNid(denominationNid);

            if (foundDenominationUuid == null) {
                parseDenominationsByCurrencyCodeFromPHPRequest(pageParser.getCurrencyUuid(), denominationNid);
                foundDenominationUuid = denominationService.findUuidByNid(denominationNid);
                assert foundDenominationUuid != null;
            }

            if (!nTypeService.hasRelationshipToDenomination(pageParser.getNTypeUuid(), foundDenominationUuid)) {
                nTypeService.setDenomination(pageParser.getNTypeUuid(), foundDenominationUuid);
                result = ParsingResult.CHANGED;
            }

            pageParser.setDenominationUuid(foundDenominationUuid);

            return result;
        });
        this.partName = "Denomination";
    }

    public static final String DENOMINATIONS_BY_CURRENCY_PREFIX = "https://en.numista.com/catalogue/get_denominations.php?";

    private static boolean parseDenominationsByCurrencyCodeFromPHPRequest(UUID currencyUuid, String prefill) {
        final String currencyNid = currencyService.getNid(currencyUuid);

        Document denominationsPHPDocument = loadPageByURL(
                DENOMINATIONS_BY_CURRENCY_PREFIX + "currency=" + currencyNid + "&prefill=" + prefill, false);

        if (denominationsPHPDocument == null) {
            logger.error("Can't load PHP request");
            return false;
        }

        Elements optgroups = denominationsPHPDocument.select("optgroup");

        if (!optgroups.isEmpty()) { // need to understand what to do with OPTGROUP in IssuingEntities
            logger.error("Find OPTGROUP while parsing Denominations.");
            return false;
        }

        List<UUID> existingDenominationUUIDs = currencyService.getDenominations(currencyUuid);
        existingDenominationUUIDs
                .forEach(denominationUUID -> denominationService.detachCurrency(denominationUUID, currencyUuid));

        Elements options = denominationsPHPDocument.select("option");
        for (Element element : options) {
            String denNid = element.attributes().get("value");
            String denFullName = element.text();

            UUID foundDenominationUuid = denominationService.findUuidByNid(denNid);
            if (foundDenominationUuid == null) {
                foundDenominationUuid = denominationService.save(new Denomination(denNid)).getUuid();
            }

            denominationService.setFullName(foundDenominationUuid, denFullName);
            String denName = denFullName.contains("(") ? denFullName.substring(0, denFullName.lastIndexOf('(') - 1)
                    : denFullName;
            denominationService.setName(foundDenominationUuid, denName);

            if (denFullName.contains("(")) {
                String denNumericValueStr = denFullName
                        .substring(denFullName.lastIndexOf('(') + 1, denFullName.lastIndexOf(')')).replace(" ", "")
                        .replace(" ", "");

                denNumericValueStr = denNumericValueStr.replace("¾", "0.75");
                denNumericValueStr = denNumericValueStr.replace("⅔", "0.666");
                denNumericValueStr = denNumericValueStr.replace("⅝", "0.625");
                denNumericValueStr = denNumericValueStr.replace("⅗", "0.6");
                denNumericValueStr = denNumericValueStr.replace("½", "0.5");
                denNumericValueStr = denNumericValueStr.replace("⅖", "0.4");
                denNumericValueStr = denNumericValueStr.replace("⅜", "0.375");
                denNumericValueStr = denNumericValueStr.replace("⅓", "0.333");
                denNumericValueStr = denNumericValueStr.replace("¼", "0.25");
                denNumericValueStr = denNumericValueStr.replace("⅕", "0.2");
                denNumericValueStr = denNumericValueStr.replace("⅙", "0.166");
                denNumericValueStr = denNumericValueStr.replace("⅐", "0.143");
                denNumericValueStr = denNumericValueStr.replace("⅛", "0.125");
                denNumericValueStr = denNumericValueStr.replace("⅒", "0.1");

                Float denNumericValue = null;

                if (denNumericValueStr.contains("⁄")) {
                    float top = Float.parseFloat(denNumericValueStr.substring(0, denNumericValueStr.indexOf("⁄")));
                    float bottom = Float.parseFloat(denNumericValueStr.substring(denNumericValueStr.indexOf("⁄") + 1));
                    denNumericValue = top / bottom;
                } else {
                    try {
                        denNumericValue = Float.valueOf(denNumericValueStr);
                    } catch (NumberFormatException e) {
                        logger.error("Can't parse Denomination numericValue from '{}'", denFullName);
                        if (denNumericValueStr.matches("[a-zA-Z]+")) {
                            denominationService.setNumericValue(foundDenominationUuid, null);
                        }
                        return false;
                    }
                }

                denominationService.setNumericValue(foundDenominationUuid, denNumericValue);
            }

            denominationService.setIsActual(foundDenominationUuid, true);
            denominationService.setCurrency(foundDenominationUuid, currencyUuid);

        }

        return true;
    }
}
