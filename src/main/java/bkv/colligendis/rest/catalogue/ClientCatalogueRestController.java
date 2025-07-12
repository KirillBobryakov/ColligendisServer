package bkv.colligendis.rest.catalogue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.GradeType;
import bkv.colligendis.database.entity.numista.Item;
import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.Variant;
import bkv.colligendis.rest.ApiResponse;
import bkv.colligendis.rest.catalogue.csi_statistics.CSICurrency;
import bkv.colligendis.rest.catalogue.csi_statistics.CSIDenomination;
import bkv.colligendis.rest.catalogue.csi_statistics.CSIIssuer;
import bkv.colligendis.rest.catalogue.csi_statistics.CSIStatistics;
import bkv.colligendis.rest.catalogue.csi_statistics.CSISubject;
import bkv.colligendis.rest.catalogue.csi_statistics.CatalogueIssuerRequest;
import bkv.colligendis.rest.catalogue.csi_statistics.CatalogueItem;
import bkv.colligendis.rest.catalogue.csi_statistics.CatalogueNType;
import bkv.colligendis.rest.catalogue.csi_statistics.CatalogueNTypeVariant;
import bkv.colligendis.rest.catalogue.csi_statistics.CatalogueNTypesRequest;
import bkv.colligendis.rest.catalogue.csi_statistics.CatalogueSubjectRequest;
import bkv.colligendis.rest.catalogue.csi_statistics.CsiStatisticsRequest;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.PART_TYPE;
import bkv.colligendis.utils.numista.item.NumistaAllItemsParser;
import bkv.colligendis.utils.ImageUtil;

@RestController
public class ClientCatalogueRestController {

    @GetMapping(value = "/catalogue/csi/{filter}")
    public ResponseEntity<List<CSIItem>> getCatalogueOfCountry(@PathVariable(name = "filter") String filter) {

        List<CSIItem> countriesCSIList = N4JUtil.getInstance().numistaService.countryService
                .findCSItemsByName("(?i).*" + filter.toLowerCase() + ".*");
        List<CSIItem> subjectsCSIList = N4JUtil.getInstance().numistaService.subjectService
                .findCSItemsByName("(?i).*" + filter.toLowerCase() + ".*");
        List<CSIItem> issuersCSIList = N4JUtil.getInstance().numistaService.issuerService
                .findCSItemsByName("(?i).*" + filter.toLowerCase() + ".*");

        List<CSIItem> csiList = new ArrayList<>();
        csiList.addAll(countriesCSIList);
        csiList.addAll(subjectsCSIList);
        csiList.addAll(issuersCSIList);
        return ResponseEntity.ok(csiList);
    }

    @PostMapping(value = "/catalogue/csi/statistics", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CSIStatistics>> getCatalogueStatistics(
            @RequestBody CsiStatisticsRequest request) {
        CSIStatistics csiStatistics = new CSIStatistics();

        switch (request.getCsiType()) {
            case "COUNTRY":
                populateCurrenciesForCountry(csiStatistics, request.getCsiCode());
                populateCountryStatistics(csiStatistics, request.getCsiCode());
                break;
            case "SUBJECT":
                populateCurrenciesForSubject(csiStatistics, request.getCsiCode());
                populateSubjectStatistics(csiStatistics, request.getCsiCode());
                break;
            case "ISSUER":
                populateCurrenciesForIssuer(csiStatistics, request.getCsiCode());
                populateIssuerStatistics(csiStatistics, request.getCsiCode());
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new ApiResponse<>(csiStatistics, "Statistics fetched successfully",
                ApiResponse.Status.SUCCESS));
    }

    @GetMapping(value = "/catalogue/csi/statistics/{type}/{code}")
    public ResponseEntity<CSIStatistics> getCatalogueStatistics(@PathVariable(name = "type") String type,
            @PathVariable(name = "code") String numistaCode) {

        CSIStatistics csiStatistics = new CSIStatistics();

        switch (type) {
            case "COUNTRY":
                populateCurrenciesForCountry(csiStatistics, numistaCode);
                populateCountryStatistics(csiStatistics, numistaCode);
                break;
            case "SUBJECT":
                populateCurrenciesForSubject(csiStatistics, numistaCode);
                populateSubjectStatistics(csiStatistics, numistaCode);
                break;
            case "ISSUER":
                populateCurrenciesForIssuer(csiStatistics, numistaCode);
                populateIssuerStatistics(csiStatistics, numistaCode);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(csiStatistics);
    }

    @GetMapping(value = "/catalogue/items/variant/{variantNid}")
    public ResponseEntity<ApiResponse<List<CatalogueItem>>> getItemsByVariantNid(
            @PathVariable(name = "variantNid") String variantNid) {
        List<Item> items = N4JUtil.getInstance().numistaService.itemService.findItemsByVariantNid(variantNid);
        List<CatalogueItem> catalogueItems = new ArrayList<>();
        for (Item item : items) {

            catalogueItems.add(new CatalogueItem(item.getUuid().toString(), item.getCreatedAt(),
                    item.getQuantity(), item.getGradeType().toString(), item.getPriceValue(),
                    item.getPriceCurrency(), item.getAcquisitionDate(), variantNid));
        }
        return ResponseEntity.ok(new ApiResponse<>(catalogueItems, "Items fetched successfully",
                ApiResponse.Status.SUCCESS));
    }

    @PostMapping(value = "/catalogue/items/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Boolean>> saveItems(
            @RequestBody CatalogueItem catalogueItem) {

        Item item = new Item();
        item.setQuantity(catalogueItem.getQuantity());

        item.setCreatedAt(catalogueItem.getCreatedAt());
        item.setGradeType(GradeType.valueOf(catalogueItem.getGradeType()));
        item.setPriceValue(catalogueItem.getPriceValue());
        item.setPriceCurrency(catalogueItem.getPriceCurrency());
        item.setAcquisitionDate(catalogueItem.getAcquisitionDate());
        item.setVariant(N4JUtil.getInstance().numistaService.variantService.findByNid(catalogueItem.getVariantNid()));

        item = N4JUtil.getInstance().numistaService.itemService.save(item);
        if (item.getUuid() == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Item not saved", ApiResponse.Status.ERROR));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Items saved successfully", ApiResponse.Status.SUCCESS));
    }

    @DeleteMapping(value = "/catalogue/items/delete/{uuid}")
    public ResponseEntity<ApiResponse<Boolean>> deleteItem(@PathVariable(name = "uuid") String uuid) {
        try {
            N4JUtil.getInstance().numistaService.itemService.delete(uuid);
            return ResponseEntity.ok(new ApiResponse<>(true, "Item deleted successfully", ApiResponse.Status.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Item not deleted", ApiResponse.Status.ERROR));
        }
    }

    @PostMapping(value = "/catalogue/ntypes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<CatalogueNType>>> getCatalogueNTypes(
            @RequestBody CatalogueNTypesRequest request) {

        List<CatalogueNType> catalogueNTypes = new ArrayList<>();
        List<NType> nTypesList = new ArrayList<>();
        switch (request.getCsiType()) {
            case "COUNTRY": {
                nTypesList = N4JUtil.getInstance().numistaService.nTypeService
                        .findNTypesByCountryNumistaCodeWithFilters(request.getCsiCode(), request.getDenominationNid(),
                                request.getIssuerCode(), request.getSubjectNumistaCode(),
                                request.getCollectibleTypeCode(), request.getTextFilter());
                break;
            }
            case "SUBJECT":
                nTypesList = N4JUtil.getInstance().numistaService.nTypeService
                        .findNTypesBySubjectNumistaCodeWithFilters(request.getCsiCode(), request.getDenominationNid(),
                                request.getIssuerCode(), request.getSubjectNumistaCode(),
                                request.getCollectibleTypeCode());
                break;
            case "ISSUER":
                nTypesList = N4JUtil.getInstance().numistaService.nTypeService
                        .findNTypesByIssuerCodeWithFilter(request.getCsiCode(), request.getCollectibleTypeCode());
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        nTypesList = nTypesList.stream().distinct().collect(Collectors.toList());

        for (NType nType : nTypesList) {
            CollectibleType collectibleTypeParent = nType.getCollectibleType();
            while (collectibleTypeParent.getCollectibleTypeParent() != null) {
                collectibleTypeParent = collectibleTypeParent.getCollectibleTypeParent();
            }

            CatalogueNType catalogueNType = new CatalogueNType(nType.getNid(), nType.getTitle(),
                    nType.getCollectibleType().getName(),
                    collectibleTypeParent.getName(),
                    new CSIIssuer(nType.getIssuer().getName(), nType.getIssuer().getCode()));

            List<Variant> variants = N4JUtil.getInstance().numistaService.variantService
                    .getVariantsByNTypeNid(nType.getNid());

            List<CatalogueNTypeVariant> catalogueNTypeVariants = new ArrayList<>();

            for (Variant variant : variants) {

                Integer itemsCount = N4JUtil.getInstance().numistaService.itemService
                        .countItemsByVariantNid(variant.getNid());

                catalogueNTypeVariants.add(new CatalogueNTypeVariant(variant.getNid(), variant.getComment(),
                        variant.getYear() != null ? variant.getYear().getValue() : null,
                        variant.getYearFrom() != null ? variant.getYearFrom().getValue() : null, itemsCount));
            }

            catalogueNType.setVariants(catalogueNTypeVariants);

            catalogueNTypes.add(catalogueNType);
        }

        return ResponseEntity.ok(new ApiResponse<>(catalogueNTypes, "N-types fetched successfully",
                ApiResponse.Status.SUCCESS));
    }

    @PostMapping(value = "/catalogue/subjects/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<CSISubject>>> getCatalogueSubjects(
            @RequestBody CatalogueSubjectRequest request) {
        if (request.getCsiType() == null || request.getCsiCode() == null) {
            return ResponseEntity.badRequest().build();
        }

        List<CSISubject> catalogueSubjects = new ArrayList<>();
        switch (request.getCsiType()) {
            case "COUNTRY":
                catalogueSubjects = N4JUtil.getInstance().numistaService.subjectService
                        .findChildrenSubjectsByCountryNumistaCode(request.getCsiCode()).stream()
                        .map((subject) -> new CSISubject(subject.getName(), subject.getNumistaCode()))
                        .collect(Collectors.toList());
                break;
            case "SUBJECT":
                catalogueSubjects = N4JUtil.getInstance().numistaService.subjectService
                        .findChildrenSubjectsBySubjectNumistaCode(request.getCsiCode()).stream()
                        .map((subject) -> new CSISubject(subject.getName(), subject.getNumistaCode()))
                        .collect(Collectors.toList());
                break;
            case "ISSUER":
                return ResponseEntity.noContent().build();
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new ApiResponse<>(
                catalogueSubjects, "Subjects fetched successfully",
                ApiResponse.Status.SUCCESS));
    }

    @PostMapping(value = "/catalogue/parse-numista", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Boolean>> parseNumista(
            @RequestBody ParsingNumistaData request) {

        if (request.getIssuerCode() == null || request.getTopCollectableType() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false, "Invalid request, null values",
                    ApiResponse.Status.ERROR));
        }
        if (request.getIssuerCode().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false, "Invalid request, empty Issuer code",
                    ApiResponse.Status.ERROR));
        }
        if (request.getTopCollectableType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false, "Invalid request, empty Top collectable type",
                    ApiResponse.Status.ERROR));
        }
        try {
            NumistaAllItemsParser numistaAllItemsParser = new NumistaAllItemsParser();
            numistaAllItemsParser.fetchAndProcessCatalog(request.getIssuerCode(),
                    request.getTopCollectableType());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>(
                    false, "Error parsing numista data",
                    ApiResponse.Status.ERROR));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Parsing numista data completed",
                ApiResponse.Status.SUCCESS));
    }

    @PostMapping(value = "/catalogue/issuers/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<CSIIssuer>>> getCatalogueIssuers(
            @RequestBody CatalogueIssuerRequest request) {
        if (request.getCsiType() == null || request.getCsiCode() == null) {
            return ResponseEntity.badRequest().build();
        }

        List<CSIIssuer> catalogueIssuers = new ArrayList<>();
        switch (request.getCsiType()) {
            case "COUNTRY":
                catalogueIssuers = N4JUtil.getInstance().numistaService.issuerService
                        .findChildrenIssuersByCountryNumistaCode(request.getCsiCode()).stream()
                        .map((issuer) -> new CSIIssuer(issuer.getName(), issuer.getCode()))
                        .collect(Collectors.toList());
                break;
            case "SUBJECT":
                catalogueIssuers = N4JUtil.getInstance().numistaService.issuerService
                        .findChildrenIssuersBySubjectNumistaCode(request.getCsiCode()).stream()
                        .map((issuer) -> new CSIIssuer(issuer.getName(), issuer.getCode()))
                        .collect(Collectors.toList());
                break;
            case "ISSUER":
                return ResponseEntity.noContent().build();
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new ApiResponse<>(
                catalogueIssuers, "Issuers fetched successfully",
                ApiResponse.Status.SUCCESS));
    }

    @GetMapping(value = "/catalogue/image/{nid}/{partSide}")
    public ResponseEntity<Resource> getLocalImage(
            @PathVariable(name = "nid") String nid,
            @PathVariable(name = "partSide") String partSide) {
        try {

            NType nType = N4JUtil.getInstance().numistaService.nTypeService.findByNid(nid);

            Resource resource = ImageUtil.getNTypeLocalImage(nType, PART_TYPE.valueOf(partSide.toUpperCase()));

            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type based on file extension
            String contentType = determineContentType(resource.getFilename());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(resource.contentLength());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String determineContentType(String imagePath) {
        String lowerCasePath = imagePath.toLowerCase();
        if (lowerCasePath.endsWith(".jpg") || lowerCasePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerCasePath.endsWith(".png")) {
            return "image/png";
        } else if (lowerCasePath.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerCasePath.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerCasePath.endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            // Default to octet-stream for unknown types
            return "application/octet-stream";
        }
    }

    private void populateCurrenciesForCountry(CSIStatistics csiStatistics, String numistaCode) {
        List<Currency> currencies = N4JUtil.getInstance().numistaService.currencyService
                .findCurrenciesByCountryNumistaCode(numistaCode);
        populateCurrencies(csiStatistics, currencies);
    }

    private void populateCurrenciesForSubject(CSIStatistics csiStatistics, String numistaCode) {
        List<Currency> currencies = N4JUtil.getInstance().numistaService.currencyService
                .findCurrenciesBySubjectNumistaCode(numistaCode);
        populateCurrencies(csiStatistics, currencies);
    }

    private void populateCurrenciesForIssuer(CSIStatistics csiStatistics, String numistaCode) {
        List<Currency> currencies = N4JUtil.getInstance().numistaService.currencyService
                .findCurrenciesByIssuerCode(numistaCode);
        populateCurrencies(csiStatistics, currencies);
    }

    private void populateCurrencies(CSIStatistics csiStatistics, List<Currency> currencies) {
        for (Currency currency : currencies) {
            CSICurrency csiCurrency = new CSICurrency(currency.getFullName(), currency.getNid());
            List<CSIDenomination> denominations = N4JUtil.getInstance().numistaService.denominationService
                    .findDenominationsByCurrencyUuid(currency.getUuid()).stream()
                    .map(denomination -> new CSIDenomination(denomination.getName(),
                            denomination.getNid()))
                    .collect(Collectors.toList());

            csiCurrency.setDenominations(denominations);
            csiStatistics.getCurrencies().add(csiCurrency);
        }
    }

    private void populateCountryStatistics(CSIStatistics csiStatistics, String numistaCode) {
        csiStatistics.setTotalSubjects(N4JUtil.getInstance().numistaService.countryService
                .countChildrenSubjects(numistaCode));
        // csiStatistics.setSubjects(N4JUtil.getInstance().numistaService.subjectService
        // .findChildrenSubjectsByCountryNumistaCode(numistaCode).stream().map((subject)
        // -> {
        // return new CSISubject(subject.getName(), subject.getNumistaCode());
        // }).collect(Collectors.toList()));
        csiStatistics.setTotalIssuers(N4JUtil.getInstance().numistaService.countryService
                .countChildrenIssuers(numistaCode));
        // csiStatistics.setIssuers(N4JUtil.getInstance().numistaService.issuerService
        // .findChildrenIssuersByCountryNumistaCode(numistaCode).stream().map((issuer)
        // -> {
        // return new CSIIssuer(issuer.getName(), issuer.getCode());
        // }).collect(Collectors.toList()));
        csiStatistics.setTotalNTypes(N4JUtil.getInstance().numistaService.countryService
                .countChildrenNTypes(numistaCode));
    }

    private void populateSubjectStatistics(CSIStatistics csiStatistics, String numistaCode) {
        Country parentCountry = N4JUtil.getInstance().numistaService.countryService
                .getParentCountryBySubjectNumistaCode(numistaCode);
        csiStatistics.setCountry(parentCountry.getName(), parentCountry.getNumistaCode());

        csiStatistics.setTotalSubjects(N4JUtil.getInstance().numistaService.subjectService
                .countChildrenSubjects(numistaCode));
        // csiStatistics.setSubjects(N4JUtil.getInstance().numistaService.subjectService
        // .findChildrenSubjectsBySubjectNumistaCode(numistaCode).stream().map((subject)
        // -> {
        // return new CSISubject(subject.getName(), subject.getNumistaCode());
        // }).collect(Collectors.toList()));
        csiStatistics.setTotalIssuers(N4JUtil.getInstance().numistaService.subjectService
                .countChildrenIssuers(numistaCode));
        // csiStatistics.setIssuers(N4JUtil.getInstance().numistaService.issuerService
        // .findChildrenIssuersBySubjectNumistaCode(numistaCode).stream().map((issuer)
        // -> {
        // return new CSIIssuer(issuer.getName(), issuer.getCode());
        // }).collect(Collectors.toList()));
        csiStatistics.setTotalNTypes(N4JUtil.getInstance().numistaService.subjectService
                .countChildrenNTypes(numistaCode));
    }

    private void populateIssuerStatistics(CSIStatistics csiStatistics, String numistaCode) {
        Country parentCountry = N4JUtil.getInstance().numistaService.countryService
                .getParentCountryByIssuerNumistaCode(numistaCode);
        csiStatistics.setCountry(parentCountry.getName(), parentCountry.getNumistaCode());

        csiStatistics.setTotalNTypes(N4JUtil.getInstance().numistaService.issuerService
                .countChildrenNTypes(numistaCode));
    }

}
