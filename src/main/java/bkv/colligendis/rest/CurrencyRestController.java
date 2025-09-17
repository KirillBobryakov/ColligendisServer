package bkv.colligendis.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.rest.dto.CurrencyDTO;
import bkv.colligendis.utils.N4JUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/database/currency")
public class CurrencyRestController {

    @GetMapping(value = "/byCountry")
    public ResponseEntity<ApiResponse<List<CurrencyDTO>>> getCurrenciesByCountryNumistaCode(
            @RequestParam(name = "countryNumistaCode") String countryNumistaCode) {
        List<CurrencyDTO> currencies = N4JUtil.getInstance().numistaService.currencyService
                .findCurrenciesDTOByCountryNumistaCode(countryNumistaCode);
        return ResponseEntity
                .ok(new ApiResponse<>(currencies, "Currencies fetched successfully", ApiResponse.Status.SUCCESS));
    }

    @GetMapping(value = "/bySubject")
    public ResponseEntity<ApiResponse<List<CurrencyDTO>>> getCurrenciesBySubjectNumistaCode(
            @RequestParam(name = "subjectNumistaCode") String subjectNumistaCode) {
        List<CurrencyDTO> currencies = N4JUtil.getInstance().numistaService.currencyService
                .findCurrenciesDTOBySubjectNumistaCode(subjectNumistaCode);
        return ResponseEntity
                .ok(new ApiResponse<>(currencies, "Currencies fetched successfully", ApiResponse.Status.SUCCESS));
    }

    @GetMapping(value = "/byIssuer")
    public ResponseEntity<ApiResponse<List<CurrencyDTO>>> getCurrenciesByIssuerCode(
            @RequestParam(name = "issuerCode") String issuerCode) {
        List<CurrencyDTO> currencies = N4JUtil.getInstance().numistaService.currencyService
                .findCurrenciesDTOByIssuerCode(issuerCode);
        return ResponseEntity
                .ok(new ApiResponse<>(currencies, "Currencies fetched successfully", ApiResponse.Status.SUCCESS));
    }
}
