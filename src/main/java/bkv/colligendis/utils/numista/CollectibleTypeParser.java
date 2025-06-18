package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.entity.numista.CollectibleTypeGroup;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CollectableTypeParser extends NumistaPartParser {

    public TypeParser() {
        super((page, nType) -> {

            Element collectibleSubtype = page.selectFirst("#collectible_subtype");

            if(collectibleSubtype == null) return ParseEvent.ERROR;

            Element typeElement = collectibleSubtype.getAllElements().stream().filter(element -> !element.text().equals("Unknown") && getAttribute(element, "selected") != null && getAttribute(element, "selected").equals("selected")).findFirst().orElse(null);

            if(typeElement == null) return ParseEvent.NOT_CHANGED;

            CollectibleType collectibleType = N4JUtil.getInstance().numistaService.typeService.findByCode(getAttribute(typeElement, "value"));

            if(collectibleType == null) {
                if(!parseAllTypes(collectibleSubtype)) {
                    DebugUtil.showError(TypeParser.class, "Can't parse all types.");
                }
            }

            collectibleType = N4JUtil.getInstance().numistaService.typeService.findByCode(getAttribute(typeElement, "value"));

            assert collectibleType != null;

            nType.setCollectibleType(collectibleType);
            return ParseEvent.CHANGED;

        });
    }

    private static boolean parseAllTypes(Element collectibleSubtype) {

        if (collectibleSubtype == null) {
            return false;
        }

        Elements elements = collectibleSubtype.children();


        for(Element element : elements){

            if(element.tag().getName().equals("option")){
                if(element.text().equals("Unknown")) continue;

                String optionCode = getAttribute(element, "value");
                String optionName = element.text();

                N4JUtil.getInstance().numistaService.typeService.update(null, optionCode, optionName, null);
            }

            if(element.tag().getName().equals("optgroup")){
                String optgroupName = getAttribute(element, "label");

                CollectibleTypeGroup typeGroup = N4JUtil.getInstance().numistaService.typeGroupService.update(optgroupName);
                assert typeGroup != null;
                //
                element.children().stream().parallel().forEach(elem -> N4JUtil.getInstance().numistaService.typeService.update(null, getAttribute(elem, "value"), elem.text(), typeGroup));
            }
        }



        return true;
    }

}
