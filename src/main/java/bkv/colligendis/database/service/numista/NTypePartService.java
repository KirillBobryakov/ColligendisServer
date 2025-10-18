package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Artist;
import bkv.colligendis.database.entity.numista.LetteringScript;
import bkv.colligendis.database.entity.numista.NTypePart;
import bkv.colligendis.services.AbstractService;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class NTypePartService extends AbstractService<NTypePart, NTypePartRepository> {

    public NTypePartService(NTypePartRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing

    // Engravers
    public List<UUID> getEngravers(UUID nTypePartUuid) {
        return getAllOutgoingRelatedNodesUUIDs(nTypePartUuid, NTypePart.ENGRAVING_WAS_DONE_BY, Artist.LABEL);
    }

    public void detachAllEngravers(UUID nTypePartUuid) {
        detachAllEntityWithRelationshipType(nTypePartUuid, NTypePart.ENGRAVING_WAS_DONE_BY);
    }

    public boolean detachEngraver(UUID nTypePartUuid, UUID engraverUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypePartUuid, engraverUuid, NTypePart.ENGRAVING_WAS_DONE_BY);
        return true;
    }

    public boolean addEngraver(UUID nTypePartUuid, UUID engraverUuid) {
        addSingleOutgoingRelationshipToNode(nTypePartUuid, engraverUuid, NTypePart.ENGRAVING_WAS_DONE_BY);
        return true;
    }

    public boolean equateEngravers(UUID nTypePartUuid, List<UUID> matchingEngraverUuids) {
        return equateFistListToSecondList(getEngravers(nTypePartUuid), matchingEngraverUuids,
                this::detachEngraver,
                this::addEngraver, nTypePartUuid);
    }

    public boolean isEngraversExists(UUID nTypePartUuid) {
        return hasAnyRelationshipWithType(nTypePartUuid, NTypePart.ENGRAVING_WAS_DONE_BY, Artist.LABEL);
    }

    // Designers

    public List<UUID> getDesigners(UUID nTypePartUuid) {
        return getAllOutgoingRelatedNodesUUIDs(nTypePartUuid, NTypePart.DESIGN_WAS_DONE_BY, Artist.LABEL);
    }

    public void detachAllDesigners(UUID nTypePartUuid) {
        detachAllEntityWithRelationshipType(nTypePartUuid, NTypePart.DESIGN_WAS_DONE_BY);
    }

    public boolean detachDesigner(UUID nTypePartUuid, UUID designerUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypePartUuid, designerUuid, NTypePart.DESIGN_WAS_DONE_BY);
        return true;
    }

    public boolean addDesigner(UUID nTypePartUuid, UUID designerUuid) {
        addSingleOutgoingRelationshipToNode(nTypePartUuid, designerUuid, NTypePart.DESIGN_WAS_DONE_BY);
        return true;
    }

    public boolean equateDesigners(UUID nTypePartUuid, List<UUID> matchingDesignerUuids) {
        return equateFistListToSecondList(getDesigners(nTypePartUuid), matchingDesignerUuids, this::detachDesigner,
                this::addDesigner, nTypePartUuid);
    }

    public boolean isDesignersExists(UUID nTypePartUuid) {
        return hasAnyRelationshipWithType(nTypePartUuid, NTypePart.DESIGN_WAS_DONE_BY, Artist.LABEL);
    }

    // Description

    public boolean compareDescription(UUID nTypePartUuid, String description) {
        return comparePropertyValue(nTypePartUuid, "description", description, String.class);
    }

    public boolean setDescription(UUID nTypePartUuid, String description) {
        return setPropertyStringValue(nTypePartUuid, "description", description);
    }

    public void deleteDescription(UUID nTypePartUuid) {
        setPropertyStringValue(nTypePartUuid, "description", null);
    }

    public boolean isDescriptionExists(UUID nTypePartUuid) {
        return isPropertyExists(nTypePartUuid, "description");
    }

    // Lettering
    public boolean compareLettering(UUID nTypePartUuid, String lettering) {
        return comparePropertyValue(nTypePartUuid, "lettering", lettering, String.class);
    }

    public boolean setLettering(UUID nTypePartUuid, String lettering) {
        return setPropertyStringValue(nTypePartUuid, "lettering", lettering);
    }

    public void deleteLettering(UUID nTypePartUuid) {
        setPropertyStringValue(nTypePartUuid, "lettering", null);
    }

    public boolean isLetteringExists(UUID nTypePartUuid) {
        return isPropertyExists(nTypePartUuid, "lettering");
    }

    // Lettering Scripts

    public void detachAllLetteringScripts(UUID nTypePartUuid) {
        detachAllEntityWithRelationshipType(nTypePartUuid, NTypePart.WRITE_ON_SCRIPT);
    }

    public List<UUID> getLetteringScripts(UUID nTypePartUuid) {
        return getAllOutgoingRelatedNodesUUIDs(nTypePartUuid, NTypePart.WRITE_ON_SCRIPT, LetteringScript.LABEL);
    }

    public boolean detachLetteringScript(UUID nTypePartUuid, UUID letteringScriptUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypePartUuid, letteringScriptUuid,
                NTypePart.WRITE_ON_SCRIPT);
        return true;
    }

    public boolean addLetteringScript(UUID nTypePartUuid, UUID letteringScriptUuid) {
        addSingleOutgoingRelationshipToNode(nTypePartUuid, letteringScriptUuid, NTypePart.WRITE_ON_SCRIPT);
        return true;
    }

    public boolean equateLetteringScripts(UUID nTypePartUuid, List<UUID> matchingLetteringScriptUuids) {
        return equateFistListToSecondList(getLetteringScripts(nTypePartUuid), matchingLetteringScriptUuids,
                this::detachLetteringScript, this::addLetteringScript, nTypePartUuid);
    }

    public boolean isLetteringScriptsExists(UUID nTypePartUuid) {
        return hasAnyRelationshipWithType(nTypePartUuid, NTypePart.WRITE_ON_SCRIPT, LetteringScript.LABEL);
    }

    // Unabridged Legend

    public boolean compareUnabridgedLegend(UUID nTypePartUuid, String unabridgedLegend) {
        return comparePropertyValue(nTypePartUuid, "unabridgedLegend", unabridgedLegend, String.class);
    }

    public boolean setUnabridgedLegend(UUID nTypePartUuid, String unabridgedLegend) {
        return setPropertyStringValue(nTypePartUuid, "unabridgedLegend", unabridgedLegend);
    }

    public void deleteUnabridgedLegend(UUID nTypePartUuid) {
        setPropertyStringValue(nTypePartUuid, "unabridgedLegend", null);
    }

    public boolean isUnabridgedLegendExists(UUID nTypePartUuid) {
        return isPropertyExists(nTypePartUuid, "unabridgedLegend");
    }

    // Lettering Translation

    public boolean compareLetteringTranslation(UUID nTypePartUuid, String letteringTranslation) {
        return comparePropertyValue(nTypePartUuid, "letteringTranslation", letteringTranslation, String.class);
    }

    public boolean setLetteringTranslation(UUID nTypePartUuid, String letteringTranslation) {
        return setPropertyStringValue(nTypePartUuid, "letteringTranslation", letteringTranslation);
    }

    public void deleteLetteringTranslation(UUID nTypePartUuid) {
        setPropertyStringValue(nTypePartUuid, "letteringTranslation", null);
    }

    public boolean isLetteringTranslationExists(UUID nTypePartUuid) {
        return isPropertyExists(nTypePartUuid, "letteringTranslation");
    }

    // Lettering Translation (Russian)

    public boolean compareLetteringTranslationRu(UUID nTypePartUuid, String letteringTranslationRu) {
        return comparePropertyValue(nTypePartUuid, "letteringTranslationRu", letteringTranslationRu, String.class);
    }

    public boolean setLetteringTranslationRu(UUID nTypePartUuid, String letteringTranslationRu) {
        return setPropertyStringValue(nTypePartUuid, "letteringTranslationRu", letteringTranslationRu);
    }

    public boolean isLetteringTranslationRuExists(UUID nTypePartUuid) {
        return isPropertyExists(nTypePartUuid, "letteringTranslationRu");
    }

    // Picture

    public boolean comparePicture(UUID nTypePartUuid, String picture) {
        return comparePropertyValue(nTypePartUuid, "picture", picture, String.class);
    }

    public boolean setPicture(UUID nTypePartUuid, String picture) {
        return setPropertyStringValue(nTypePartUuid, "picture", picture);
    }

    public void deletePicture(UUID nTypePartUuid) {
        setPropertyStringValue(nTypePartUuid, "picture", null);
    }

    public boolean isPictureExists(UUID nTypePartUuid) {
        return isPropertyExists(nTypePartUuid, "picture");
    }

    // End: Methods for Numista parsing

}
