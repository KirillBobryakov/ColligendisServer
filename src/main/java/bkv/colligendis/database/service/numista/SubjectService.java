package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.rest.catalogue.CSIItem;
import bkv.colligendis.rest.catalogue.csi_statistics.CSITreeNode;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubjectService extends AbstractService<Subject, SubjectRepository> {

    private final ModelMapper modelMapper;

    public SubjectService(SubjectRepository repository, ModelMapper modelMapper) {
        super(repository);
        this.modelMapper = modelMapper;
    }

    // /**
    // * Check a Relationship form (SUBJECT or COUNTRY) to (SUBJECT or child
    // SUBJECT)
    // * (:COUNTRY)-[:CONTAINS_CHILD_SUBJECT]->(:SUBJECT)
    // * (:SUBJECT)-[:CONTAINS_CHILD_SUBJECT]->(:SUBJECT)
    // *
    // * @param subjectUuid COUNTRY's or SUBJECT's UUID
    // * @param subjectUuid SUBJECT's UUID
    // * @return {@code true} If a relationship was presented, return {@code false}
    // if
    // * there isn't a relationship
    // */
    // public Boolean hasContainsChildSubjectToSubject(UUID fromNodeUuid, UUID
    // subjectUuid) {
    // return repository.hasSingleRelationshipToNode(fromNodeUuid.toString(),
    // subjectUuid.toString(),
    // Country.CONTAINS_CHILD_SUBJECT);
    // }

    // /**
    // * If there is no a relationship between SUBJECT (with UUID subjectUuid) and
    // * child SUBJECT (with UUID childSubjectUuid), then create one with a label -
    // * CONTAINS_CHILD_SUBJECT.
    // *
    // * @param subjectUuid SUBJECT's UUID
    // * @param childSubjectUuid child SUBJECT's UUID
    // * @return {@code true} If a relationship was presented, or was created;
    // * {@code false} There was not a relationship, and it was not created
    // */
    // public Boolean setContainsChildSubject(UUID subjectUuid, UUID
    // childSubjectUuid) {
    // return repository.createSingleRelationshipToNode(subjectUuid.toString(),
    // childSubjectUuid.toString(),
    // Country.CONTAINS_CHILD_SUBJECT);
    // }

    /**
     * Find a Subject's UUID by unique field {@code name}
     * 
     * @param name unique Subject's field {@code name}
     * @return Subject's uuid if exists, or null
     */
    public UUID findSubjectUuidByName(String name) {
        String uuid = repository.findSubjectUuidByName(name);
        return uuid != null ? UUID.fromString(uuid) : null;
    }

    /**
     * Find a Subject's uuid by unique filed {@code name}. If can't find, then
     * create new one with {@code name}
     * 
     * @param name Subject's name
     * @return Subject's uuid
     */
    public UUID findSubjectUuidByNameOrCreate(String name) {
        UUID subjectUuid = findSubjectUuidByName(name);
        if (subjectUuid == null) {
            DebugUtil.showInfo(this, "New Subject with name=" + name + " was created.");
            return repository.save(new Subject(name)).getUuid();
        }
        return subjectUuid;
    }

    public CSITreeNode findCSISubjectTreeNodeByNumistaCode(String numistaCode) {
        Subject subject = repository.findByNumistaCodeWithSubjectsAndIssuers(numistaCode);
        if (subject == null) {
            return null;
        }

        List<CSITreeNode> children = new ArrayList<>();
        if (subject.getChildSubjects() != null && !subject.getChildSubjects().isEmpty()) {
            children.addAll(subject.getChildSubjects().stream()
                    .map(childSubject -> {
                        CSITreeNode childSubjectNode = modelMapper.map(childSubject, CSITreeNode.class);
                        childSubjectNode.setType("subject");
                        return childSubjectNode;
                    })
                    .collect(Collectors.toList()));
        }

        if (subject.getIssuers() != null && !subject.getIssuers().isEmpty()) {
            children.addAll(subject.getIssuers().stream()
                    .map(issuer -> {
                        CSITreeNode issuerNode = modelMapper.map(issuer, CSITreeNode.class);
                        issuerNode.setType("issuer");
                        return issuerNode;
                    })
                    .collect(Collectors.toList()));
        }

        CSITreeNode subjectNode = modelMapper.map(subject, CSITreeNode.class);
        subjectNode.setType("subject");
        subjectNode.setChildren(children);
        return subjectNode;
    }

    public List<Subject> findByNameContainingIgnoreCase(String nameFilter) {
        List<Subject> subjects = repository.findByNameContainingIgnoreCase(".*(?i)" + nameFilter + ".*");

        return subjects;
    }

    public List<Subject> findByCountryNumistaCode(String countryNumistaCode) {
        return repository.findByCountryNumistaCode(countryNumistaCode);
    }

    public Subject findSubjectByNumistaCode(String numistaCode) {
        return repository.findSubjectByNumistaCode(numistaCode);
    }

    /**
     * Check if subject exists by numista code
     * 
     * @param numistaCode
     * @return true if subject exists, false otherwise
     */
    public boolean isSubjectExists(String numistaCode) {
        return repository.isSubjectExists(numistaCode);
    }

    public boolean connectToParentSubject(UUID subjectUuid, UUID parentSubjectUuid) {
        return repository.connectToParentSubject(subjectUuid.toString(), parentSubjectUuid.toString());
    }

    public boolean connectToCountry(UUID subjectUuid, UUID countryUuid) {
        return repository.connectToCountry(subjectUuid.toString(), countryUuid.toString());
    }

    public List<CSIItem> findCSItemsByName(String filter) {
        List<CSIItem> csiItems = repository.findCSItemsByName(filter);
        if (csiItems != null && !csiItems.isEmpty()) {
            return csiItems.stream().map(t -> {
                return new CSIItem(CSIItem.Label.SUBJECT, t.getCode(), t.getName());
            }).collect(Collectors.toList());
        }
        return new ArrayList<CSIItem>();
    }

    public Integer countChildrenSubjects(String subjectNumistaCode) {
        return repository.countChildrenSubjects(subjectNumistaCode);
    }

    public List<Subject> findChildrenSubjectsBySubjectNumistaCode(String subjectNumistaCode) {
        return repository.findChildrenSubjectsBySubjectNumistaCode(subjectNumistaCode);
    }

    public Integer countChildrenIssuers(String subjectNumistaCode) {
        return repository.countChildrenIssuers(subjectNumistaCode);
    }

    public Integer countChildrenNTypes(String subjectNumistaCode) {
        return repository.countChildrenNTypes(subjectNumistaCode);
    }

    public List<Subject> findChildrenSubjectsByCountryNumistaCode(String countryNumistaCode) {
        return repository.findChildrenSubjectsByCountryNumistaCode(countryNumistaCode);
    }

    public List<CSITreeNode> findByNameFilter(String nameFilter) {
        List<Subject> subjects = repository.findByNameFilter(nameFilter);

        return subjects.stream()
                .map(subject -> convertToCSITreeNodes(subject))
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

}
