package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CountryService extends AbstractService<Country, CountryRepository> {
    public CountryService(CountryRepository repository) {
        super(repository);
    }

//    public Country findByName(String name){
//        return repository.findByName(name);
//    }

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
     * Find COUNTRY's UUID by COUNTRY's name. If not, create new COUNTRY with {@code name}
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

    /**
     * Check a Relationship between Country and Issuer
     *
     * @param countryUuid COUNTRY's UUID
     * @param issuerUuid  ISSUER's UUID
     * @return {@code true} If a relationship was presented, return {@code false} if there isn't a relationship
     */
    public Boolean hasContainsIssuerRelationshipToIssuer(UUID countryUuid, UUID issuerUuid) {
        return repository.hasSingleRelationshipToNode(countryUuid.toString(), issuerUuid.toString(), Country.CONTAINS_ISSUER);
    }

    /**
     * If there is no a relationship between COUNTRY (with UUID countryUuid) and ISSUER (with UUID issuerUuid), then create one with a label - CONTAINS_ISSUER.
     *
     * @param countryUuid       COUNTRY's UUID
     * @param issuerUuid ISSUER's UUID
     * @return {@code true} If a relationship was presented, or was created; {@code false} There was not a relationship, and it was not created
     */
    public Boolean setContainsIssuer(UUID countryUuid, UUID issuerUuid) {
        return repository.createSingleRelationshipToNode(countryUuid.toString(), issuerUuid.toString(), Country.CONTAINS_ISSUER);
    }

    public List<String> findAllCountriesNames() {
        return repository.findAllCountriesNames();
    }


    public List<Country> findByNameContainingIgnoreCase(String nameFilter) {
        return repository.findByNameContainingIgnoreCase(".*(?i)" + nameFilter + ".*");
    }

    /**
     * Find Country by {@code numistaCode}
     * @param numistaCode Country's numistaCode
     * @return Country if exists, or null
     */
    public Country findCountryByNumistaCode(String numistaCode){
        return repository.findCountryByNumistaCode(numistaCode);
    }



    public boolean connectToParentSubject(UUID countryUuid, UUID parentSubjectUuid){
        return repository.connectToParentSubject(countryUuid.toString(), parentSubjectUuid.toString());
    }


}
