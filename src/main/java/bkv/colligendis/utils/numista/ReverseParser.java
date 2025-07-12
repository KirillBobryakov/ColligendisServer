package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.NTypePart;
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
                        nType.setReverse(new NTypePart(PART_TYPE.REVERSE));
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

                    if (nType.getObverse() == null) {
                        nType.setObverse(new NTypePart(PART_TYPE.OBVERSE));
                    } else if (nType.getObverse().getDesigners().contains(designer)) {
                        continue;
                    }

                    assert nType.getObverse().getDesigners() != null;

                    nType.getObverse().getDesigners().add(designer);
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
            if (nType.getReverse() == null)
                nType.setReverse(new NTypePart(PART_TYPE.REVERSE));

            if (nType.getReverse().getDescription() == null
                    || !nType.getReverse().getDescription().equals(descriptionRevers)) {
                nType.getReverse().setDescription(descriptionRevers);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

    private static ParseEvent parseReverseLettering(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        String texteRevers = getTagText(page.selectFirst("#texte_revers"));
        if (texteRevers != null && !texteRevers.isEmpty()) {
            if (nType.getReverse() == null)
                nType.setReverse(new NTypePart(PART_TYPE.REVERSE));

            if (nType.getReverse().getLettering() == null
                    || !nType.getReverse().getLettering().equals(texteRevers)) {
                nType.getReverse().setLettering(texteRevers);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

    private static ParseEvent parseReverseScripts(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        List<HashMap<String, String>> scriptsRevers = getAttributesWithTextSelectedOptions(
                page.selectFirst("#script_revers"));
        if (scriptsRevers != null) {

            // Check if both lists have the same elements
            boolean listsAreEqual = true;

            if (nType.getReverse() == null) {
                nType.setReverse(new NTypePart(PART_TYPE.REVERSE));
                listsAreEqual = false;
            } else if (nType.getReverse().getLetteringScripts().size() != scriptsRevers.size()) {
                listsAreEqual = false;
            } else {
                for (HashMap<String, String> script : scriptsRevers) {
                    if (isValueAndTextNotNullAndNotEmpty(script)) {
                        boolean found = nType.getReverse().getLetteringScripts().stream()
                                .anyMatch(t -> t.getNid().equals(script.get("value")));
                        if (!found) {
                            listsAreEqual = false;
                            break;
                        }
                    }
                }
            }

            // If lists are not equal, update nType.getTechniques() with parsed techniques
            if (!listsAreEqual) {
                nType.getReverse().getLetteringScripts().clear();
                for (HashMap<String, String> script : scriptsRevers) {
                    if (isValueAndTextNotNullAndNotEmpty(script)) {
                        nType.getReverse().getLetteringScripts()
                                .add(N4JUtil.getInstance().numistaService.letteringScriptService
                                        .findByNid(script.get("value"), script.get("text")));
                    }
                }
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

    private static ParseEvent parseReverseUnabridgedLegend(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String unabridgedRevers = getTagText(page.selectFirst("#unabridged_revers"));
        if (unabridgedRevers != null && !unabridgedRevers.isEmpty()) {
            if (nType.getReverse() == null)
                nType.setReverse(new NTypePart(PART_TYPE.REVERSE));

            if (nType.getReverse().getUnabridgedLegend() == null
                    || !nType.getReverse().getUnabridgedLegend().equals(unabridgedRevers)) {
                nType.getReverse().setUnabridgedLegend(unabridgedRevers);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

    private static ParseEvent parseReverseLetteringTranslation(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        String traductionRevers = getTagText(page.selectFirst("#traduction_revers"));
        if (traductionRevers != null && !traductionRevers.isEmpty()) {
            if (nType.getReverse() == null)
                nType.setReverse(new NTypePart(PART_TYPE.REVERSE));

            if (nType.getReverse().getLetteringTranslation() == null
                    || !nType.getReverse().getLetteringTranslation().equals(traductionRevers)) {
                nType.getReverse().setLetteringTranslation(traductionRevers);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

    private static ParseEvent parseReversePicture(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        Element reverse = page.selectFirst("fieldset:contains(Reverse (back))");
        if (reverse != null) {
            String reversePhoto = getAttribute(reverse.selectFirst("a[target=_blank]"), "href");
            if (reversePhoto != null && !reversePhoto.isEmpty()) {
                if (nType.getReverse() == null)
                    nType.setReverse(new NTypePart(PART_TYPE.REVERSE));

                if (nType.getReverse().getPicture() == null
                        || !nType.getReverse().getPicture().equals(reversePhoto)) {
                    nType.getReverse().setPicture(reversePhoto);
                    result = ParseEvent.CHANGED;
                }
            }
        }
        return result;
    }

}
