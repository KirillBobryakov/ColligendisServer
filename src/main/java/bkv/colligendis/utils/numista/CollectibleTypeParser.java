package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CollectibleTypeParser extends NumistaPartParser {

    public CollectibleTypeParser() {
        super((page, nType) -> {

            Element collectibleSubtype = page.selectFirst("#collectible_type");

            if (collectibleSubtype == null)
                return ParseEvent.ERROR;

            Element typeElement = collectibleSubtype.getAllElements().stream()
                    .filter(element -> !element.text().equals("Unknown") && getAttribute(element, "selected") != null
                            && getAttribute(element, "selected").equals("selected"))
                    .findFirst().orElse(null);

            if (typeElement == null)
                return ParseEvent.NOT_CHANGED;

            String collectibleTypeCode = getAttribute(typeElement, "value");

            if (nType.getCollectibleType() != null && !collectibleTypeCode.isEmpty()
                    && nType.getCollectibleType().getCode().equals(collectibleTypeCode)) {
                return ParseEvent.NOT_CHANGED;
            }

            CollectibleType collectibleType = N4JUtil.getInstance().numistaService.collectibleTypeService
                    .findByCode(collectibleTypeCode);

            if (collectibleType == null) {
                if (!parseAllTypes(collectibleSubtype)) {
                    DebugUtil.showError(CollectibleTypeParser.class, "Can't parse all types.");
                }
            }

            collectibleType = N4JUtil.getInstance().numistaService.collectibleTypeService
                    .findByCode(getAttribute(typeElement, "value"));

            assert collectibleType != null;

            nType.setCollectibleType(collectibleType);
            return ParseEvent.CHANGED;

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
                    collectibleTypeParent = N4JUtil.getInstance().numistaService.collectibleTypeService.update(null,
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
                collectibleTypeCurrent = N4JUtil.getInstance().numistaService.collectibleTypeService.update(null,
                        optionCode, optionName.replace(" ", ""), collectibleTypeParent);
                nbspCountLast = nbspCount;
            }
        }

        return true;
    }

}
