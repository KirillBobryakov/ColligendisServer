package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CatalogueReference;
import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.entity.numista.CommemoratedEvent;
import bkv.colligendis.database.entity.numista.Composition;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.Denomination;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.IssuingEntity;
import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.NTypePart;
import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.database.entity.numista.Series;
import bkv.colligendis.database.entity.numista.Shape;
import bkv.colligendis.database.entity.numista.Technique;
import bkv.colligendis.services.AbstractService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NTypeService extends AbstractService<NType, NTypeRepository> {

    public NTypeService(NTypeRepository repository) {
        super(repository);
    }

    public NType findByNid(String nid) {

        return repository.findByNid(nid);
    }

    public void setDemonetization(UUID nTypeUuid, String demonetized, String demonetizationYear,
            String demonetizationMonth, String demonetizationDay) {
        repository.setDemonetization(nTypeUuid.toString(), demonetized, demonetizationYear, demonetizationMonth,
                demonetizationDay);
    }

    // Start: Methods for Numista parsing

    public boolean compareTitle(UUID nTypeUuid, String title) {
        return comparePropertyValue(nTypeUuid, "title", title, String.class);
    }

    public boolean setTitle(UUID nTypeUuid, String title) {
        return setPropertyStringValue(nTypeUuid, "title", title);
    }

    public void setCollectibleType(UUID nTypeUuid, UUID collectibleTypeUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, collectibleTypeUuid, NType.HAS_COLLECTIBLE_TYPE,
                CollectibleType.LABEL);
    }

    public boolean hasRelationshipToCollectibleType(UUID nTypeUuid, UUID collectibleTypeUuid) {
        return hasSingleRelationshipToNode(nTypeUuid, collectibleTypeUuid, NType.HAS_COLLECTIBLE_TYPE);
    }

    public UUID getCollectibleTypeUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.HAS_COLLECTIBLE_TYPE, CollectibleType.LABEL);
    }

    public boolean hasRelationshipToIssuer(UUID nTypeUuid, UUID issuerUuid) {
        return hasSingleRelationshipToNode(nTypeUuid, issuerUuid, NType.ISSUED_BY);
    }

    public UUID getIssuerUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.ISSUED_BY, Issuer.LABEL);
    }

    public void setIssuer(UUID nTypeUuid, UUID issuerUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, issuerUuid, NType.ISSUED_BY, Issuer.LABEL);
    }

    public List<UUID> getRulers(UUID nTypeUuid) {
        return getAllOutgoingRelatedNodesUUIDs(nTypeUuid, NType.DURING_OF_RULER, Ruler.LABEL);
    }

    public boolean detachRuler(UUID nTypeUuid, UUID rulerUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypeUuid, rulerUuid, NType.DURING_OF_RULER);
        return true;
    }

    public boolean addRuler(UUID nTypeUuid, UUID rulerUuid) {
        addSingleOutgoingRelationshipToNode(nTypeUuid, rulerUuid, NType.DURING_OF_RULER);
        return true;
    }

    public boolean equateRulers(UUID nTypeUuid, List<UUID> matchingRulersUUIDs) {
        return equateFistListToSecondList(getRulers(nTypeUuid), matchingRulersUUIDs, this::detachRuler, this::addRuler,
                nTypeUuid);
    }

    public List<UUID> getIssuingEntities(UUID nTypeUuid) {
        return getAllOutgoingRelatedNodesUUIDs(nTypeUuid, NType.ISSUED_BY_ISSUING_ENTITY, IssuingEntity.LABEL);
    }

    public boolean detachIssuingEntity(UUID nTypeUuid, UUID issuingEntityUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypeUuid, issuingEntityUuid,
                NType.ISSUED_BY_ISSUING_ENTITY);
        return true;
    }

    public boolean addIssuingEntity(UUID nTypeUuid, UUID issuingEntityUuid) {
        addSingleOutgoingRelationshipToNode(nTypeUuid, issuingEntityUuid, NType.ISSUED_BY_ISSUING_ENTITY);
        return true;
    }

    public boolean equateIssuingEntities(UUID nTypeUuid, List<UUID> matchingIssuingEntitiesUUIDs) {
        return equateFistListToSecondList(getIssuingEntities(nTypeUuid), matchingIssuingEntitiesUUIDs,
                this::detachIssuingEntity, this::addIssuingEntity, nTypeUuid);
    }

    public boolean hasRelationshipToCurrency(UUID nTypeUuid, UUID currencyUuid) {
        return hasSingleRelationshipToNode(nTypeUuid, currencyUuid, NType.REFERS_TO_CURRENCY);
    }

    public UUID getCurrencyUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.REFERS_TO_CURRENCY, Currency.LABEL);
    }

    public void setCurrency(UUID nTypeUuid, UUID currencyUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, currencyUuid, NType.REFERS_TO_CURRENCY, Currency.LABEL);
    }

    public boolean hasRelationshipToDenomination(UUID nTypeUuid, UUID denominationUuid) {
        return hasSingleRelationshipToNode(nTypeUuid, denominationUuid, NType.DENOMINATED_IN);
    }

    public void setDenomination(UUID nTypeUuid, UUID denominationUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, denominationUuid, NType.DENOMINATED_IN, Denomination.LABEL);
    }

    public UUID getDenominationUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.DENOMINATED_IN, Denomination.LABEL);
    }

    public boolean hasRelationshipToCommemoratedEvent(UUID nTypeUuid, UUID commemoratedEventUuid) {
        return hasSingleRelationshipToNode(nTypeUuid, commemoratedEventUuid, NType.COMMEMORATE_FOR);
    }

    public UUID getCommemoratedEventUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.COMMEMORATE_FOR, CommemoratedEvent.LABEL);
    }

    public void detachCommemoratedEvent(UUID nTypeUuid, UUID commemoratedEventUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypeUuid, commemoratedEventUuid,
                NType.COMMEMORATE_FOR);
    }

    public void setCommemoratedEvent(UUID nTypeUuid, UUID commemoratedEventUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, commemoratedEventUuid, NType.COMMEMORATE_FOR,
                CommemoratedEvent.LABEL);
    }

    public boolean hasRelationshipToSeries(UUID nTypeUuid, UUID seriesUuid) {
        return hasSingleRelationshipToNode(nTypeUuid, seriesUuid, NType.HAS_SERIES);
    }

    public void setSeries(UUID nTypeUuid, UUID seriesUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, seriesUuid, NType.HAS_SERIES, Series.LABEL);
    }

    public UUID getSeriesUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.HAS_SERIES, Series.LABEL);
    }

    public boolean compareDemonetized(UUID nTypeUuid, String demonetizedValue) {
        return comparePropertyValue(nTypeUuid, "demonetized", demonetizedValue, String.class);
    }

    public void setDemonetized(UUID nTypeUuid, String demonetized) {
        setPropertyStringValue(nTypeUuid, "demonetized", demonetized);
    }

    public boolean compareDemonetizationYear(UUID nTypeUuid, String demonetizationYear) {
        return comparePropertyValue(nTypeUuid, "demonetizationYear", demonetizationYear, String.class);
    }

    public void setDemonetizationYear(UUID nTypeUuid, String demonetizationYear) {
        setPropertyStringValue(nTypeUuid, "demonetizationYear", demonetizationYear);
    }

    public boolean compareDemonetizationMonth(UUID nTypeUuid, String demonetizationMonth) {
        return comparePropertyValue(nTypeUuid, "demonetizationMonth", demonetizationMonth, String.class);
    }

    public void setDemonetizationMonth(UUID nTypeUuid, String demonetizationMonth) {
        setPropertyStringValue(nTypeUuid, "demonetizationMonth", demonetizationMonth);
    }

    public boolean compareDemonetizationDay(UUID nTypeUuid, String demonetizationDay) {
        return comparePropertyValue(nTypeUuid, "demonetizationDay", demonetizationDay, String.class);
    }

    public void setDemonetizationDay(UUID nTypeUuid, String demonetizationDay) {
        setPropertyStringValue(nTypeUuid, "demonetizationDay", demonetizationDay);
    }

    public boolean compareYearIssueDate(UUID nTypeUuid, String yearIssueDate) {
        return comparePropertyValue(nTypeUuid, "yearIssueDate", yearIssueDate, String.class);
    }

    public void setYearIssueDate(UUID nTypeUuid, String yearIssueDate) {
        setPropertyStringValue(nTypeUuid, "yearIssueDate", yearIssueDate);
    }

    public boolean compareMonthIssueDate(UUID nTypeUuid, String monthIssueDate) {
        return comparePropertyValue(nTypeUuid, "monthIssueDate", monthIssueDate, String.class);
    }

    public void setMonthIssueDate(UUID nTypeUuid, String monthIssueDate) {
        setPropertyStringValue(nTypeUuid, "monthIssueDate", monthIssueDate);
    }

    public boolean compareDayIssueDate(UUID nTypeUuid, String dayIssueDate) {
        return comparePropertyValue(nTypeUuid, "dayIssueDate", dayIssueDate, String.class);
    }

    public void setDayIssueDate(UUID nTypeUuid, String dayIssueDate) {
        setPropertyStringValue(nTypeUuid, "dayIssueDate", dayIssueDate);
    }

    public boolean equateCatalogueReferences(UUID nTypeUuid, List<UUID> matchingCatalogueReferencesUUIDs) {
        return equateFistListToSecondList(getCatalogueReferences(nTypeUuid), matchingCatalogueReferencesUUIDs,
                this::detachCatalogueReference, this::addCatalogueReference, nTypeUuid);
    }

    public List<UUID> getCatalogueReferences(UUID nTypeUuid) {
        return getAllOutgoingRelatedNodesUUIDs(nTypeUuid, NType.HAS_REFERENCE, CatalogueReference.LABEL);
    }

    public boolean detachCatalogueReference(UUID nTypeUuid, UUID catalogueReferenceUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypeUuid, catalogueReferenceUuid, NType.HAS_REFERENCE);
        return true;
    }

    public boolean addCatalogueReference(UUID nTypeUuid, UUID catalogueReferenceUuid) {
        addSingleOutgoingRelationshipToNode(nTypeUuid, catalogueReferenceUuid, NType.HAS_REFERENCE);
        return true;
    }

    public void addVariant(UUID nTypeUuid, UUID variantUuid) {
        addSingleOutgoingRelationshipToNode(nTypeUuid, variantUuid, NType.VARIANTS);
    }

    public UUID getCompositionUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.HAS_COMPOSITION, Composition.LABEL);
    }

    public void setComposition(UUID nTypeUuid, UUID compositionUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, compositionUuid, NType.HAS_COMPOSITION, Composition.LABEL);
    }

    public UUID getShapeUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.HAS_SHAPE, Shape.LABEL);
    }

    public void setShape(UUID nTypeUuid, UUID shapeUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, shapeUuid, NType.HAS_SHAPE, Shape.LABEL);
    }

    public boolean compareShapeAdditionalDetails(UUID nTypeUuid, String shapeAdditionalDetails) {
        return comparePropertyValue(nTypeUuid, "shapeAdditionalDetails", shapeAdditionalDetails, String.class);
    }

    public boolean setShapeAdditionalDetails(UUID nTypeUuid, String shapeAdditionalDetails) {
        return setPropertyStringValue(nTypeUuid, "shapeAdditionalDetails", shapeAdditionalDetails);
    }

    public boolean compareWeight(UUID nTypeUuid, Float weight) {
        return comparePropertyValue(nTypeUuid, "weight", weight, Float.class);
    }

    public boolean setWeight(UUID nTypeUuid, Float weight) {
        return setPropertyFloatValue(nTypeUuid, "weight", weight);
    }

    public boolean compareSize(UUID nTypeUuid, Float size) {
        return comparePropertyValue(nTypeUuid, "size", size, Float.class);
    }

    public boolean setSize(UUID nTypeUuid, Float size) {
        return setPropertyFloatValue(nTypeUuid, "size", size);
    }

    public boolean compareSize2(UUID nTypeUuid, Float size2) {
        return comparePropertyValue(nTypeUuid, "size2", size2, Float.class);
    }

    public boolean setSize2(UUID nTypeUuid, Float size2) {
        return setPropertyFloatValue(nTypeUuid, "size2", size2);
    }

    public boolean compareThickness(UUID nTypeUuid, Float thickness) {
        return comparePropertyValue(nTypeUuid, "thickness", thickness, Float.class);
    }

    public boolean setThickness(UUID nTypeUuid, Float thickness) {
        return setPropertyFloatValue(nTypeUuid, "thickness", thickness);
    }

    public List<UUID> getTechniques(UUID nTypeUuid) {
        return getAllOutgoingRelatedNodesUUIDs(nTypeUuid, NType.WITH_TECHNIQUE, Technique.LABEL);
    }

    public boolean detachTechnique(UUID nTypeUuid, UUID techniqueUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypeUuid, techniqueUuid, NType.WITH_TECHNIQUE);
        return true;
    }

    public boolean addTechnique(UUID nTypeUuid, UUID techniqueUuid) {
        addSingleOutgoingRelationshipToNode(nTypeUuid, techniqueUuid, NType.WITH_TECHNIQUE);
        return true;
    }

    public void equateTechniques(UUID nTypeUuid, List<UUID> matchingTechniqueUuids) {
        equateFistListToSecondList(getTechniques(nTypeUuid), matchingTechniqueUuids, this::detachTechnique,
                this::addTechnique, nTypeUuid);
    }

    public boolean compareTechniqueAdditionalDetails(UUID nTypeUuid, String techniqueAdditionalDetails) {
        return comparePropertyValue(nTypeUuid, "techniqueAdditionalDetails", techniqueAdditionalDetails, String.class);
    }

    public boolean setTechniqueAdditionalDetails(UUID nTypeUuid, String techniqueAdditionalDetails) {
        return setPropertyStringValue(nTypeUuid, "techniqueAdditionalDetails", techniqueAdditionalDetails);
    }

    public boolean compareAlignment(UUID nTypeUuid, String alignment) {
        return comparePropertyValue(nTypeUuid, "alignment", alignment, String.class);
    }

    public boolean setAlignment(UUID nTypeUuid, String alignment) {
        return setPropertyStringValue(nTypeUuid, "alignment", alignment);
    }

    public UUID getObverseUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.HAS_OBVERSE, NTypePart.LABEL);
    }

    public void setObverse(UUID nTypeUuid, UUID obverseUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, obverseUuid, NType.HAS_OBVERSE, NTypePart.LABEL);
    }

    public UUID getReverseUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.HAS_REVERSE, NTypePart.LABEL);
    }

    public void setReverse(UUID nTypeUuid, UUID reverseUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, reverseUuid, NType.HAS_REVERSE, NTypePart.LABEL);
    }

    public UUID getEdgeUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.HAS_EDGE, NTypePart.LABEL);
    }

    public void setEdge(UUID nTypeUuid, UUID edgeUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, edgeUuid, NType.HAS_EDGE, NTypePart.LABEL);
    }

    public UUID getWatermarkUuid(UUID nTypeUuid) {
        return getSingleRelatedNodeUUID(nTypeUuid, NType.HAS_WATERMARK, NTypePart.LABEL);
    }

    public void setWatermark(UUID nTypeUuid, UUID watermarkUuid) {
        setSingleOutgoingRelationshipToNode(nTypeUuid, watermarkUuid, NType.HAS_WATERMARK, NTypePart.LABEL);
    }

    // End: Methods for Numista parsing

    /**
     * Find a NType's uuid by {@code nid}
     *
     * @param nid NType's nid
     * @return NType's uuid if exists, or null
     */
    public UUID findNTypeUuidByNid(String nid) {
        String uuid = repository.findNTypeUuidByNid(nid.toString());
        return uuid != null ? UUID.fromString(uuid) : null;
    }

    // public NType findByNidAll(String nid){
    // List<NType> nTypes = repository.findByNidAll(nid);

    // return nTypes.get(0);
    // }

    public boolean existsByNid(String nid) {
        return Boolean.TRUE.equals(repository.existsByNid(nid));
    }

    /**
     * Set NTYPE's {@code isActual} in true
     *
     * @param uuid NTYPE's uuid
     */
    void setActual(UUID uuid) {
        repository.setActual(uuid.toString());
    }

    /**
     * Find a list of NTYPE's nid of only actual NTYPEs
     *
     * @return list of NTYPE's nid
     */
    public List<String> findActualNTypeNidList() {
        return repository.findNTypeNidListByIsActual(true);
    }

    /**
     * Find a list of NTYPE's nid of only not actual NTYPEs
     *
     * @return list of NTYPE's nid
     */
    public List<String> findNotActualNTypeNidList() {
        return repository.findNTypeNidListByIsActual(false);
    }

    // public List<NType> findByTitleFilter(String filter){
    // return repository.findByTitleFilter("(?i).*" + filter + ".*");
    // }

    public List<NType> findNTypesByCountryNumistaCodeWithFilters(String numistaCode, String denominationNid,
            String issuerCode,
            String subjectNumistaCode, String collectibleTypeCode, String textFilter) {
        assert numistaCode != null;
        assert !numistaCode.isEmpty();
        assert denominationNid != null;
        assert issuerCode != null;
        assert subjectNumistaCode != null;

        return repository.findNTypesByCountryNumistaCodeWithFilters(numistaCode,
                denominationNid, issuerCode, subjectNumistaCode, collectibleTypeCode, textFilter);
    }

    public List<NType> findNTypesBySubjectNumistaCodeWithFilters(String numistaCode, String denominationNid,
            String issuerCode,
            String subjectNumistaCode, String collectibleTypeCode) {
        assert numistaCode != null;
        assert !numistaCode.isEmpty();
        assert denominationNid != null;
        assert issuerCode != null;
        assert subjectNumistaCode != null;

        return repository.findNTypesBySubjectNumistaCodeWithFilters(numistaCode,
                denominationNid, issuerCode, subjectNumistaCode, collectibleTypeCode);
    }

    /*
     * Find NTypes by Country's numista code and Collectible Type's code
     * 
     * @param numistaCode Country's numista code
     * 
     * @param collectibleTypeCode Collectible Type's code
     * 
     * @return List of NTypes
     */
    public List<NType> findNTypesByCountryNumistaCodeWithFilterByCollectableTypeByCurrencyNid(String countryNumistaCode,
            String collectibleTypeCode, String currencyNid) {
        List<NType> nTypes = repository.findNTypesByCountryNumistaCodeWithFilterByCollectableTypeByCurrencyNid(
                countryNumistaCode,
                collectibleTypeCode, currencyNid);
        nTypes = nTypes.stream().distinct().collect(Collectors.toList());

        return nTypes;
    }

    /*
     * Find NTypes by Subject's numista code and Collectible Type's code
     * 
     * @param numistaCode Subject's numista code
     * 
     * @param collectibleTypeCode Collectible Type's code
     * 
     * @return List of NTypes
     */
    public List<NType> findNTypesBySubjectNumistaCodeWithFilterByCollectableTypeByCurrencyNid(String subjectNumistaCode,
            String collectibleTypeCode, String currencyNid) {
        return repository.findNTypesBySubjectNumistaCodeWithFilterByCollectableTypeByCurrencyNid(subjectNumistaCode,
                collectibleTypeCode, currencyNid);
    }

    /*
     * Find NTypes by Issuer's code and Collectible Type's code
     * 
     * @param code Issuer's code
     * 
     * @param collectibleTypeCode Collectible Type's code
     * 
     * @return List of NTypes
     */
    public List<NType> findNTypesByIssuerCodeWithFilterByCollectableTypeByCurrencyNid(String issuerCode,
            String collectibleTypeCode, String currencyNid) {
        return repository.findNTypesByIssuerCodeWithFilterByCollectableTypeByCurrencyNid(issuerCode,
                collectibleTypeCode, currencyNid);
    }

    /**
     * Find List of NType's {@code nid} by Issuer's uuid
     *
     * @param uuid Issuer's uuid
     * @return List of NType's nid
     */
    public List<String> findNTypeNidListByIssuerEid(UUID uuid) {
        return repository.findNTypeNidListByIssuerEid(uuid.toString());
    }

    public List<String> findNTypeNidByIssuerEid(String eid) {
        return repository.findNTypeNidByIssuerEid(eid);
    }

    public List<NType> findByIssuerEidAndYear(String eid, int year) {
        return repository.findByIssuerEidAndYear(eid, year);
    }

    public List<NType> findByTitleFilterAndIssuerEidAndYear(String filter, String issuerEid, int year) {
        return repository.findByTitleFilterAndIssuerEidAndYear("(?i).*" + filter + ".*", issuerEid, year);
    }

    // Statistics
    public Integer countNTypesByCountryNumistaCodeAndCollectibleTypeCode(String countryNumistaCode,
            String collectibleTypeCode) {
        return repository.countNTypesByCountryNumistaCodeAndCollectibleTypeCode(countryNumistaCode,
                collectibleTypeCode);
    }

    public Integer countNTypesBySubjectNumistaCodeAndCollectibleTypeCode(String subjectNumistaCode,
            String collectibleTypeCode) {
        return repository.countNTypesBySubjectNumistaCodeAndCollectibleTypeCode(subjectNumistaCode,
                collectibleTypeCode);
    }

    public Integer countNTypesByIssuerCodeAndCollectibleTypeCode(String issuerCode, String collectibleTypeCode) {
        return repository.countNTypesByIssuerCodeAndCollectibleTypeCode(issuerCode, collectibleTypeCode);
    }
}
