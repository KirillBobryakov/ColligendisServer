package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.Denomination;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.rest.dto.CurrencyDTO;
import bkv.colligendis.services.AbstractService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class CurrencyService extends AbstractService<Currency, CurrencyRepository> {

    private final ModelMapper modelMapper;

    public CurrencyService(CurrencyRepository repository, ModelMapper modelMapper) {
        super(repository);
        this.modelMapper = modelMapper;
    }

    // Start: Methods for Numista parsing

    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Currency.LABEL, "nid", nid);
    }

    public void detachIssuer(UUID currencyUuid, UUID issuerUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(currencyUuid, issuerUuid,
                Currency.CIRCULATE_WHEN_BEEN);
    }

    public boolean setFullName(UUID currencyUuid, String fullName) {
        return setPropertyStringValue(currencyUuid, "fullName", fullName);
    }

    public boolean setKind(UUID currencyUuid, String kind) {
        return setPropertyStringValue(currencyUuid, "kind", kind);
    }

    public boolean setName(UUID currencyUuid, String name) {
        return setPropertyStringValue(currencyUuid, "name", name);
    }

    public String getNid(UUID currencyUuid) {
        return getPropertyValue(currencyUuid, "nid", String.class);
    }

    public void detachRulesFromYears(UUID rulerUuid) {
        repository.detachAllOutgoingRelationshipsWithRelationshipTypeAndSecondEntityLabel(rulerUuid.toString(),
                Currency.CIRCULATED_FROM, Year.LABEL);
    }

    public void detachRulesTillYears(UUID rulerUuid) {
        repository.detachAllOutgoingRelationshipsWithRelationshipTypeAndSecondEntityLabel(rulerUuid.toString(),
                Currency.CIRCULATED_TILL, Year.LABEL);
    }

    public void addRuleFromYear(UUID rulerUuid, UUID yearUuid) {
        addSingleOutgoingRelationshipToNode(rulerUuid, yearUuid, Currency.CIRCULATED_FROM);
    }

    public void addRuleTillYear(UUID rulerUuid, UUID yearUuid) {
        addSingleOutgoingRelationshipToNode(rulerUuid, yearUuid, Currency.CIRCULATED_TILL);
    }

    public void setIssuer(UUID currencyUuid, UUID issuerUuid) {
        setSingleOutgoingRelationshipToNode(currencyUuid, issuerUuid, Currency.CIRCULATE_WHEN_BEEN, Issuer.LABEL);
    }

    public boolean setIsActual(UUID currencyUuid, Boolean isActual) {
        return setPropertyBooleanValue(currencyUuid, "isActual", isActual);
    }

    public List<UUID> getDenominations(UUID currencyUuid) {
        return getAllIncomingRelatedNodesUUIDs(currencyUuid, Denomination.UNDER_CURRENCY, Denomination.LABEL);
    }

    // End: Methods for Numista parsing

    public Currency findByName(String name) {
        return repository.findByName(name);
    }

    /**
     * If there is no a relationship between CURRENCY (with UUID currencyUuid) and
     * DENOMINATION (with UUID denominationUuid), then create one with a label -
     * HAS_DENOMINATION.
     * 
     * @param currencyUuid     CURRENCY's UUID
     * @param denominationUuid DENOMINATION's UUID
     * 
     * @return {@code true} If a relationship was presented, or was created;
     *         {@code false} There was not a relationship, and it was not created
     */
    public Boolean setHasDenomination(UUID currencyUuid, UUID denominationUuid) {
        // return repository.createSingleRelationshipToNode(currencyUuid.toString(),
        // denominationUuid.toString(), Currency.HAS_DENOMINATION);
        return null;
    }

    /**
     * Check a Relationship between CURRENCY and DENOMINATION
     * 
     * @param currencyUuid     CURRENCY's UUID
     * @param denominationUuid DENOMINATION's UUID
     * @return {@code true} If a relationship was presented, return {@code false} if
     *         there isn't a relationship
     */
    public Boolean hasSingleRelationshipToNode(UUID currencyUuid, UUID denominationUuid) {
        // return repository.hasSingleRelationshipToNode(currencyUuid.toString(),
        // denominationUuid.toString(), Currency.HAS_DENOMINATION);
        return null;
    }

    /**
     * Find Currency's nid by Currency's uuid
     * 
     * @param uuid Currency's uuid
     * @return Currency's nid
     */
    public String findCurrencyNidByUuid(UUID uuid) {
        return repository.findCurrencyNidByUuid(uuid.toString());
    }

    /**
     * Find Currency's uuid by Currency's nid
     * 
     * @param nid Currency's nid
     * @return Currency's uuid
     */
    public Currency findCurrencyByNid(String nid) {
        return repository.findCurrencyByNid(nid);
    }

    public List<Currency> findCurrencyByIssuer(Issuer issuer) {
        return repository.findByIssuerCode(issuer.getCode());
    }

    public List<Currency> findCurrenciesByCountryNumistaCode(String numistaCode) {

        List<String> currencyUuids = repository.findCurrenciesUuidsByCountryNumistaCode(numistaCode);
        return currencyUuids.stream().map(uuid -> repository.findByUuid(uuid)).collect(Collectors.toList());
    }

    public List<CurrencyDTO> findCurrenciesDTOByCountryNumistaCode(String countryNumistaCode) {
        List<Currency> currencies = repository.findCurrenciesByCountryNumistaCode(countryNumistaCode);
        return currencies.stream()
                .map(currency -> modelMapper.map(currency, CurrencyDTO.class))
                .collect(Collectors.toList());
    }

    public List<Currency> findCurrenciesBySubjectNumistaCode(String numistaCode) {
        List<String> currencyUuids = repository.findCurrenciesUuidsBySubjectNumistaCode(numistaCode);
        return currencyUuids.stream().map(uuid -> repository.findByUuid(uuid)).collect(Collectors.toList());
    }

    public List<CurrencyDTO> findCurrenciesDTOBySubjectNumistaCode(String subjectNumistaCode) {
        List<Currency> currencies = repository.findCurrenciesBySubjectNumistaCode(subjectNumistaCode);
        return currencies.stream()
                .map(currency -> modelMapper.map(currency, CurrencyDTO.class))
                .collect(Collectors.toList());
    }

    public List<Currency> findCurrenciesByIssuerCode(String code) {
        List<String> currencyUuids = repository.findCurrenciesUuidsByIssuerCode(code);
        return currencyUuids.stream().map(uuid -> repository.findByUuid(uuid)).collect(Collectors.toList());
    }

    public List<CurrencyDTO> findCurrenciesDTOByIssuerCode(String code) {
        List<Currency> currencies = repository.findCurrenciesByIssuerCode(code);
        return currencies.stream()
                .map(currency -> modelMapper.map(currency, CurrencyDTO.class))
                .collect(Collectors.toList());
    }

}
