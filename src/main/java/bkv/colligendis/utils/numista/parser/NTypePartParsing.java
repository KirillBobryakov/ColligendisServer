package bkv.colligendis.utils.numista.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import bkv.colligendis.database.entity.numista.NTypePart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NTypePartParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(NTypePartParsing.class);

    public NTypePartParsing(PART_TYPE partType) {
        super((pageParser) -> {

            ParsingResult result = ParsingResult.NOT_CHANGED;

            // UUID nTypePartUuid = nTypeService.getNTypePartUuid(pageParser.getNTypeUuid(),
            // partType);

            // if (nTypePartUuid == null) {
            // nTypePartUuid = nTypePartService.save(new NTypePart(partType)).getUuid();
            // nTypeService.setNTypePart(pageParser.getNTypeUuid(), nTypePartUuid,
            // partType);
            // }

            result = parseEngravers(pageParser.getNumistaPage(), pageParser.getNTypeUuid(), partType);
            result = parseDesigners(pageParser.getNumistaPage(), pageParser.getNTypeUuid(), partType);
            result = parseDescription(pageParser.getNumistaPage(), pageParser.getNTypeUuid(), partType);
            result = parseLettering(pageParser.getNumistaPage(), pageParser.getNTypeUuid(), partType);
            result = parseScripts(pageParser.getNumistaPage(), pageParser.getNTypeUuid(), partType);
            result = parseUnabridgedLegend(pageParser.getNumistaPage(), pageParser.getNTypeUuid(), partType);
            result = parseLetteringTranslation(pageParser.getNumistaPage(), pageParser.getNTypeUuid(), partType);
            result = parsePicture(pageParser.getNumistaPage(), pageParser.getNTypeUuid(), partType);

            return result;
        });

        this.partName = "ObverseParsing";
    }

    private static UUID getNTypePartUuidWithCreating(UUID nTypeUuid, PART_TYPE partType) {
        UUID nTypePartUuid = nTypeService.getNTypePartUuid(nTypeUuid, partType);

        if (nTypePartUuid == null) {
            nTypePartUuid = nTypePartService.save(new NTypePart(partType)).getUuid();
            nTypeService.setNTypePart(nTypeUuid, nTypePartUuid, partType);
        }
        return nTypePartUuid;
    }

    private static ParsingResult parseEngravers(Document page, UUID nTypeUuid, PART_TYPE partType) {
        ParsingResult result = ParsingResult.NOT_CHANGED;
        String engraversTag = null;
        switch (partType) {
            case OBVERSE:
                engraversTag = "#graveur_avers";
                break;
            case REVERSE:
                engraversTag = "#graveur_revers";
                break;
            case EDGE, WATERMARK:
                return result;
            default:
                throw new IllegalArgumentException("Invalid part type: " + partType);
        }

        List<String> engravers = PartParser.getTextsSelectedOptions(page.selectFirst(engraversTag));
        if (engravers == null || engravers.isEmpty()) {
            UUID nTypePartUuid = nTypeService.getNTypePartUuid(nTypeUuid, partType);
            if (nTypePartUuid == null) {
                return result;
            }
            if (nTypePartService.isEngraversExists(nTypePartUuid)) {
                nTypePartService.detachAllEngravers(nTypePartUuid);
                logger.warn("Detached all engravers for NTypePart: " + nTypePartUuid);
                result = ParsingResult.CHANGED;
            }

            return result;
        }
        UUID nTypePartUuid = getNTypePartUuidWithCreating(nTypeUuid, partType);

        if (nTypePartService.equateEngravers(nTypePartUuid, engravers.stream()
                .map(engraver -> {
                    UUID artistUuid = artistService.findUuidByName(engraver);
                    if (artistUuid == null) {
                        logger.error("Engraver (Artist) not found: " + engraver);
                    }
                    return artistUuid;
                }).filter(Objects::nonNull)
                .collect(Collectors.toList()))) {
            result = ParsingResult.CHANGED;
        }
        return result;
    }

    private static ParsingResult parseDesigners(Document page, UUID nTypeUuid, PART_TYPE partType) {
        ParsingResult result = ParsingResult.NOT_CHANGED;

        String designersTag = null;
        switch (partType) {
            case OBVERSE:
                designersTag = "#designer_avers";
                break;
            case REVERSE:
                designersTag = "#designer_revers";
                break;
            case EDGE, WATERMARK:
                return result;
            default:
                throw new IllegalArgumentException("Invalid part type: " + partType);
        }
        List<String> designers = PartParser.getTextsSelectedOptions(page.selectFirst(designersTag));
        if (designers == null || designers.isEmpty()) {
            UUID nTypePartUuid = nTypeService.getNTypePartUuid(nTypeUuid, partType);
            if (nTypePartUuid == null) {
                return result;
            }
            if (nTypePartService.isDesignersExists(nTypePartUuid)) {
                nTypePartService.detachAllDesigners(nTypePartUuid);
                logger.warn("Detached all designers for NTypePart: " + nTypePartUuid);
                result = ParsingResult.CHANGED;
            }

            return result;
        }

        UUID nTypePartUuid = getNTypePartUuidWithCreating(nTypeUuid, partType);

        if (nTypePartService.equateDesigners(nTypePartUuid, designers.stream()
                .map(designer -> {
                    UUID artistUuid = artistService.findUuidByName(designer);
                    if (artistUuid == null) {
                        logger.error("Designer (Artist) not found: " + designer);
                    }
                    return artistUuid;
                }).filter(Objects::nonNull)
                .collect(Collectors.toList()))) {
            result = ParsingResult.CHANGED;
        }
        return result;
    }

    private static ParsingResult parseDescription(Document page, UUID nTypeUuid, PART_TYPE partType) {
        ParsingResult result = ParsingResult.NOT_CHANGED;
        String descriptionTag = null;
        switch (partType) {
            case OBVERSE:
                descriptionTag = "#description_avers";
                break;
            case REVERSE:
                descriptionTag = "#description_revers";
                break;
            case EDGE:
                descriptionTag = "#description_tranche";
                break;
            case WATERMARK:
                descriptionTag = "#description_watermark";
                break;
            default:
                throw new IllegalArgumentException("Invalid part type: " + partType);
        }

        String description = PartParser.getTagText(page.selectFirst(descriptionTag));

        if (description == null || description.isEmpty()) {
            UUID nTypePartUuid = nTypeService.getNTypePartUuid(nTypeUuid, partType);
            if (nTypePartUuid == null) {
                return result;
            }
            if (nTypePartService.isDescriptionExists(nTypePartUuid)) {
                nTypePartService.deleteDescription(nTypePartUuid);
                logger.warn("Deleted description for NTypePart: " + nTypePartUuid);
                result = ParsingResult.CHANGED;
            }
            return result;
        }
        UUID nTypePartUuid = getNTypePartUuidWithCreating(nTypeUuid, partType);

        if (!nTypePartService.compareDescription(nTypePartUuid, description)) {
            nTypePartService.setDescription(nTypePartUuid, description);
            result = ParsingResult.CHANGED;
        }
        return result;
    }

    private static ParsingResult parseLettering(Document page, UUID nTypeUuid, PART_TYPE partType) {
        ParsingResult result = ParsingResult.NOT_CHANGED;

        String letteringTag = null;
        switch (partType) {
            case OBVERSE:
                letteringTag = "#texte_avers";
                break;
            case REVERSE:
                letteringTag = "#texte_revers";
                break;
            case EDGE:
                letteringTag = "#texte_tranche";
                break;
            case WATERMARK:
                return result;
            default:
                throw new IllegalArgumentException("Invalid part type: " + partType);
        }
        String lettering = PartParser.getTagText(page.selectFirst(letteringTag));

        if (lettering == null || lettering.isEmpty()) {
            UUID nTypePartUuid = nTypeService.getNTypePartUuid(nTypeUuid, partType);
            if (nTypePartUuid == null) {
                return result;
            }
            if (nTypePartService.isLetteringExists(nTypePartUuid)) {
                nTypePartService.deleteLettering(nTypePartUuid);
                logger.warn("Deleted lettering for NTypePart: " + nTypePartUuid);
                result = ParsingResult.CHANGED;
            }
            return result;
        }
        UUID nTypePartUuid = getNTypePartUuidWithCreating(nTypeUuid, partType);

        if (!nTypePartService.compareLettering(nTypePartUuid, lettering)) {
            nTypePartService.setLettering(nTypePartUuid, lettering);
            result = ParsingResult.CHANGED;
        }
        return result;
    }

    private static ParsingResult parseScripts(Document page, UUID nTypeUuid, PART_TYPE partType) {
        ParsingResult result = ParsingResult.NOT_CHANGED;
        String scriptsTag = null;
        switch (partType) {
            case OBVERSE:
                scriptsTag = "#script_avers";
                break;
            case REVERSE:
                scriptsTag = "#script_revers";
                break;
            case EDGE:
                scriptsTag = "#script_tranche";
                break;
            case WATERMARK:
                return result;
            default:
                throw new IllegalArgumentException("Invalid part type: " + partType);
        }

        List<HashMap<String, String>> scripts = PartParser.getAttributesWithTextSelectedOptions(
                page.selectFirst(scriptsTag));

        if (scripts == null || scripts.isEmpty()) {
            UUID nTypePartUuid = nTypeService.getNTypePartUuid(nTypeUuid, partType);
            if (nTypePartUuid == null) {
                return result;
            }
            if (nTypePartService.isLetteringScriptsExists(nTypePartUuid)) {
                nTypePartService.detachAllLetteringScripts(nTypePartUuid);
                logger.warn("Detached all lettering scripts for NTypePart: " + nTypePartUuid);
                result = ParsingResult.CHANGED;
            }
            return result;
        }
        UUID nTypePartUuid = getNTypePartUuidWithCreating(nTypeUuid, partType);

        nTypePartService.equateLetteringScripts(nTypePartUuid,
                scripts.stream().map(script -> {
                    UUID letteringScriptUuid = letteringScriptService.findUuidByNid(script.get("value"));
                    if (letteringScriptUuid == null) {
                        logger.error("Lettering script not found: " + script.get("value"));
                    }
                    return letteringScriptUuid;
                }).filter(Objects::nonNull)
                        .filter(Objects::nonNull).collect(Collectors.toList()));
        result = ParsingResult.CHANGED;
        return result;
    }

    private static ParsingResult parseUnabridgedLegend(Document page, UUID nTypeUuid, PART_TYPE partType) {
        ParsingResult result = ParsingResult.NOT_CHANGED;

        String unabridgedTag = null;
        switch (partType) {
            case OBVERSE:
                unabridgedTag = "#unabridged_avers";
                break;
            case REVERSE:
                unabridgedTag = "#unabridged_revers";
                break;
            case EDGE:
                unabridgedTag = "#unabridged_tranche";
                break;
            case WATERMARK:
                return result;
            default:
                throw new IllegalArgumentException("Invalid part type: " + partType);
        }

        String unabridged = PartParser.getTagText(page.selectFirst(unabridgedTag));

        if (unabridged == null || unabridged.isEmpty()) {
            UUID nTypePartUuid = nTypeService.getNTypePartUuid(nTypeUuid, partType);
            if (nTypePartUuid == null) {
                return result;
            }
            if (nTypePartService.isUnabridgedLegendExists(nTypePartUuid)) {
                nTypePartService.deleteUnabridgedLegend(nTypePartUuid);
                logger.warn("Deleted unabridged legend for NTypePart: " + nTypePartUuid);
                result = ParsingResult.CHANGED;
            }
            return result;
        }
        UUID nTypePartUuid = getNTypePartUuidWithCreating(nTypeUuid, partType);

        if (!nTypePartService.compareUnabridgedLegend(nTypePartUuid, unabridged)) {
            nTypePartService.setUnabridgedLegend(nTypePartUuid, unabridged);
            result = ParsingResult.CHANGED;
        }

        return result;
    }

    private static ParsingResult parseLetteringTranslation(Document page, UUID nTypeUuid, PART_TYPE partType) {
        ParsingResult result = ParsingResult.NOT_CHANGED;
        String traductionTag = null;
        switch (partType) {
            case OBVERSE:
                traductionTag = "#traduction_avers";
                break;
            case REVERSE:
                traductionTag = "#traduction_revers";
                break;
            case EDGE:
                traductionTag = "#traduction_tranche";
                break;
            case WATERMARK:
                return result;
            default:
                throw new IllegalArgumentException("Invalid part type: " + partType);
        }

        String traduction = PartParser.getTagText(page.selectFirst(traductionTag));

        if (traduction == null || traduction.isEmpty()) {
            UUID nTypePartUuid = nTypeService.getNTypePartUuid(nTypeUuid, partType);
            if (nTypePartUuid == null) {
                return result;
            }
            if (nTypePartService.isLetteringTranslationExists(nTypePartUuid)) {
                nTypePartService.deleteLetteringTranslation(nTypePartUuid);
                logger.warn("Deleted lettering translation for NTypePart: " + nTypePartUuid);
                result = ParsingResult.CHANGED;
            }
            return result;
        }
        UUID nTypePartUuid = getNTypePartUuidWithCreating(nTypeUuid, partType);

        if (!nTypePartService.compareLetteringTranslation(nTypePartUuid, traduction)) {
            nTypePartService.setLetteringTranslation(nTypePartUuid, traduction);
            result = ParsingResult.CHANGED;
        }
        return result;
    }

    private static ParsingResult parsePicture(Document page, UUID nTypeUuid, PART_TYPE partType) {
        ParsingResult result = ParsingResult.NOT_CHANGED;
        String pictureTag = null;
        switch (partType) {
            case OBVERSE:
                pictureTag = "fieldset:contains(Obverse)";
                break;
            case REVERSE:
                pictureTag = "fieldset:contains(Reverse (back))";
                break;
            case EDGE:
                pictureTag = "fieldset>legend:containsOwn(Edge)";
                break;
            case WATERMARK:
                pictureTag = "fieldset:contains(Watermark)";
                break;
            default:
                throw new IllegalArgumentException("Invalid part type: " + partType);
        }

        Element pictureElement = page.selectFirst(pictureTag);
        if (pictureElement == null) {
            return result;
        }
        String obversePhoto = PartParser.getAttribute(pictureElement.selectFirst("a[target=_blank]"),
                "href");

        if (obversePhoto == null || obversePhoto.isEmpty()) {
            UUID nTypePartUuid = nTypeService.getNTypePartUuid(nTypeUuid, partType);
            if (nTypePartUuid == null) {
                return result;
            }
            if (nTypePartService.isPictureExists(nTypePartUuid)) {
                nTypePartService.deletePicture(nTypePartUuid);
                logger.warn("Deleted picture for NTypePart: " + nTypePartUuid);
                result = ParsingResult.CHANGED;
            }
            return result;
        }

        UUID nTypePartUuid = getNTypePartUuidWithCreating(nTypeUuid, partType);
        if (!nTypePartService.comparePicture(nTypePartUuid, obversePhoto)) {
            nTypePartService.setPicture(nTypePartUuid, obversePhoto);
            result = ParsingResult.CHANGED;
        }
        return result;
    }

}
