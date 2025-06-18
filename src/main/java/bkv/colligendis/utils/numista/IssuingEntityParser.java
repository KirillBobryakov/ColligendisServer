package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.IssuingEntity;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.NumistaEditPageUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IssuingEntityParser extends NumistaPartParser {

    public static final String ISSUING_ENTITIES_BY_ISSUER_PREFIX = "https://en.numista.com/catalogue/get_issuing_entities.php?prefill=&country=";



    /**
     * Load code from
     * "$.get("../get_issuing_entities.php", { country: selection, prefill: "2325"})"
     * line in Numista page. Then load name from get-php request
     */
    public IssuingEntityParser() {
        super((page, nType) -> {
            Elements elements = page.select("script");

            ParseEvent result = ParseEvent.NOT_CHANGED;


            // try to find a prefill value for IssuingEntity on page
            List<String> issuingEntityCodeList = new ArrayList<>();
            for (Element element : elements) {
                if (!element.childNodes().isEmpty()) {
                    issuingEntityCodeList = Arrays.stream(element.childNodes().get(0).toString().split("\n"))
                            .filter(s -> s.contains("$.get(\"../get_issuing_entities.php\""))
                            .map(s -> s.substring(s.indexOf("prefill:") + 10, s.indexOf("\"})")))
                            .filter(s -> !s.isEmpty())
                            .toList();

                    if(!issuingEntityCodeList.isEmpty()) break;
                }
            }

            if(issuingEntityCodeList.isEmpty()) {
                DebugUtil.showWarning(RulerParser.class, "Can't find any EssuingEntity on page for Ntype with nid : " + nType.getNid());
                return ParseEvent.NOT_CHANGED;
            }

            int countIssuingEntities = nType.getIssuingEntities().size();

            // Find mood IssuingEntities in NType
            List<String> finalIssuingEntityCodeList = issuingEntityCodeList;
            nType.getIssuingEntities().removeIf(issuingEntity -> finalIssuingEntityCodeList.stream().noneMatch(issuingEntityCode -> issuingEntityCode.equals(issuingEntity.getCode())));

            if (nType.getIssuingEntities().size() != countIssuingEntities) result = ParseEvent.CHANGED;


            // Add new IssuingEntities to NType

            for(String issuingEntityCode : issuingEntityCodeList){
                //Check if IssuingEntity is not present in nType.IssuingEntities
                if(nType.getIssuingEntities().stream().noneMatch(issuingEntity -> issuingEntity.getCode().equals(issuingEntityCode))){
                    IssuingEntity issuingEntity = N4JUtil.getInstance().numistaService.issuingEntityService.findIssuingEntityByCode(issuingEntityCode);

                    //If we can't find IssuingEntity in Database, try to loat from PHP Request
                    if (issuingEntity == null || !issuingEntity.getIsActual()) {
                        if (!parseIssuingEntitiesByIssuerCodeFromPHPRequest(nType.getIssuer())) {
                            DebugUtil.showError(RulerParser.class, "Can't parse Rulers by PHP request with URL " + ISSUING_ENTITIES_BY_ISSUER_PREFIX + nType.getIssuer().getCode() + ", while loading Rulers for Ntype with nid : " + nType.getNid());
                            return ParseEvent.ERROR;
                        }
                    }

                    //Try to load IssuingEntity one more time
                    issuingEntity = N4JUtil.getInstance().numistaService.issuingEntityService.findIssuingEntityByCode(issuingEntityCode);
                    assert issuingEntity != null;

                    nType.getIssuingEntities().add(issuingEntity);

                    result = ParseEvent.CHANGED;
                }
            }

            return result;
        });

        this.partName = "IssuingEntity";
    }

    /*
    This method looking for all Issuing Entities for Issuer.
    Firstly, detach all Issuing Entities from Issuer, then connect again only that was find.
 */
    private static boolean parseIssuingEntitiesByIssuerCodeFromPHPRequest(Issuer issuer) {
        Document issuingEntitiesPHPDocument = loadPageByURL(ISSUING_ENTITIES_BY_ISSUER_PREFIX + issuer.getCode(), false);

        if (issuingEntitiesPHPDocument == null) {
            DebugUtil.showError(NumistaEditPageUtil.class, "Can't load PHP IssuingEntities while parsing page.");
            return false;
        }

        Elements optgroups = issuingEntitiesPHPDocument.select("optgroup");

        if (!optgroups.isEmpty()) {  //need to understand what to do with OPTGROUP in IssuingEntities
            DebugUtil.showError(NumistaEditPageUtil.class, "Find OPTGROUP while parsing IssuingEntities from PHP request with issuer's code: " + issuer.getCode());
            return false;
        }


        List<IssuingEntity> issuingEntityList = N4JUtil.getInstance().numistaService.issuingEntityService.findIssuingEntitiesByIssuer(issuer);
        issuingEntityList.forEach(issuingEntity -> issuingEntity.setIssuer(null));

        Elements options = issuingEntitiesPHPDocument.select("option");
        for (Element element : options) {
            String ieCode = element.attributes().get("value");
            String ieName = element.text();

            IssuingEntity ie = N4JUtil.getInstance().numistaService.issuingEntityService.findIssuingEntityByCode(ieCode);
            if (ie == null) {
                ie = new IssuingEntity(ieCode, ieName);
            }
            ie.setIsActual(true);
            ie.setIssuer(issuer);
            N4JUtil.getInstance().numistaService.issuingEntityService.save(ie);
        }

        return true;
    }

}
