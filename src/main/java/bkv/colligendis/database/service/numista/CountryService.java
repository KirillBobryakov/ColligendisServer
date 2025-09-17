package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.rest.catalogue.CSIItem;
import bkv.colligendis.rest.catalogue.csi_statistics.CSITreeNode;
import bkv.colligendis.rest.dto.CountryDTO;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CountryService extends AbstractService<Country, CountryRepository> {

    private final ModelMapper modelMapper;

    public CountryService(CountryRepository repository, ModelMapper modelMapper) {
        super(repository);
        this.modelMapper = modelMapper;
    }

    /**
     * Find an COUNTRY's UUID by {@code name}
     *
     * @param name COUNTRY's name
     * @return COUNTRY's UUID
     */
    public UUID findCountryUuidByName(String name) {
        String uuid = repository.findCountryUuidByName(name);
        return uuid != null ? UUID.fromString(uuid) : null;
    }

    /**
     * Find COUNTRY's UUID by COUNTRY's name. If not, create new COUNTRY with
     * {@code name}
     *
     * @param name COUNTRY's name
     * @return COUNTRY's UUID
     */
    public UUID findCountryUuidByNameOrCreate(String name) {
        UUID countryUuid = findCountryUuidByName(name);
        if (countryUuid == null) {
            DebugUtil.showInfo(this, "New Country with name=" + name + " was created.");
            return repository.save(new Country(name)).getUuid();
        }
        return countryUuid;
    }

    // /**
    // * Check a Relationship between Country and Issuer
    // *
    // * @param countryUuid COUNTRY's UUID
    // * @param issuerUuid ISSUER's UUID
    // * @return {@code true} If a relationship was presented, return {@code false}
    // if
    // * there isn't a relationship
    // */
    // public Boolean hasContainsIssuerRelationshipToIssuer(UUID countryUuid, UUID
    // issuerUuid) {
    // return repository.hasSingleRelationshipToNode(countryUuid.toString(),
    // issuerUuid.toString(),
    // Country.CONTAINS_ISSUER);
    // }

    // /**
    // * If there is no a relationship between COUNTRY (with UUID countryUuid) and
    // * ISSUER (with UUID issuerUuid), then create one with a label -
    // * CONTAINS_ISSUER.
    // *
    // * @param countryUuid COUNTRY's UUID
    // * @param issuerUuid ISSUER's UUID
    // * @return {@code true} If a relationship was presented, or was created;
    // * {@code false} There was not a relationship, and it was not created
    // */
    // public Boolean setContainsIssuer(UUID countryUuid, UUID issuerUuid) {
    // return repository.createSingleRelationshipToNode(countryUuid.toString(),
    // issuerUuid.toString(),
    // Country.CONTAINS_ISSUER);
    // }

    public List<String> findAllCountriesNames() {
        return repository.findAllCountriesNames();
    }

    /**
     * Find all countries without relationships for Client Catalogue Service
     * 
     * @return List of countries with CountryDTO
     */
    public List<CountryDTO> findAllCountriesWithoutRelationships() {
        return repository.findAllCountriesWithoutRelationships().stream()
                .map(country -> modelMapper.map(country, CountryDTO.class))
                .collect(Collectors.toList());
    }

    public List<CSITreeNode> findByNameFilter(String nameFilter) {
        List<Country> countries = repository.findByNameFilter(nameFilter);

        return countries.stream()
                .map(country -> convertToCSITreeNodes(country))
                .collect(Collectors.toList());
    }

    public <T> CSITreeNode convertToCSITreeNodes(T entity) {
        if (entity instanceof Country) {
            Country country = (Country) entity;
            CSITreeNode countryNode = modelMapper.map(country, CSITreeNode.class);
            List<CSITreeNode> children = new ArrayList<>();
            countryNode.setType("country");
            if (country.getSubjects() != null && !country.getSubjects().isEmpty()) {
                children.addAll(country.getSubjects().stream()
                        .map(subject -> convertToCSITreeNodes(subject))
                        .collect(Collectors.toList()));
            }
            if (country.getIssuers() != null && !country.getIssuers().isEmpty()) {
                children.addAll(country.getIssuers().stream()
                        .map(issuer -> convertToCSITreeNodes(issuer))
                        .collect(Collectors.toList()));
            }
            countryNode.setChildren(children);

            return countryNode;
        } else if (entity instanceof Subject) {
            Subject subject = (Subject) entity;
            CSITreeNode subjectNode = modelMapper.map(subject, CSITreeNode.class);
            subjectNode.setType("subject");
            List<CSITreeNode> children = new ArrayList<>();
            if (subject.getChildSubjects() != null && !subject.getChildSubjects().isEmpty()) {
                children.addAll(subject.getChildSubjects().stream()
                        .map(childSubject -> convertToCSITreeNodes(childSubject))
                        .collect(Collectors.toList()));
            }
            if (subject.getIssuers() != null && !subject.getIssuers().isEmpty()) {
                children.addAll(subject.getIssuers().stream()
                        .map(issuer -> convertToCSITreeNodes(issuer))
                        .collect(Collectors.toList()));
            }
            subjectNode.setChildren(children);

            return subjectNode;
        } else if (entity instanceof Issuer) {
            Issuer issuer = (Issuer) entity;
            CSITreeNode issuerNode = modelMapper.map(issuer, CSITreeNode.class);
            issuerNode.setType("issuer");
            return issuerNode;
        }

        return null;
    }

    public List<Country> findByNameContainingIgnoreCase(String nameFilter) {
        return repository.findByNameContainingIgnoreCase(".*(?i)" + nameFilter + ".*");
    }

    /**
     * Find Country by {@code numistaCode} with relationships
     * 
     * @param numistaCode Country's numistaCode
     * @return Country if exists, or null
     */
    public Country findCountryByNumistaCode(String numistaCode) {
        return repository.findByNumistaCode(numistaCode);
    }

    /**
     * Find Country Tree node with all children by {@code numistaCode} for Client
     * Catalogue Service (Tree Node Selection)
     * 
     * @param numistaCode Country's numistaCode
     * @return Country Tree node if exists, or null
     */
    public CSITreeNode findCSICountryTreeNodeByNumistaCode(String numistaCode) {
        Country country = repository.findByNumistaCodeWithSubjectsAndIssuers(numistaCode);
        if (country == null) {
            return null;
        }
        List<CSITreeNode> children = new ArrayList<>();
        if (country.getSubjects() != null && !country.getSubjects().isEmpty()) {
            children.addAll(country.getSubjects().stream()
                    .map(subject -> {
                        CSITreeNode subjectNode = modelMapper.map(subject, CSITreeNode.class);
                        subjectNode.setType("subject");
                        return subjectNode;
                    })
                    .collect(Collectors.toList()));
        }
        if (country.getIssuers() != null && !country.getIssuers().isEmpty()) {
            children.addAll(country.getIssuers().stream()
                    .map(issuer -> {
                        CSITreeNode issuerNode = modelMapper.map(issuer, CSITreeNode.class);
                        issuerNode.setType("issuer");
                        return issuerNode;
                    })
                    .collect(Collectors.toList()));
        }
        CSITreeNode countryNode = modelMapper.map(country, CSITreeNode.class);
        countryNode.setType("country");
        countryNode.setChildren(children);
        return countryNode;
    }

    public boolean connectToParentSubject(UUID countryUuid, UUID parentSubjectUuid) {
        return repository.connectToParentSubject(countryUuid.toString(), parentSubjectUuid.toString());
    }

    public List<CSIItem> findCSItemsByName(String filter) {
        List<CSIItem> csiItems = repository.findCSItemsByName(filter);
        if (csiItems != null && !csiItems.isEmpty()) {
            return csiItems.stream().map(t -> {
                return new CSIItem(CSIItem.Label.COUNTRY, t.getCode(), t.getName());
            }).collect(Collectors.toList());
        }
        return new ArrayList<CSIItem>();
    }

    public Integer countChildrenSubjects(String countryNumistaCode) {
        return repository.countChildrenSubjects(countryNumistaCode);
    }

    public Integer countChildrenIssuers(String countryNumistaCode) {
        return repository.countChildrenIssuers(countryNumistaCode);
    }

    public Integer countChildrenNTypes(String countryNumistaCode) {
        return repository.countChildrenNTypes(countryNumistaCode);
    }

    public Country getParentCountryBySubjectNumistaCode(String subjectNumistaCode) {
        return repository.getParentCountryBySubjectNumistaCode(subjectNumistaCode);
    }

    public Country getParentCountryByIssuerNumistaCode(String issuerNumistaCode) {
        return repository.getParentCountryByIssuerNumistaCode(issuerNumistaCode);
    }

}
