package bkv.colligendis.utils.numista.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bkv.colligendis.database.entity.numista.IssuingEntity;
import bkv.colligendis.database.service.numista.IssuerService;
import bkv.colligendis.database.service.numista.IssuingEntityService;
import bkv.colligendis.utils.N4JUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IssuingEntityParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(IssuingEntityParsing.class);

    public static final String ISSUING_ENTITIES_BY_ISSUER_PREFIX = "https://en.numista.com/catalogue/get_issuing_entities.php?prefill=&country=";

    /**
     * Load code from
     * "$.get("../get_issuing_entities.php", { country: selection, prefill:
     * "2325"})"
     * line in Numista page. Then load name from get-php request
     */

    public IssuingEntityParsing() {
        super((pageParser) -> {

            Elements elements = pageParser.getNumistaPage().select("script");

            ParsingResult result = ParsingResult.NOT_CHANGED;

            // try to find a prefill value for IssuingEntity on page
            List<String> issuingEntityCodeList = new ArrayList<String>();
            for (Element element : elements) {
                if (!element.childNodes().isEmpty()) {
                    issuingEntityCodeList = Arrays.stream(element.childNodes().get(0).toString().split("\n"))
                            .filter(s -> s.contains("$.get(\"../get_issuing_entities.php\""))
                            .map(s -> s.substring(s.indexOf("prefill:") + 10, s.indexOf("\"})")))
                            .filter(s -> !s.isEmpty())
                            .toList();

                    if (!issuingEntityCodeList.isEmpty())
                        break;
                }
            }

            if (issuingEntityCodeList.isEmpty()) {
                return ParsingResult.NOT_CHANGED;
            }

            List<UUID> foundIssuingEntitiesUUIDs = issuingEntityCodeList.stream()
                    .map(code -> {
                        UUID issuingEntityUUID = issuingEntityService.findUuidByCode(code);
                        if (issuingEntityUUID == null) {
                            parseIssuingEntitiesByIssuerCodeFromPHPRequest(pageParser.getIssuerUuid());
                        }
                        issuingEntityUUID = issuingEntityService.findUuidByCode(code);
                        assert issuingEntityUUID != null;
                        return issuingEntityUUID;
                    })
                    .collect(Collectors.toList());

            if (nTypeService.equateIssuingEntities(pageParser.getNTypeUuid(), foundIssuingEntitiesUUIDs)) {
                result = ParsingResult.CHANGED;
            }

            return result;
        });
        this.partName = "IssuingEntity";
    }

    /*
     * This method looking for all Issuing Entities for Issuer.
     * Firstly, detach all Issuing Entities from Issuer, then connect again only
     * that was find.
     */
    private static boolean parseIssuingEntitiesByIssuerCodeFromPHPRequest(UUID issuerUuid) {

        String issuerCode = issuerService.getCode(issuerUuid);
        Document issuingEntitiesPHPDocument = loadPageByURL(
                ISSUING_ENTITIES_BY_ISSUER_PREFIX + issuerCode,
                false);

        if (issuingEntitiesPHPDocument == null) {
            logger.error("Can't load PHP IssuingEntities while parsing page.");
            return false;
        }

        Elements optgroups = issuingEntitiesPHPDocument.select("optgroup");

        if (!optgroups.isEmpty()) { // need to understand what to do with OPTGROUP in IssuingEntities
            logger.error("Find OPTGROUP while parsing IssuingEntities from PHP request with issuer's code: {}",
                    issuerCode);
            return false;
        }

        List<UUID> issuingEntitiesUUIDs = issuerService.getIssuingEntities(issuerUuid);
        issuingEntitiesUUIDs
                .forEach(issuingEntityUUID -> issuingEntityService.detachIssuer(issuingEntityUUID, issuerUuid));

        Elements options = issuingEntitiesPHPDocument.select("option");
        for (Element element : options) {
            String ieCode = element.attributes().get("value");
            String ieName = element.text();

            UUID issuingEntityUUID = issuingEntityService.findUuidByCode(ieCode);
            if (issuingEntityUUID == null) {
                issuingEntityUUID = issuingEntityService.save(new IssuingEntity(ieCode, ieName)).getUuid();
            }
            issuingEntityService.setIssuer(issuingEntityUUID, issuerUuid);

        }

        return true;
    }

}
