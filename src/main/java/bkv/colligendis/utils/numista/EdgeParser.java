package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.NTypePart;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;

public class ReverseParser extends NumistaPartParser {

    public ReverseParser() {
        super((page, nType) -> {
            ParseEvent result = ParseEvent.NOT_CHANGED;

            ParseEvent[] parseFunctions = new ParseEvent[] {
                    parseReverseEngravers(page, nType),
                    parseReverseDesigners(page, nType),
                    parseReverseDescription(page, nType),
                    parseReverseLettering(page, nType),
                    parseReverseScripts(page, nType),
                    parseReverseUnabridgedLegend(page, nType),
                    parseReverseLetteringTranslation(page, nType),
                    parseReversePicture(page, nType),
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

        this.partName = "ReverseParser";
    }

    private static ParseEvent parseReverseEngravers(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        List<String> graveursRevers = getTextsSelectedOptions(page.selectFirst("#graveur_revers"));
        if (graveursRevers != null) {
            for (String engraver : graveursRevers) {
                if (engraver != null && !engraver.isEmpty()) {

                    if (nType.getReverse() == null) {
                        nType.setReverse(new NTypePart());
                    } else if (nType.getReverse().getEngravers().contains(engraver)) {
                        continue;
                    }

                    assert nType.getReverse().getEngravers() != null;

                    nType.getReverse().getEngravers().add(engraver);
                    result = ParseEvent.CHANGED;
                }
            }
        }
        return result;
    }

    private static ParseEvent parseReverseDesigners(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        List<String> designersRevers = getTextsSelectedOptions(page.selectFirst("#designer_revers"));
        if (designersRevers != null) {
            for (String designer : designersRevers) {
                if (designer != null && !designer.isEmpty()) {

                    if (nType.getReverse() == null) {
                        nType.setReverse(new NTypePart());
                    } else if (nType.getObverse().getDesigners().contains(designer)) {
                        continue;
                    }

                    assert nType.getReverse().getDesigners() != null;

                    nType.getReverse().getDesigners().add(designer);
                    result = ParseEvent.CHANGED;
                }
            }
        }
        return result;
    }


    private static ParseEvent parseReverseDescription(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        String descriptionRevers = getTagText(page.selectFirst("#description_revers"));
        if (descriptionRevers != null && !descriptionRevers.isEmpty()) {
            if (nType.getReverse() == null) nType.setReverse(new NTypePart());

            nType.getReverse().setDescription(descriptionRevers);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseReverseLettering(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        String texteRevers = getTagText(page.selectFirst("#texte_revers"));
        if (texteRevers != null && !texteRevers.isEmpty()) {
            if (nType.getReverse() == null) nType.setReverse(new NTypePart());

            nType.getReverse().setLettering(texteRevers);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseReverseScripts(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;


        //todo Change code on without clearing Lettering scripts.
        if(nType.getReverse() != null && nType.getReverse().getLetteringScripts() != null && !nType.getReverse().getLetteringScripts().isEmpty()) {
            nType.getReverse().getLetteringScripts().clear();
        }

        List<HashMap<String, String>> scriptsRevers = getAttributesWithTextSelectedOptions(page.selectFirst("#script_revers"));
        if (scriptsRevers != null) {
            for (HashMap<String, String> scriptRevers : scriptsRevers) {
                if (isValueAndTextNotNullAndNotEmpty(scriptRevers)) {
                    if (nType.getReverse() == null) nType.setReverse(new NTypePart());

                    assert nType.getReverse().getLetteringScripts() != null;

                    nType.getReverse().getLetteringScripts().add(N4JUtil.getInstance().numistaService.letteringScriptService.findByNid(scriptRevers.get("value"), scriptRevers.get("text")));
                    result = ParseEvent.CHANGED;
                }
            }
        }
        return result;
    }

    private static ParseEvent parseReverseUnabridgedLegend(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String unabridgedRevers = getTagText(page.selectFirst("#unabridged_revers"));
        if (unabridgedRevers != null && !unabridgedRevers.isEmpty()) {
            if (nType.getReverse() == null) nType.setReverse(new NTypePart());

            nType.getReverse().setUnabridgedLegend(unabridgedRevers);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseReverseLetteringTranslation(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        String traductionRevers = getTagText(page.selectFirst("#traduction_revers"));
        if (traductionRevers != null && !traductionRevers.isEmpty()) {
            if (nType.getReverse() == null) nType.setReverse(new NTypePart());

            nType.getReverse().setLetteringTranslation(traductionRevers);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseReversePicture(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        Element reverse = page.selectFirst("fieldset:contains(Reverse (back))");
        if (reverse != null) {
            String reversePhoto = getAttribute(reverse.selectFirst("a[target=_blank]"), "href");
            if (reversePhoto != null && !reversePhoto.isEmpty()) {
                if (nType.getReverse() == null) nType.setReverse(new NTypePart());

                nType.getReverse().setPicture(reversePhoto);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }


}
