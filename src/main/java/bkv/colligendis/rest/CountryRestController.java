package bkv.colligendis.rest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.rest.dto.CountryDTO;
import bkv.colligendis.utils.N4JUtil;

@RestController
@RequestMapping("/database/country")
public class CountryRestController {

    @GetMapping(value = "/all")
    public ResponseEntity<ApiResponse<List<CountryDTO>>> getAllCountries() {
        try {
            List<CountryDTO> countries = N4JUtil.getInstance().numistaService.countryService
                    .findAllCountriesWithoutRelationships().stream()
                    .sorted(Comparator.comparing(CountryDTO::getName))
                    .collect(Collectors.toList());
            return ResponseEntity
                    .ok(new ApiResponse<>(countries, "Countries fetched successfully", ApiResponse.Status.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new ApiResponse<>(null, "Error fetching countries", ApiResponse.Status.ERROR));
        }

    }

    @GetMapping(value = "/addRuAlternativeName/{numistaCode}/{ruAlternativeName}")
    public ResponseEntity<ApiResponse<Boolean>> addRuAlternativeName(
            @PathVariable(name = "numistaCode", required = true) String numistaCode,
            @PathVariable(name = "ruAlternativeName", required = true) String ruAlternativeName) {

        Country country = N4JUtil.getInstance().numistaService.countryService.findCountryByNumistaCode(numistaCode);
        country.addRuAlternativeName(ruAlternativeName);
        N4JUtil.getInstance().numistaService.countryService.save(country);
        return ResponseEntity.ok(new ApiResponse<>(true, "Ru alternative name added successfully",
                ApiResponse.Status.SUCCESS));
    }

    @DeleteMapping(value = "/deleteRuAlternativeName/{numistaCode}/{ruAlternativeName}")
    public ResponseEntity<ApiResponse<Boolean>> deleteRuAlternativeName(
            @PathVariable(name = "numistaCode", required = true) String numistaCode,
            @PathVariable(name = "ruAlternativeName", required = true) String ruAlternativeName) {
        Country country = N4JUtil.getInstance().numistaService.countryService.findCountryByNumistaCode(numistaCode);
        country.removeRuAlternativeName(ruAlternativeName);
        N4JUtil.getInstance().numistaService.countryService.save(country);
        return ResponseEntity.ok(new ApiResponse<>(true, "Ru alternative name deleted successfully",
                ApiResponse.Status.SUCCESS));
    }

    @GetMapping(value = "/{numistaCode}")
    public ResponseEntity<ApiResponse<Country>> getCountry(
            @PathVariable(name = "numistaCode", required = true) String numistaCode) {
        Country country = N4JUtil.getInstance().numistaService.countryService.findCountryByNumistaCode(numistaCode);
        return ResponseEntity.ok(new ApiResponse<>(country, "Country fetched successfully",
                ApiResponse.Status.SUCCESS));
    }

}
