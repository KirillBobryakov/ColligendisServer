package bkv.colligendis.utils.numista.parser;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bkv.colligendis.database.entity.numista.CollectibleType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CollectibleTypeParsing extends PartParser {

    private static final Logger logger = LogManager.getLogger(CollectibleTypeParsing.class);

    public CollectibleTypeParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            Element collectibleSubtype = pageParser.getNumistaPage().selectFirst("#collectible_type");

            if (collectibleSubtype == null)
                return ParsingResult.ERROR;

            Element typeElement = collectibleSubtype.getAllElements().stream()
                    .filter(element -> !element.text().equals("Unknown")
                            && getAttribute(element, "selected") != null
                            && getAttribute(element, "selected").equals("selected"))
                    .findFirst().orElse(null);

            if (typeElement == null)
                return ParsingResult.NOT_CHANGED;

            String collectibleTypeCode = getAttribute(typeElement, "value");

            UUID collectibleTypeUuid = collectibleTypeService.findUuidByCode(collectibleTypeCode);
            if (collectibleTypeUuid == null) {
                if (!parseAllTypes(collectibleSubtype)) {
                    logger.error("Can't parse all Collectible types.");
                    return ParsingResult.ERROR;
                }
                collectibleTypeUuid = collectibleTypeService.findUuidByCode(collectibleTypeCode);
                assert collectibleTypeUuid != null;
            }

            if (!nTypeService.hasRelationshipToCollectibleType(pageParser.getNTypeUuid(), collectibleTypeUuid)) {
                nTypeService.setCollectibleType(pageParser.getNTypeUuid(), collectibleTypeUuid);
                result = ParsingResult.CHANGED;
            }
            pageParser.setCollectibleTypeUuid(collectibleTypeUuid);

            return result;

        });

        this.partName = "CollectibleType";
    }

    private static boolean parseAllTypes(Element collectibleSubtype) {

        if (collectibleSubtype == null) {
            return false;
        }

        Elements elements = collectibleSubtype.children();

        CollectibleType collectibleTypeParent = null;
        CollectibleType collectibleTypeCurrent = null;
        int nbspCountLast = 0;
        for (Element optionElement : elements) {
            if (optionElement.tag().getName().equals("option")) {
                if (optionElement.text().equals("Unknown"))
                    continue;

                String optionCode = getAttribute(optionElement, "value");
                String optionName = optionElement.wholeOwnText();

                if (!optionName.startsWith(" ")) {
                    collectibleTypeParent = collectibleTypeService.update(null,
                            optionCode, optionName.replace(" ", ""), null);
                    collectibleTypeCurrent = collectibleTypeParent;
                    continue;
                }

                int nbspCount = StringUtils.countMatches(optionName, ' ');
                if (nbspCount > nbspCountLast) {
                    collectibleTypeParent = collectibleTypeCurrent;
                } else if (nbspCount < nbspCountLast) {
                    assert collectibleTypeParent != null;
                    assert collectibleTypeParent.getCollectibleTypeParent() != null;
                    collectibleTypeParent = collectibleTypeParent.getCollectibleTypeParent();
                }
                collectibleTypeCurrent = collectibleTypeService.update(null,
                        optionCode, optionName.replace(" ", ""), collectibleTypeParent);
                nbspCountLast = nbspCount;
            }
        }

        return true;
    }
}
