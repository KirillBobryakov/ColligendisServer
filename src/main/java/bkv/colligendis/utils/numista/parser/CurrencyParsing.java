package bkv.colligendis.utils.numista.parser;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bkv.colligendis.database.entity.numista.Currency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CurrencyParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(CurrencyParsing.class);

    public CurrencyParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            Map<String, String> devise = getAttributeWithTextSingleOption(pageParser.getNumistaPage(), "#devise",
                    "value");

            if (devise == null) {
                logger.warn("Can't find Currency (devise) while parsing page with nid: {}", pageParser.getNid());
                return ParsingResult.NOT_CHANGED;
            }

            String currencyNid = devise.get("value");

            UUID foundCurrencyUuid = currencyService.findUuidByNid(currencyNid);
            if (foundCurrencyUuid == null) {
                UUID collectibleTypeUuid = pageParser.getCollectibleTypeUuid();
                String collectibleTypeCode = collectibleTypeService.getCode(collectibleTypeUuid);
                parseCurrenciesByIssuerCodeFromPHPRequest(pageParser.getIssuerUuid(), "", collectibleTypeCode);
                foundCurrencyUuid = currencyService.findUuidByNid(currencyNid);
                assert foundCurrencyUuid != null;
            }

            if (!nTypeService.hasRelationshipToCurrency(pageParser.getNTypeUuid(), foundCurrencyUuid)) {
                nTypeService.setCurrency(pageParser.getNTypeUuid(), foundCurrencyUuid);
                result = ParsingResult.CHANGED;
            }
            pageParser.setCurrencyUuid(foundCurrencyUuid);

            return result;
        });

        this.partName = "Currency";
    }

    public static final String CURRENCIES_BY_ISSUER_PREFIX = "https://en.numista.com/catalogue/get_currencies.php?";

    private static boolean parseCurrenciesByIssuerCodeFromPHPRequest(UUID issuerUuid, String prefill,
            String collectibleTypeCode) {

        final String issuerCode = issuerService.getCode(issuerUuid);

        Document currenciesPHPDocument = loadPageByURL(CURRENCIES_BY_ISSUER_PREFIX + "country=" + issuerCode
                + "&prefill=" + prefill + "&ct=" + collectibleTypeCode, true);

        if (currenciesPHPDocument == null) {
            logger.error("Can't load PHP Currencies while parsing page.");
            return false;
        }

        Elements optgroups = currenciesPHPDocument.select("optgroup");

        if (!optgroups.isEmpty()) { // need to understand what to do with OPTGROUP in Currencies
            logger.error("Find OPTGROUP while parsing Currencies.");
            return false;
        }

        Elements options = currenciesPHPDocument.select("option");

        if (options.isEmpty()) {
            logger.error("There is no any Currency's <option> tags");
            return false;
        }

        List<UUID> existingCurrencyUUIDs = issuerService.getCurrencies(issuerUuid);
        existingCurrencyUUIDs.forEach(currencyUUID -> currencyService.detachIssuer(currencyUUID, issuerUuid));

        for (Element element : options) {
            String curNid = element.attributes().get("value");
            String curFullName = element.text();

            UUID curUuid = currencyService.findUuidByNid(curNid);
            if (curUuid == null) {
                curUuid = currencyService.save(new Currency(curNid)).getUuid();
            }

            curFullName = curFullName.substring(curFullName.indexOf('–') + 1).trim();
            currencyService.setFullName(curUuid, curFullName);

            currencyService.detachRulesFromYears(curUuid);
            currencyService.detachRulesTillYears(curUuid);

            Pattern pattern = Pattern.compile("[(]\\S+[)]");
            Matcher matcher = pattern.matcher(curFullName);

            while (matcher.find()) {
                String periodStr = matcher.group(0);
                UUID yearFromUuid = null;
                UUID yearTillUuid = null;

                String insideParentheses = periodStr.replace("(", "").replace(")", "");

                String[] years;
                // notgeld - Mark (notgeld, 1914-1924)
                // Occupation currency - Mark (Occupation currency, 1918), Rouble (Occupation
                // currency, 1916)
                if (insideParentheses.contains(",")) {
                    String[] partsBetweenComma = insideParentheses.split(",");
                    currencyService.setKind(curUuid, partsBetweenComma[0]);

                    years = partsBetweenComma[1].trim().split("-");
                } else {
                    years = insideParentheses.split("-");
                }

                // Years can be (1887-1918), (1936), (1990-date)
                // After splitting by "-", we can get array of 2 strings or 1 string

                if (years.length == 0 || years.length > 2) {
                    logger.error("Can't parse PHP request (years for = {} with length != 1 or 2).", curFullName);
                    return false;
                } else if (years.length == 1) { // we have a period during one year, example "(1936)"
                    if (StringUtils.isNumeric(years[0])) {
                        yearFromUuid = yearService.findGregorianYearUuidByValue(Integer.parseInt(years[0]));
                        assert yearFromUuid != null;
                        yearTillUuid = yearFromUuid;
                    } else { // Try to catch another variants for ruler's period with one year which is not
                             // numeric
                        logger.error("Can't parse PHP request (period for = {} with one year which is not Numeric).",
                                curFullName);
                        return false;
                    }
                } else { // Ruler's Period has two years (1887-1918) or (1990-date)
                    if (StringUtils.isNumeric(years[0])) {
                        // Now I only know that the start year is only number
                        yearFromUuid = yearService.findGregorianYearUuidByValue(Integer.parseInt(years[0]));
                        assert yearFromUuid != null;
                    } else { // Try to catch another variants for ruler's period with two year which start
                             // year is not numeric
                        logger.error("Can't parse PHP request (start year = {} is not Numeric).", curFullName);
                        return false;
                    }

                    if (years[1].equals("date")) { // End year can be Numeric or "date". The "date" means that the
                                                   // ruling is not finished.

                    } else if (StringUtils.isNumeric(years[1])) {
                        yearTillUuid = yearService.findGregorianYearUuidByValue(Integer.parseInt(years[1]));
                        assert yearTillUuid != null;
                    } else { // Try to catch another variants for ruler's period with two year which end year
                             // is not numeric and not "date"
                        logger.error("Can't parse PHP request (end year = {} is not Numeric and not 'date').",
                                curFullName);
                        return false;
                    }
                }
                currencyService.addRuleFromYear(curUuid, yearFromUuid);
                if (yearTillUuid != null) {
                    currencyService.addRuleTillYear(curUuid, yearTillUuid);
                }

            }

            // If Period exists for current Ruler, then get only name without Period and
            // first char '8199' symbol
            String curName = curFullName.contains("(") ? curFullName.substring(0, curFullName.indexOf("(") - 1).trim()
                    : curFullName.trim();
            currencyService.setName(curUuid, curName);
            currencyService.setIssuer(curUuid, issuerUuid);
            currencyService.setIsActual(curUuid, true);

        }

        return true;
    }

}
