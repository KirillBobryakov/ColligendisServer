package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.NTypePart;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;

public class EdgeParser extends NumistaPartParser {

    public EdgeParser() {
        super((page, nType) -> {
            ParseEvent result = ParseEvent.NOT_CHANGED;

            ParseEvent[] parseFunctions = new ParseEvent[] {
                    parseEdgeDescription(page, nType),
                    parseEdgeLettering(page, nType),
                    parseEdgeScripts(page, nType),
                    parseEdgeUnabridgedLegend(page, nType),
                    parseEdgeLetteringTranslation(page, nType),
                    parseEdgePicture(page, nType),
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

        this.partName = "EdgeParser";
    }

    private static ParseEvent parseEdgeDescription(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        String descriptionTranche = getTagText(page.selectFirst("#description_tranche"));
        if (descriptionTranche != null && !descriptionTranche.isEmpty()) {
            if(nType.getEdge() == null) nType.setEdge(new NTypePart(PART_TYPE.EDGE));
            nType.getEdge().setDescription(descriptionTranche);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseEdgeLettering(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        String texteTranche = getTagText(page.selectFirst("texte_tranche"));
        if (texteTranche != null && !texteTranche.isEmpty()) {
            if(nType.getEdge() == null) nType.setEdge(new NTypePart(PART_TYPE.EDGE));

            nType.getEdge().setLettering(texteTranche);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseEdgeScripts(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        //todo Change code on without clearing Lettering scripts.
        if(nType.getEdge() != null && nType.getEdge().getLetteringScripts() != null && !nType.getEdge().getLetteringScripts().isEmpty()) {
            nType.getEdge().getLetteringScripts().clear();
        }


        List<HashMap<String, String>> scriptsTranche = getAttributesWithTextSelectedOptions(page.selectFirst("#script_tranche"));
        if (scriptsTranche != null) {
            for (HashMap<String, String> scriptTranche : scriptsTranche) {
                if (isValueAndTextNotNullAndNotEmpty(scriptTranche)) {
                    if(nType.getEdge() == null) nType.setEdge(new NTypePart(PART_TYPE.EDGE));

                    assert nType.getEdge().getLetteringScripts() != null;

                    nType.getEdge().getLetteringScripts().add(N4JUtil.getInstance().numistaService.letteringScriptService.findByNid(scriptTranche.get("value"), scriptTranche.get("text")));
                    result = ParseEvent.CHANGED;
                }
            }
        }

        return result;
    }

    private static ParseEvent parseEdgeUnabridgedLegend(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String unabridgedTranche = getTagText(page.selectFirst("#unabridged_tranche"));
        if (unabridgedTranche != null && !unabridgedTranche.isEmpty()) {
            if(nType.getEdge() == null) nType.setEdge(new NTypePart(PART_TYPE.EDGE));

            nType.getEdge().setUnabridgedLegend(unabridgedTranche);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseEdgeLetteringTranslation(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String traductionTranche = getTagText(page.selectFirst("#traduction_tranche"));
        if (traductionTranche != null && !traductionTranche.isEmpty()) {
            if(nType.getEdge() == null) nType.setEdge(new NTypePart(PART_TYPE.EDGE));

            nType.getEdge().setLetteringTranslation(traductionTranche);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseEdgePicture(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        Element legendEdge = page.selectFirst("fieldset>legend:containsOwn(Edge)");
        if (legendEdge != null) {
            String edgePhoto = getAttribute(legendEdge.parent().selectFirst("a[target=_blank]"), "href");
            if (edgePhoto != null && !edgePhoto.isEmpty()) {
                if(nType.getEdge() == null) nType.setEdge(new NTypePart(PART_TYPE.EDGE));

                nType.getEdge().setPicture(edgePhoto);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }


}
