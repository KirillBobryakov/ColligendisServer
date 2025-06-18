package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.Composition;
import bkv.colligendis.database.entity.numista.CompositionType;
import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.service.numista.CompositionMetalType;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ObverseParser extends NumistaPartParser {

    public ObverseParser() {
        super((page, nType) -> {
            ParseEvent result = ParseEvent.NOT_CHANGED;

            ParseEvent[] parseFunctions = new ParseEvent[] {
                    parseObverseEngravers(page, nType),
                    parseObverseDesigners(page, nType),
                    parseObverseDescription(page, nType),
                    parseObverseLettering(page, nType),
                    parseObverseScripts(page, nType),
                    parseObverseUnabridgedLegend(page, nType),
                    parseObverseLetteringTranslation(page, nType),
                    parseObversePicture(page, nType),
            };

            for (ParseEvent partResult : parseFunctions) {
                if (partResult == ParseEvent.ERROR) {
                    return ParseEvent.ERROR;
                } else if (partResult == ParseEvent.CHANGED) {
                    result = ParseEvent.CHANGED;
                }
            }

            return result;
        });

        this.partName = "ObverseParser";
    }

    private static ParseEvent parseObverseEngravers(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        List<String> graveursAvers = getTextsSelectedOptions(page.selectFirst("#graveur_avers"));
        if (graveursAvers != null) {
            for (String engraver : graveursAvers) {
                if (engraver != null && !engraver.isEmpty() && !nType.getObverse().getEngravers().contains(engraver)) {
                    nType.getObverse().getEngravers().add(engraver);
                    result = ParseEvent.CHANGED;
                }
            }
        }
        return result;
    }

    private static ParseEvent parseObverseDesigners(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        List<String> designersAvers = getTextsSelectedOptions(page.selectFirst("#designer_avers"));
        if (designersAvers != null) {
            for (String designer : designersAvers) {
                if (designer != null && !designer.isEmpty() && !nType.getObverse().getDesigners().contains(designer)) {
                    nType.getObverse().getDesigners().add(designer);
                    result = ParseEvent.CHANGED;
                }
            }
        }
        return result;
    }


    private static ParseEvent parseObverseDescription(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String descriptionAvers = getTagText(page.selectFirst("#description_avers"));
        if (descriptionAvers != null && !descriptionAvers.isEmpty()) {
            nType.getObverse().setDescription(descriptionAvers);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseObverseLettering(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String texteAvers = getTagText(page.selectFirst("#texte_avers"));
        if (texteAvers != null && !texteAvers.isEmpty()) {
            nType.getObverse().setLettering(texteAvers);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseObverseScripts(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        nType.getObverse().getLetteringScripts().clear();

        List<HashMap<String, String>> scriptsAvers = getAttributesWithTextSelectedOptions(page.selectFirst("#script_avers"));
        if (scriptsAvers != null) {
            for (HashMap<String, String> scriptAvers : scriptsAvers) {
                if (isValueAndTextNotNullAndNotEmpty(scriptAvers)) {
                    nType.getObverse().getLetteringScripts().add(N4JUtil.getInstance().numistaService.letteringScriptService.findByNid(scriptAvers.get("value"), scriptAvers.get("text")));
                    result = ParseEvent.CHANGED;
                }
            }
        }
        return result;
    }

    private static ParseEvent parseObverseUnabridgedLegend(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String unabridgedAvers = getTagText(page.selectFirst("#unabridged_avers"));
        if (unabridgedAvers != null && !unabridgedAvers.isEmpty()) {
            nType.getObverse().setUnabridgedLegend(unabridgedAvers);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseObverseLetteringTranslation(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String traductionAvers = getTagText(page.selectFirst("#traduction_avers"));
        if (traductionAvers != null && !traductionAvers.isEmpty()) {
            nType.getObverse().setLetteringTranslation(traductionAvers);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseObversePicture(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        Element obverse = page.selectFirst("fieldset:contains(Obverse)");
        if (obverse != null) {
            String obversePhoto = getAttribute(obverse.selectFirst("a[target=_blank]"), "href");
            if (obversePhoto != null && !obversePhoto.isEmpty()) {
                nType.getObverse().setPicture(obversePhoto);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

}
