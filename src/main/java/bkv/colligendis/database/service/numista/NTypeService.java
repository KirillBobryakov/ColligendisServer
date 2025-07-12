package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NTypeService extends AbstractService<NType, NTypeRepository> {

    public NTypeService(NTypeRepository repository) {
        super(repository);
    }

    public NType findByNid(String nid) {

        return repository.findByNid(nid);
    }

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
     * Try to find NType by unique field {@code nid}.
     * After find a NType, update a title and connected Issuer on {@code title} and
     * {@code issuerUuid}
     * Can't find NType by nid in database, create new one with {@code title} and
     * relationship (:NTYPE)-[:ISSUED_BY]->(:ISSUER)
     *
     * @param nid        NType's nid
     * @param title      new field NType's title
     * @param issuerUuid related Issuer's uuid
     * @return
     */
    public UUID findNTypeUuidByTitleOrCreate(String nid, String title, UUID issuerUuid) {
        UUID nTypeUuid = findNTypeUuidByNid(nid);
        if (nTypeUuid == null) { // There is no NType with nid in Database. Create new.
            nTypeUuid = repository.save(new NType(nid, title)).getUuid();
            if (nTypeUuid == null) {
                DebugUtil.showError(this, "Can't create new NType with title= " + title + ".");
                return null;
            }
            DebugUtil.showInfo(this, "New NType with title= " + title + " was created.");
            setIssuedBy(nTypeUuid, issuerUuid);
        } else { // Has NType in the database

            if (!compareTitle(nTypeUuid, title)) { // set new title
                DebugUtil.showError(this, "NType with nid: " + nid + " has title: "
                        + getPropertyStringValue(nTypeUuid, "title") + " then new title: " + title);
                setPropertyStringValue(nTypeUuid, "title", title);
            }

            if (repository.hasAnyRelationshipWithType(nTypeUuid.toString(), NType.ISSUED_BY)) { // has a relationship to
                                                                                                // any Issuer
                if (!hasIssuedBy(nTypeUuid, issuerUuid)) { // connect with new Issuer with issuerUuid
                    DebugUtil.showWarning(this,
                            "NType with nid " + nid + " has issue "
                                    + getPropertyStringValue(getNTypeIssuerUuid(nTypeUuid), "name") + ". " +
                                    "New Issuer is " + getPropertyStringValue(issuerUuid, "name"));
                    detachIssuer(nTypeUuid);
                    setIssuedBy(nTypeUuid, issuerUuid);
                }
            } else { // create relationship to Issuer with issuerUuid
                DebugUtil.showWarning(this,
                        "NType with nid " + nid + " new Issuer is " + getPropertyStringValue(issuerUuid, "name"));
                setIssuedBy(nTypeUuid, issuerUuid);
            }
        }

        setActual(nTypeUuid);

        return nTypeUuid;
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

    /**
     * Find an Issuer's uuid with relationship to NType
     * (n:NTYPE)-[:ISSUED_BY]->(i:ISSUER)
     *
     * @param nTypeUuid NType's uuid
     * @return Issuer's uuid
     */
    public UUID getNTypeIssuerUuid(UUID nTypeUuid) {
        String uuid = repository.getNTypeIssuerUuid(nTypeUuid.toString());
        return uuid != null ? UUID.fromString(uuid) : null;
    }

    public void detachIssuer(UUID nTypeUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(nTypeUuid, getNTypeIssuerUuid(nTypeUuid), NType.ISSUED_BY);
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

    public List<NType> findNTypesByIssuerCodeWithFilter(String code, String collectibleTypeCode) {
        return repository.findNTypesByIssuerCodeWithFilter(code, collectibleTypeCode);
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

    /**
     * If there is no a relationship between NTYPE (with UUID $nTypeUuid) and
     * CATEGORY (with UUID $categoryUuid), then create one with a label -
     * UNDER_CATEGORY.
     *
     * @param nTypeUuid    NTYPE's UUID
     * @param categoryUuid CATEGORY's UUID
     * @return {@code true} If a relationship was presented, or was created;
     *         {@code false} There was not a relationship, and it was not created
     */
    public Boolean setUnderCategory(UUID nTypeUuid, UUID categoryUuid) {
        return repository.createSingleRelationshipToNode(nTypeUuid.toString(), categoryUuid.toString(),
                NType.UNDER_CATEGORY);
    }

    /**
     * This method can be use for creating relationship to an Issuer and to an
     * IssuingEntity.
     * <p>
     * For Issuer: (:NTYPE)-[:ISSUED_BY]->(:ISSUER)
     * If there is no a relationship between NTYPE (with UUID $nTypeUuid) and ISSUER
     * (with UUID $issuerUuid), then create one with a label - ISSUED_BY.
     * For IssuingEntity: (:NTYPE)-[:ISSUED_BY]->(:ISSUING_ENTITY)
     * If there is no a relationship between NTYPE (with UUID $nTypeUuid) and
     * ISSUING_ENTITY (with UUID $issuerUuid), then create one with a label -
     * ISSUED_BY.
     *
     * @param nTypeUuid  NTYPE's UUID
     * @param issuerUuid ISSUER's UUID, or ISSUING_ENTITY's UUID
     * @return {@code true} If a relationship was presented, or was created;
     *         {@code false} There was not a relationship, and it was not created
     */
    public Boolean setIssuedBy(UUID nTypeUuid, UUID issuerUuid) {
        return repository.createSingleRelationshipToNode(nTypeUuid.toString(), issuerUuid.toString(), NType.ISSUED_BY);
    }

    /**
     * Check is the relationship to an Issuer and to an IssuingEntity exists.
     * <p>
     * For Issuer: (:NTYPE)-[:ISSUED_BY]->(:ISSUER)
     * Is there a relationship between NTYPE (with UUID $nTypeUuid) and ISSUER (with
     * UUID $issuerUuid).
     * For IssuingEntity: (:NTYPE)-[:ISSUED_BY]->(:ISSUING_ENTITY)
     * Is there a relationship between NTYPE (with UUID $nTypeUuid) and
     * ISSUING_ENTITY (with UUID $issuerUuid).
     *
     * @param nTypeUuid  NTYPE's UUID
     * @param issuerUuid ISSUER's UUID, or ISSUING_ENTITY's UUID
     * @return {@code true} If a relationship is exists, or false is doesn't.
     */
    public Boolean hasIssuedBy(UUID nTypeUuid, UUID issuerUuid) {
        return repository.hasSingleRelationshipToNode(nTypeUuid.toString(), issuerUuid.toString(), NType.ISSUED_BY);
    }

    /**
     * This method can be use for creating relationship to an Denomination.
     * <p>
     * If there is no a relationship between NTYPE (with UUID nTypeUuid) and
     * DENOMINATION (with UUID denominationUuid), then create one with a label -
     * DENOMINATED_IN.
     *
     * @param nTypeUuid        NTYPE's UUID
     * @param denominationUuid DENOMINATION's UUID
     * @return {@code true} If a relationship was presented, or was created;
     *         {@code false} There was not a relationship, and it was not created
     */
    public Boolean setDenominatedIn(UUID nTypeUuid, UUID denominationUuid) {
        return repository.createSingleRelationshipToNode(nTypeUuid.toString(), denominationUuid.toString(),
                NType.DENOMINATED_IN);
    }

    /**
     * If there is no a relationship between NTYPE (with UUID $nTypeUuid) and RULER
     * (with UUID $rulerUuid), then create one with a label - DURING_OF_RULER.
     *
     * @param nTypeUuid NTYPE's UUID
     * @param rulerUuid RULER's UUID
     * @return {@code true} If a relationship was presented, or was created;
     *         {@code false} There was not a relationship, and it was not created
     */
    public Boolean setDuringOfRuler(UUID nTypeUuid, UUID rulerUuid) {
        return repository.createSingleRelationshipToNode(nTypeUuid.toString(), rulerUuid.toString(),
                NType.DURING_OF_RULER);
    }

    /**
     * Because of nType's title field is a unique field, this method helps to check
     * some {@code title} with nType's title filed.
     *
     * @param nTypeUuid NTYPE's UUID
     * @param title     comparing with
     * @return {@code true} equals, and {@code false} if not equal
     */
    public Boolean compareTitle(UUID nTypeUuid, String title) {
        return repository.compareTitle(nTypeUuid.toString(), title);
    }

}
