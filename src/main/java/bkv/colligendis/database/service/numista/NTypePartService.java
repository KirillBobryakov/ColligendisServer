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

    public List<UUID> getEngravers(UUID nTypePartUuid) {
        return getAllOutgoingRelatedNodesUUIDs(nTypePartUuid, NTypePart.ENGRAVING_WAS_DONE_BY, Artist.LABEL);
    }

    public boolean detachEngraver(UUID nTypePartUuid, UUID engraverUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypePartUuid, engraverUuid, NTypePart.ENGRAVING_WAS_DONE_BY);
        return true;
    }

    public boolean addEngraver(UUID nTypePartUuid, UUID engraverUuid) {
        addSingleOutgoingRelationshipToNode(nTypePartUuid, engraverUuid, NTypePart.ENGRAVING_WAS_DONE_BY);
        return true;
    }

    public void equateEngravers(UUID nTypePartUuid, List<UUID> matchingEngraverUuids) {
        equateFistListToSecondList(getEngravers(nTypePartUuid), matchingEngraverUuids, this::detachEngraver,
                this::addEngraver, nTypePartUuid);
    }

    public List<UUID> getDesigners(UUID nTypePartUuid) {
        return getAllOutgoingRelatedNodesUUIDs(nTypePartUuid, NTypePart.DESIGN_WAS_DONE_BY, Artist.LABEL);
    }

    public boolean detachDesigner(UUID nTypePartUuid, UUID designerUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypePartUuid, designerUuid, NTypePart.DESIGN_WAS_DONE_BY);
        return true;
    }

    public boolean addDesigner(UUID nTypePartUuid, UUID designerUuid) {
        addSingleOutgoingRelationshipToNode(nTypePartUuid, designerUuid, NTypePart.DESIGN_WAS_DONE_BY);
        return true;
    }

    public void equateDesigners(UUID nTypePartUuid, List<UUID> matchingDesignerUuids) {
        equateFistListToSecondList(getDesigners(nTypePartUuid), matchingDesignerUuids, this::detachDesigner,
                this::addDesigner, nTypePartUuid);
    }

    public boolean compareDescription(UUID nTypePartUuid, String description) {
        return comparePropertyValue(nTypePartUuid, "description", description, String.class);
    }

    public boolean setDescription(UUID nTypePartUuid, String description) {
        return setPropertyStringValue(nTypePartUuid, "description", description);
    }

    public boolean compareLettering(UUID nTypePartUuid, String lettering) {
        return comparePropertyValue(nTypePartUuid, "lettering", lettering, String.class);
    }

    public boolean setLettering(UUID nTypePartUuid, String lettering) {
        return setPropertyStringValue(nTypePartUuid, "lettering", lettering);
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

    public void equateLetteringScripts(UUID nTypePartUuid, List<UUID> matchingLetteringScriptUuids) {
        equateFistListToSecondList(getLetteringScripts(nTypePartUuid), matchingLetteringScriptUuids,
                this::detachLetteringScript, this::addLetteringScript, nTypePartUuid);
    }

    public boolean compareUnabridgedLegend(UUID nTypePartUuid, String unabridgedLegend) {
        return comparePropertyValue(nTypePartUuid, "unabridgedLegend", unabridgedLegend, String.class);
    }

    public boolean setUnabridgedLegend(UUID nTypePartUuid, String unabridgedLegend) {
        return setPropertyStringValue(nTypePartUuid, "unabridgedLegend", unabridgedLegend);
    }

    public boolean compareLetteringTranslation(UUID nTypePartUuid, String letteringTranslation) {
        return comparePropertyValue(nTypePartUuid, "letteringTranslation", letteringTranslation, String.class);
    }

    public boolean setLetteringTranslation(UUID nTypePartUuid, String letteringTranslation) {
        return setPropertyStringValue(nTypePartUuid, "letteringTranslation", letteringTranslation);
    }

    public boolean compareLetteringTranslationRu(UUID nTypePartUuid, String letteringTranslationRu) {
        return comparePropertyValue(nTypePartUuid, "letteringTranslationRu", letteringTranslationRu, String.class);
    }

    public boolean setLetteringTranslationRu(UUID nTypePartUuid, String letteringTranslationRu) {
        return setPropertyStringValue(nTypePartUuid, "letteringTranslationRu", letteringTranslationRu);
    }

    public boolean comparePicture(UUID nTypePartUuid, String picture) {
        return comparePropertyValue(nTypePartUuid, "picture", picture, String.class);
    }

    public boolean setPicture(UUID nTypePartUuid, String picture) {
        return setPropertyStringValue(nTypePartUuid, "picture", picture);
    }

    // End: Methods for Numista parsing

}
