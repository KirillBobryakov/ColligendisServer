package bkv.colligendis.rest.statistics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.rest.ApiResponse;
import bkv.colligendis.utils.N4JUtil;

@RestController
public class StatisticsRestController {

    @GetMapping(value = "/statistics/ntypes/country/{countryNumistaCode}")
    public ResponseEntity<ApiResponse<NTypesStatistics>> getNumistaCountries(
            @PathVariable(name = "countryNumistaCode") String countryNumistaCode) {

        NTypesStatistics itemsStatistics = new NTypesStatistics();

        itemsStatistics.setCoinsNTypesCount(N4JUtil.getInstance().numistaService.nTypeService
                .countNTypesByCountryNumistaCodeAndCollectibleTypeCode(countryNumistaCode, CollectibleType.COINS_CODE));
        itemsStatistics.setCoinsVariantsCount(N4JUtil.getInstance().numistaService.variantService
                .countVariantsByCountryNumistaCodeAndCollectibleTypeCode(countryNumistaCode,
                        CollectibleType.COINS_CODE));
        itemsStatistics.setBanknotesNTypesCount(N4JUtil.getInstance().numistaService.nTypeService
                .countNTypesByCountryNumistaCodeAndCollectibleTypeCode(countryNumistaCode,
                        CollectibleType.BANKNOTES_CODE));
        itemsStatistics.setBanknotesVariantsCount(N4JUtil.getInstance().numistaService.variantService
                .countVariantsByCountryNumistaCodeAndCollectibleTypeCode(countryNumistaCode,
                        CollectibleType.BANKNOTES_CODE));

        return ResponseEntity
                .ok(new ApiResponse<>(itemsStatistics, "Statistics fetched successfully", ApiResponse.Status.SUCCESS));
    }

    @GetMapping(value = "/statistics/ntypes/subject/{subjectNumistaCode}")
    public ResponseEntity<ApiResponse<NTypesStatistics>> getNumistaSubjects(
            @PathVariable(name = "subjectNumistaCode") String subjectNumistaCode) {

        NTypesStatistics itemsStatistics = new NTypesStatistics();

        itemsStatistics.setCoinsNTypesCount(N4JUtil.getInstance().numistaService.nTypeService
                .countNTypesBySubjectNumistaCodeAndCollectibleTypeCode(subjectNumistaCode, CollectibleType.COINS_CODE));
        itemsStatistics.setCoinsVariantsCount(N4JUtil.getInstance().numistaService.variantService
                .countVariantsBySubjectNumistaCodeAndCollectibleTypeCode(subjectNumistaCode,
                        CollectibleType.COINS_CODE));
        itemsStatistics.setBanknotesNTypesCount(N4JUtil.getInstance().numistaService.nTypeService
                .countNTypesBySubjectNumistaCodeAndCollectibleTypeCode(subjectNumistaCode,
                        CollectibleType.BANKNOTES_CODE));
        itemsStatistics.setBanknotesVariantsCount(N4JUtil.getInstance().numistaService.variantService
                .countVariantsBySubjectNumistaCodeAndCollectibleTypeCode(subjectNumistaCode,
                        CollectibleType.BANKNOTES_CODE));

        return ResponseEntity
                .ok(new ApiResponse<>(itemsStatistics, "Statistics fetched successfully", ApiResponse.Status.SUCCESS));
    }

    @GetMapping(value = "/statistics/ntypes/issuer/{issuerCode}")
    public ResponseEntity<ApiResponse<NTypesStatistics>> getNumistaIssuers(
            @PathVariable(name = "issuerCode") String issuerCode) {

        NTypesStatistics itemsStatistics = new NTypesStatistics();

        itemsStatistics.setCoinsNTypesCount(N4JUtil.getInstance().numistaService.nTypeService
                .countNTypesByIssuerCodeAndCollectibleTypeCode(issuerCode, CollectibleType.COINS_CODE));
        itemsStatistics.setCoinsVariantsCount(N4JUtil.getInstance().numistaService.variantService
                .countVariantsByIssuerCodeAndCollectibleTypeCode(issuerCode,
                        CollectibleType.COINS_CODE));
        itemsStatistics.setBanknotesNTypesCount(N4JUtil.getInstance().numistaService.nTypeService
                .countNTypesByIssuerCodeAndCollectibleTypeCode(issuerCode,
                        CollectibleType.BANKNOTES_CODE));
        itemsStatistics.setBanknotesVariantsCount(N4JUtil.getInstance().numistaService.variantService
                .countVariantsByIssuerCodeAndCollectibleTypeCode(issuerCode,
                        CollectibleType.BANKNOTES_CODE));

        return ResponseEntity
                .ok(new ApiResponse<>(itemsStatistics, "Statistics fetched successfully", ApiResponse.Status.SUCCESS));
    }
}
