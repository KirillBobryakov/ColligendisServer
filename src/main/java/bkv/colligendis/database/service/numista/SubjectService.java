package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubjectService extends AbstractService<Subject, SubjectRepository> {
    public SubjectService(SubjectRepository repository) {
        super(repository);
    }

    /**
     * Check a Relationship form (SUBJECT or COUNTRY) to (SUBJECT or child SUBJECT)
     * (:COUNTRY)-[:CONTAINS_CHILD_SUBJECT]->(:SUBJECT)
     * (:SUBJECT)-[:CONTAINS_CHILD_SUBJECT]->(:SUBJECT)
     *
     * @param subjectUuid COUNTRY's or SUBJECT's UUID
     * @param subjectUuid SUBJECT's UUID
     * @return {@code true} If a relationship was presented, return {@code false} if there isn't a relationship
     */
    public Boolean hasContainsChildSubjectToSubject(UUID fromNodeUuid, UUID subjectUuid) {
        return repository.hasSingleRelationshipToNode(fromNodeUuid.toString(), subjectUuid.toString(), Country.CONTAINS_CHILD_SUBJECT);
    }


    /**
     * If there is no a relationship between SUBJECT (with UUID subjectUuid) and child SUBJECT (with UUID childSubjectUuid), then create one with a label - CONTAINS_CHILD_SUBJECT.
     *
     * @param subjectUuid       SUBJECT's UUID
     * @param childSubjectUuid child SUBJECT's UUID
     * @return {@code true} If a relationship was presented, or was created; {@code false} There was not a relationship, and it was not created
     */
    public Boolean setContainsChildSubject(UUID subjectUuid, UUID childSubjectUuid) {
        return repository.createSingleRelationshipToNode(subjectUuid.toString(), childSubjectUuid.toString(), Country.CONTAINS_CHILD_SUBJECT);
    }

    /**
     * Find a Subject's UUID by unique field {@code name}
     * @param name unique Subject's field {@code name}
     * @return Subject's uuid if exists, or null
     */
    public UUID findSubjectUuidByName(String name){
        String uuid = repository.findSubjectUuidByName(name);
        return uuid != null ? UUID.fromString(uuid) : null;
    }


    /**
     * Find a Subject's uuid by unique filed {@code name}. If can't find, then create new one with {@code name}
     * @param name Subject's name
     * @return Subject's uuid
     */
    public UUID findSubjectUuidByNameOrCreate(String name){
        UUID subjectUuid = findSubjectUuidByName(name);
        if (subjectUuid == null) {
            DebugUtil.showInfo(this, "New Subject with name=" + name + " was created.");
            return repository.save(new Subject(name)).getUuid();
        }
        return subjectUuid;
    }



    public List<Subject> findByNameContainingIgnoreCase(String nameFilter){
        List<Subject> subjects = repository.findByNameContainingIgnoreCase(".*(?i)" + nameFilter + ".*");

        return subjects;
    }

    public List<Subject> findByCountryEid(String countryEid){
        return repository.findByCountryEid(countryEid);
    }

    public Subject findSubjectByNumistaCode(String numistaCode){
        return repository.findSubjectByNumistaCode(numistaCode);
    }

    /**
     * Check if subject exists by numista code
     * @param numistaCode
     * @return true if subject exists, false otherwise
     */
    public boolean isSubjectExists(String numistaCode){
        return repository.isSubjectExists(numistaCode);
    }

    public boolean connectToParentSubject(UUID subjectUuid, UUID parentSubjectUuid){
        return repository.connectToParentSubject(subjectUuid.toString(), parentSubjectUuid.toString());
    }

    public boolean connectToCountry(UUID subjectUuid, UUID countryUuid){
        return repository.connectToCountry(subjectUuid.toString(), countryUuid.toString());
    }


}
