package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.service.UniqueEntityException;
import bkv.colligendis.rest.catalogue.CSIItem;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IssuerService extends AbstractService<Issuer, IssuerRepository> {
    public IssuerService(IssuerRepository repository) {
        super(repository);
    }

    /**
     * Find issuer's {@code uuid} like UUID by {@code  name}.
     * 
     * @param name Issuer's name
     * @return If Issuer with {@code name} exists, then return issuer's uuid like
     *         UUID, or return NULL
     */
    public UUID findIssuerUuidByName(String name) {
        String uuid = repository.findIssuerUuidByName(name);
        return uuid != null ? UUID.fromString(uuid) : null;
    }

    /**
     * Find issuer's {@code uuid} like UUID by {@code  name}. if not, then create
     * new one with {@code name}.
     * 
     * @param name Issuer's name
     * @return issuer's uuid like UUID
     */
    public UUID findIssuerUuidByNameOrCreate(String name) {
        UUID issuerUuid = findIssuerUuidByName(name);
        if (issuerUuid == null) {
            DebugUtil.showInfo(this, "New Issuer with name=" + name + " was created.");
            return repository.save(new Issuer(name)).getUuid();
        }
        return issuerUuid;
    }

    /**
     * Find Issuer's UUID by Issuer's code
     * 
     * @param code Issuer's code
     * @return Issuer's UUID, or return null
     */
    public UUID findIssuerUuidByCode(String code) {
        String eid = repository.findUuidByCode(code);
        return eid != null ? UUID.fromString(eid) : null;
    }

    /**
     * Find Issuer's Code by Issuer's Eid
     * 
     * @param eid Issuer's eid in UUID value
     * @return Issuer's code, or return null
     */
    public String findIssuerCodeByEid(UUID eid) {
        return repository.findIssuerCodeByUuid(eid.toString());
    }

    /**
     * Find issuer's {@code eid} UUID by {@code code} firstly, then by
     * {@code  name}.
     * 
     * @param code Issuer's code
     * @param name Issuer's name
     * @return If Issuer with {@code code} or {@code name} exists, then return
     *         issuer's eid like UUID
     */
    public UUID findIssuerUuidByCodeThenName(String code, String name) {
        UUID issuerUuid = findIssuerUuidByCode(code);
        if (issuerUuid == null) {
            issuerUuid = findIssuerUuidByName(name);
            DebugUtil.showInfo(this, "Find Issuer by name=" + name + ". Can't find by code: " + code);
        }

        return issuerUuid;
    }

    /**
     * Find issuer by {@code code} firstly, then by {@code  name}.
     * 
     * @param code Issuer's code
     * @param name Issuer's name
     * @return If Issuer with {@code code} or {@code name} exists, then return
     *         issuer
     */
    public Issuer findIssuerByCodeOrName(String code, String name) throws UniqueEntityException {
        List<Issuer> issuers = repository.findByCodeOrName(code, name);

        if (issuers.size() > 1) {
            throw new UniqueEntityException(Map.of("code", code, "name", name));
        }
        return issuers.stream().findFirst().orElse(null);
    }

    /**
     * If there is no a relationship between ISSUER (with UUID issuerUuid) and
     * ISSUING_ENTITY (with UUID issuingEntityUuid), then create one with a label -
     * CONTAINS_ISSUING_ENTITY.
     * 
     * @param issuerUuid        ISSUER's UUID
     * @param issuingEntityUuid ISSUING_ENTITY's UUID
     * 
     * @return {@code true} If a relationship was presented, or was created;
     *         {@code false} There was not a relationship, and it was not created
     */
    public Boolean setContainsIssuingEntity(UUID issuerUuid, UUID issuingEntityUuid) {
        return repository.createSingleRelationshipToNode(issuerUuid.toString(), issuingEntityUuid.toString(),
                Issuer.CONTAINS_ISSUING_ENTITY);
    }

    /**
     * Check a Relationship between Issuer and IssuingEntity
     * 
     * @param issuerUuid        ISSUER's UUID
     * @param issuingEntityUuid ISSUING_ENTITY's UUID
     * @return {@code true} If a relationship was presented, return {@code false} if
     *         there isn't a relationship
     */
    public Boolean hasContainsIssuingEntityRelationshipToIssuingEntity(UUID issuerUuid, UUID issuingEntityUuid) {
        return repository.hasSingleRelationshipToNode(issuerUuid.toString(), issuingEntityUuid.toString(),
                Issuer.CONTAINS_ISSUING_ENTITY);
    }

    public List<Issuer> findByNameContainingIgnoreCase(String nameFilter) {
        return repository.findByNameContainingIgnoreCase(".*(?i)" + nameFilter + ".*").stream().distinct()
                .collect(Collectors.toList());
    }

    public List<Issuer> findByCountryEid(String eid) {
        return repository.findByCountryEid(eid);
    }

    public List<Issuer> findBySubjectEid(String eid) {
        return repository.findBySubjectEid(eid);
    }

    /**
     * Check is Issuer's code equals to {@code code}.
     * This Query is needed to know is new issuer's code equal to old one before
     * {@code updateIssuerCode} method.
     *
     * @param issuerUuid Unique UUID field
     * @param code       value for checking
     * @return {@code true} if equal, or {@code false} if not
     */
    public Boolean isIssuerCodeEqualsTo(UUID issuerUuid, String code) {
        return repository.isIssuerCodeEqualsTo(issuerUuid.toString(), code);
    }

    /**
     * Use this method for update issuer's code on new one.
     *
     * @param issuerUuid Unique UUID field
     * @param code       new issuer's code
     */
    public void updateIssuerCode(UUID issuerUuid, String code) {
        repository.updateIssuerCode(issuerUuid.toString(), code);
    }

    public Issuer update(Issuer issuer, String code, String name) {
        if (issuer == null || !issuer.getCode().equals(code)) {
            // issuer = repository.findByCode(code);
        }
        if (issuer != null) {
            if (!issuer.getName().equals(name)) {
                DebugUtil.showWarning(this, "Trying to find Issuer with code=" + code + " and name=" + name
                        + ". But there is an Issuer with the same code and other name= " + issuer.getName()
                        + " in DB already.");
                DebugUtil.showWarning(this, "Issuer.name was updated.");
                issuer.setName(name);
                return repository.save(issuer);
            }
        } else {
            DebugUtil.showInfo(this, "New Issuer with code=" + code + " and name=" + name + " was created.");
            return repository.save(new Issuer(code, name));
        }
        return issuer;
    }

    /**
     * Find Issuer by {@code numistaCode}
     * 
     * @param numistaCode Issuer's numistaCode
     * @return Issuer if exists, or null
     */
    public Issuer findIssuerByNumistaCode(String numistaCode) {
        return repository.findIssuerByNumistaCode(numistaCode);
    }

    /**
     * Connect issuer to country
     * 
     * @param issuerUuid  Issuer's UUID
     * @param countryUuid Country's UUID
     * @return {@code true} if connection was created, {@code false} if connection
     *         already exists
     */
    public boolean connectToCountry(UUID issuerUuid, UUID countryUuid) {
        return repository.connectToCountry(issuerUuid.toString(), countryUuid.toString());
    }

    /**
     * Connect issuer to parent subject
     * 
     * @param issuerUuid        Issuer's UUID
     * @param parentSubjectUuid Parent subject's UUID
     * @return {@code true} if connection was created, {@code false} if connection
     *         already exists
     */
    public boolean connectToParentSubject(UUID issuerUuid, UUID parentSubjectUuid) {
        return repository.connectToParentSubject(issuerUuid.toString(), parentSubjectUuid.toString());
    }

    public List<CSIItem> findCSItemsByName(String filter) {
        List<CSIItem> csiItems = repository.findCSItemsByName(filter);
        if (csiItems != null && !csiItems.isEmpty()) {
            return csiItems.stream().map(t -> {
                return new CSIItem(CSIItem.Label.ISSUER, t.getCode(), t.getName());
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public Integer countChildrenNTypes(String numistaCode) {
        return repository.countChildrenNTypes(numistaCode);
    }

    public List<Issuer> findChildrenIssuersBySubjectNumistaCode(String subjectNumistaCode) {
        return repository.findChildrenIssuersBySubjectNumistaCode(subjectNumistaCode);
    }

    public List<Issuer> findChildrenIssuersByCountryNumistaCode(String countryNumistaCode) {
        return repository.findChildrenIssuersByCountryNumistaCode(countryNumistaCode);
    }

}
