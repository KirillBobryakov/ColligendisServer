package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyParser extends NumistaPartParser {

    public static final String CURRENCIES_BY_ISSUER_PREFIX = "https://en.numista.com/catalogue/get_currencies.php?";

    public CurrencyParser() {
        super((page, nType) -> {

            Map<String, String> devise = getAttributeWithTextSingleOption(page, "#devise", "value");

            if (devise == null) {
                DebugUtil.showWarning(CurrencyParser.class, "Can't find Currency (devise) while parsing page with nid: " + nType.getNid());
                return ParseEvent.NOT_CHANGED;
            }

            String currencyNid = devise.get("value");

            if (nType.getCurrency() != null && nType.getCurrency().getIsActual() != null && nType.getCurrency().getIsActual() && nType.getCurrency().getNid().equals(currencyNid)) {
                return ParseEvent.NOT_CHANGED;
            }

            Currency currency = N4JUtil.getInstance().numistaService.currencyService.findCurrencyByNid(currencyNid);
            if (currency != null && currency.getIsActual() != null && currency.getIsActual()) {
                nType.setCurrency(currency);
                return ParseEvent.CHANGED;
            }

            if (!parseCurrenciesByIssuerCodeFromPHPRequest(nType.getIssuer(), "", nType.getCollectibleType().getCode())) {
                // DebugUtil.showError(CurrencyParser.class, "Can't parse PHP Currencies while parsing page with nid: " + nType.getNid() + " with Issuer's Code: " + nType.getIssuer().getCode() + " and category " + nType.getCategory().getName() + "URI: " + CURRENCIES_BY_ISSUER_PREFIX + nType.getIssuer().getCode() + "&ct=" + nType.getCategory().getName());
                return ParseEvent.ERROR;
            }

            currency = N4JUtil.getInstance().numistaService.currencyService.findCurrencyByNid(currencyNid);


            //for some ntypes need to make request with frefill parameters to avoid missing results
            if(currency == null){
                parseCurrenciesByIssuerCodeFromPHPRequest(nType.getIssuer(), currencyNid, nType.getCollectibleType().getCode());
            }

            currency = N4JUtil.getInstance().numistaService.currencyService.findCurrencyByNid(currencyNid);

            if (currency == null) {
                DebugUtil.showError(CurrencyParser.class, "Can't find Currency in Graph after PHP parsing while parsing page with nid: " + nType.getNid());
                return ParseEvent.ERROR;
            }

            nType.setCurrency(currency);

            return ParseEvent.CHANGED;
        });

        this.partName = "Currency";
    }

    private static boolean parseCurrenciesByIssuerCodeFromPHPRequest(Issuer issuer, String prefill, String collectibleTypeCode) {
        final String issuerCode = issuer.getCode();

        Document currenciesPHPDocument = loadPageByURL(CURRENCIES_BY_ISSUER_PREFIX + "country=" + issuerCode + "&prefill=" + prefill + "&ct=" + collectibleTypeCode, true);

        if (currenciesPHPDocument == null) {
            DebugUtil.showError(CurrencyParser.class, "Can't load PHP Currencies while parsing page.");
            return false;
        }

        Elements optgroups = currenciesPHPDocument.select("optgroup");

        if (!optgroups.isEmpty()) {  //need to understand what to do with OPTGROUP in Currencies
            DebugUtil.showError(CurrencyParser.class, "Find OPTGROUP while parsing Currencies.");
            return false;
        }


        Elements options = currenciesPHPDocument.select("option");

        if (options.isEmpty()) {
            DebugUtil.showError(CurrencyParser.class, "There is no any Currency's <option> tags");
            return false;
        }

        List<Currency> currencyList = N4JUtil.getInstance().numistaService.currencyService.findCurrencyByIssuer(issuer);
        currencyList.forEach(currency -> currency.setIssuer(null));


        for (Element element : options) {
            String curNid = element.attributes().get("value");
            String curFullName = element.text();

            Currency cur = N4JUtil.getInstance().numistaService.currencyService.findCurrencyByNid(curNid);

            cur = cur != null ? cur : new Currency();
            cur.setNid(curNid);

            curFullName = curFullName.substring(curFullName.indexOf('–') + 1).trim();
            cur.setFullName(curFullName);

            Pattern pattern = Pattern.compile("[(]\\S+[)]");
            Matcher matcher = pattern.matcher(curFullName);

            while (matcher.find()) {
                String periodStr = matcher.group(0);
                Year yearFrom = null;
                Year yearTill = null;

                String insideParentheses = periodStr.replace("(", "").replace(")", "");


                String[] years;
                // notgeld - Mark (notgeld, 1914-1924)
                // Occupation currency - Mark (Occupation currency, 1918), Rouble (Occupation currency, 1916)
                if (insideParentheses.contains(",")) {
                    String[] partsBetweenComma = insideParentheses.split(",");
                    cur.setKind(partsBetweenComma[0]);

                    years = partsBetweenComma[1].trim().split("-");
                } else {
                    years = insideParentheses.split("-");
                }


                // Years can be (1887-1918), (1936), (1990-date)
                // After splitting by "-", we can get array of 2 strings or 1 string

                if (years.length == 0 || years.length > 2) {
                    DebugUtil.showError(CurrencyParser.class, "Can't parse PHP request (years for = " + curFullName + " with length != 1 or 2).");
                    return false;
                } else if (years.length == 1) { // we have a period during one year, example "(1936)"
                    if (StringUtils.isNumeric(years[0])) {
                        yearFrom = N4JUtil.getInstance().numistaService.calendarService.findGregorianYearByValueOrCreate(Integer.parseInt(years[0]));
                        yearTill = yearFrom;
                    } else {    // Try to catch another variants for ruler's period with one year which is not numeric
                        DebugUtil.showError(CurrencyParser.class, "Can't parse PHP request (period for = " + curFullName + " with one year which is not Numeric).");
                        return false;
                    }
                } else {    // Ruler's Period has two years (1887-1918) or (1990-date)
                    if (StringUtils.isNumeric(years[0])) {    // Now I only know that the start year is only number
                        yearFrom = N4JUtil.getInstance().numistaService.calendarService.findGregorianYearByValueOrCreate(Integer.parseInt(years[0]));
                    } else { // Try to catch another variants for ruler's period with two year which start year is not numeric
                        DebugUtil.showError(CurrencyParser.class, "Can't parse PHP request (start year = " + curFullName + " is not Numeric).");
                        return false;
                    }

                    assert yearFrom != null;

                    if (years[1].equals("date")) {    //  End year can be Numeric or "date". The "date" means that the ruling is not finished.

                    } else if (StringUtils.isNumeric(years[1])) {
                        yearTill = N4JUtil.getInstance().numistaService.calendarService.findGregorianYearByValueOrCreate(Integer.parseInt(years[1]));
                    } else { // Try to catch another variants for ruler's period with two year which end year is not numeric and not "date"
                        DebugUtil.showError(CurrencyParser.class, "Can't parse PHP request (end year = " + curFullName + " is not Numeric and not 'date').");
                        return false;
                    }
                }


                if (!cur.getCirculatedFromYears().contains(yearFrom)) {
                    cur.getCirculatedFromYears().add(yearFrom);
                }

                if (yearTill != null && !cur.getCirculatedTillYears().contains(yearTill)) {
                    cur.getCirculatedTillYears().add(yearTill);
                }
            }


            //If Period exists for current Ruler, then get only name without Period and first char '8199' symbol
            String curName = curFullName.contains("(") ? curFullName.substring(0, curFullName.indexOf("(") - 1).trim() : curFullName.trim();
            cur.setName(curName);
            cur.setIssuer(issuer);
            cur.setIsActual(true);

            N4JUtil.getInstance().numistaService.currencyService.save(cur);
        }

        return true;
    }
}
