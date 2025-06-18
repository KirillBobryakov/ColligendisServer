package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.Denomination;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.NumistaEditPageUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;

public class DenominationParser extends NumistaPartParser {

    public static final String DENOMINATIONS_BY_CURRENCY_PREFIX = "https://en.numista.com/catalogue/get_denominations.php?";

    public DenominationParser() {
        super((page, nType) -> {

            Map<String, String> denominationAttr = getAttributeWithTextSingleOption(page, "#denomination", "value");

            if (denominationAttr == null) {
                DebugUtil.showWarning(DenominationParser.class, "Can't find Denomination while parsing page with nid: " + nType.getNid());
                return ParseEvent.NOT_CHANGED;
            }

            String denominationNid = denominationAttr.get("value");
            if (nType.getDenomination() != null && nType.getDenomination().getIsActual() != null && nType.getDenomination().getIsActual() && nType.getDenomination().getNid().equals(denominationNid)) {
                return ParseEvent.NOT_CHANGED;
            }

            Denomination denomination = N4JUtil.getInstance().numistaService.denominationService.findDenominationByNid(denominationNid);
            if (denomination != null && denomination.getIsActual() != null && denomination.getIsActual()) {
                nType.setDenomination(denomination);
                return ParseEvent.CHANGED;
            }



            if (!parseDenominationsByCurrencyCodeFromPHPRequest(nType.getCurrency(), denominationNid)) {
                DebugUtil.showError(DenominationParser.class, "Can't parse PHP Denominations while parsing page with nid: " + nType.getNid() + " URI: " + DENOMINATIONS_BY_CURRENCY_PREFIX + nType.getCurrency());
                return ParseEvent.ERROR;
            }


            denomination = N4JUtil.getInstance().numistaService.denominationService.findDenominationByNid(denominationNid);


            if (denomination == null) {
                DebugUtil.showError(DenominationParser.class, "Find a Denomination's Nid but can't find Denomination in database or create new one while parsing page with nid: " + nType.getNid());
                return ParseEvent.ERROR;
            }

            nType.setDenomination(denomination);

            return ParseEvent.CHANGED;
        });

        this.partName = "Denomination";
    }



    private static boolean parseDenominationsByCurrencyCodeFromPHPRequest(Currency currency, String prefill) {

        final String currencyCode = currency != null ? currency.getNid() : "";

        Document denominationsPHPDocument = loadPageByURL(DENOMINATIONS_BY_CURRENCY_PREFIX + "currency=" + currencyCode + "&prefill=" + prefill, false);

        if (denominationsPHPDocument == null) {
            DebugUtil.showError(DenominationParser.class, "Can't load PHP request");
            return false;
        }


        Elements optgroups = denominationsPHPDocument.select("optgroup");

        if (!optgroups.isEmpty()) {  //need to understand what to do with OPTGROUP in IssuingEntities
            DebugUtil.showError(DenominationParser.class, "Find OPTGROUP while parsing Denominations.");
            return false;
        }

        if(currency != null){
            List<Denomination> denominations = N4JUtil.getInstance().numistaService.denominationService.findDenominationsByCurrency(currency);
            denominations.forEach(denomination -> denomination.setCurrency(null));
        }

        Elements options = denominationsPHPDocument.select("option");
        for (Element element : options) {
            String denNid = element.attributes().get("value");
            String denFullName = element.text();


            Denomination den = N4JUtil.getInstance().numistaService.denominationService.findDenominationByNid(denNid);

            if (den != null && den.getIsActual() != null && den.getIsActual()) {
                den.setCurrency(currency);
                continue;
            }


            den = den != null ? den : new Denomination();

            den.setNid(denNid);
            den.setFullName(denFullName);
            String denName = denFullName.contains("(") ? denFullName.substring(0, denFullName.lastIndexOf('(') - 1) : denFullName;
            den.setName(denName);

            if(denFullName.contains("(")){
                String denNumericValueStr = denFullName.substring(denFullName.lastIndexOf('(') + 1, denFullName.lastIndexOf(')')).replace(" ", "").replace(" ", "");




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
                        DebugUtil.showError(DenominationParser.class, "Can't parse Denomination numericValue from '" + denFullName + "'");
                        if(denNumericValueStr.matches("[a-zA-Z]+")){
                            den.setNumericValue(null);
                        }
                        return false;
                    }
                }


                den.setNumericValue(denNumericValue);
            }

            den.setIsActual(true);
            den.setCurrency(currency);

            N4JUtil.getInstance().numistaService.denominationService.save(den);
        }

        return true;
    }

}
