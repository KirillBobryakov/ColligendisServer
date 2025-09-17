package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Currency;
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

    public void deleteAll() {
        repository.deleteAll();
    }

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
