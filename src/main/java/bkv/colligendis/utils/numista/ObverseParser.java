package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.NTypePart;
import bkv.colligendis.utils.N4JUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
                if (engraver != null && !engraver.isEmpty()) {
                    if (nType.getObverse() == null) {
                        nType.setObverse(new NTypePart(PART_TYPE.OBVERSE));
                    } else if (nType.getObverse().getEngravers().contains(engraver)) {
                        continue;
                    }

                    assert nType.getObverse().getEngravers() != null;

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

    private static ParseEvent parseObverseDescription(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String descriptionAvers = getTagText(page.selectFirst("#description_avers"));
        if (descriptionAvers != null && !descriptionAvers.isEmpty()) {
            if (nType.getObverse() == null)
                nType.setObverse(new NTypePart(PART_TYPE.OBVERSE));

            if (nType.getObverse().getDescription() == null
                    || !nType.getObverse().getDescription().equals(descriptionAvers)) {
                nType.getObverse().setDescription(descriptionAvers);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

    private static ParseEvent parseObverseLettering(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String texteAvers = getTagText(page.selectFirst("#texte_avers"));
        if (texteAvers != null && !texteAvers.isEmpty()) {
            if (nType.getObverse() == null)
                nType.setObverse(new NTypePart(PART_TYPE.OBVERSE));

            if (nType.getObverse().getLettering() == null
                    || !nType.getObverse().getLettering().equals(texteAvers)) {
                nType.getObverse().setLettering(texteAvers);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

    private static ParseEvent parseObverseScripts(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        List<HashMap<String, String>> scriptsAvers = getAttributesWithTextSelectedOptions(
                page.selectFirst("#script_avers"));
        if (scriptsAvers != null) {

            // Check if both lists have the same elements
            boolean listsAreEqual = true;

            if (nType.getObverse() == null) {
                nType.setObverse(new NTypePart(PART_TYPE.OBVERSE));
                listsAreEqual = false;
            } else if (nType.getObverse().getLetteringScripts().size() != scriptsAvers.size()) {
                listsAreEqual = false;
            } else {
                for (HashMap<String, String> script : scriptsAvers) {
                    if (isValueAndTextNotNullAndNotEmpty(script)) {
                        boolean found = nType.getObverse().getLetteringScripts().stream()
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
                nType.getObverse().getLetteringScripts().clear();
                for (HashMap<String, String> script : scriptsAvers) {
                    if (isValueAndTextNotNullAndNotEmpty(script)) {
                        nType.getObverse().getLetteringScripts()
                                .add(N4JUtil.getInstance().numistaService.letteringScriptService
                                        .findByNid(script.get("value"), script.get("text")));
                    }
                }
                result = ParseEvent.CHANGED;
            }
        }

        return result;
    }

    private static ParseEvent parseObverseUnabridgedLegend(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String unabridgedAvers = getTagText(page.selectFirst("#unabridged_avers"));
        if (unabridgedAvers != null && !unabridgedAvers.isEmpty()) {
            if (nType.getObverse() == null)
                nType.setObverse(new NTypePart(PART_TYPE.OBVERSE));

            if (nType.getObverse().getUnabridgedLegend() == null
                    || !nType.getObverse().getUnabridgedLegend().equals(unabridgedAvers)) {
                nType.getObverse().setUnabridgedLegend(unabridgedAvers);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

    private static ParseEvent parseObverseLetteringTranslation(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String traductionAvers = getTagText(page.selectFirst("#traduction_avers"));
        if (traductionAvers != null && !traductionAvers.isEmpty()) {
            if (nType.getObverse() == null)
                nType.setObverse(new NTypePart(PART_TYPE.OBVERSE));

            if (nType.getObverse().getLetteringTranslation() == null
                    || !nType.getObverse().getLetteringTranslation().equals(traductionAvers)) {
                nType.getObverse().setLetteringTranslation(traductionAvers);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

    private static ParseEvent parseObversePicture(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        Element obverse = page.selectFirst("fieldset:contains(Obverse)");
        if (obverse != null) {
            String obversePhoto = getAttribute(obverse.selectFirst("a[target=_blank]"), "href");
            if (obversePhoto != null && !obversePhoto.isEmpty()) {
                if (nType.getObverse() == null)
                    nType.setObverse(new NTypePart(PART_TYPE.OBVERSE));

                if (nType.getObverse().getPicture() == null
                        || !nType.getObverse().getPicture().equals(obversePhoto)) {
                    nType.getObverse().setPicture(obversePhoto);
                    result = ParseEvent.CHANGED;
                }
            }
        }
        return result;
    }

}
