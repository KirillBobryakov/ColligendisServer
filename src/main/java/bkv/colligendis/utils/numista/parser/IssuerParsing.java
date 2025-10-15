package bkv.colligendis.utils.numista.parser;

import java.util.Map;
import java.util.UUID;

import bkv.colligendis.database.entity.numista.Issuer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IssuerParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(IssuerParsing.class);

    public IssuerParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            Map<String, String> emetteur = getAttributeWithTextSingleOption(pageParser.getNumistaPage(), "#emetteur",
                    "value");

            if (emetteur == null) {
                logger.error("Can't find Issuer on the page : " + pageParser.getNTypeUuid());
                return ParsingResult.ERROR;
            }

            String code = emetteur.get("value");
            String name = emetteur.get("text");

            Map<String, Object> issuerNameAndCode = issuerService.getIssuerNameAndCode(pageParser.getNTypeUuid());

            if (issuerNameAndCode != null
                    && issuerNameAndCode.get("name") != null && issuerNameAndCode.get("name").equals(name)
                    && issuerNameAndCode.get("code") != null && issuerNameAndCode.get("code").equals(code)) {
                return ParsingResult.NOT_CHANGED;
            }

            UUID issuerUuid = issuerService.findUuidByCode(code);
            if (issuerUuid == null) {
                issuerUuid = issuerService.save(new Issuer(code, name)).getUuid();
            }

            if (!issuerService.compareName(issuerUuid, name)) {
                issuerService.setName(issuerUuid, name);
                result = ParsingResult.CHANGED;
            }

            if (!nTypeService.hasRelationshipToIssuer(pageParser.getNTypeUuid(), issuerUuid)) {
                nTypeService.setIssuer(pageParser.getNTypeUuid(), issuerUuid);
                result = ParsingResult.CHANGED;
            }

            pageParser.setIssuerUuid(issuerUuid);

            return result;

        });

        this.partName = "Issuer";
    }

}
