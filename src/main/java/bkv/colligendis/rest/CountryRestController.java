package bkv.colligendis.rest;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.utils.N4JUtil;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CountryRestController {

    @GetMapping(value = "/countries/all/names")
    @ResponseBody
    public ResponseEntity<List<String>> getAllCountriesNames() {
        List<String> countriesNames = N4JUtil.getInstance().numistaService.countryService.findAllCountriesNames();
        return ResponseEntity.ok(countriesNames);
    }

    @PostMapping(value = "/countries/filter/")
    public ResponseEntity<List<Country>> getCountriesWithNameFilter(@RequestBody Filter filter) {
        List<Country> countries = N4JUtil.getInstance().numistaService.countryService
                .findByNameContainingIgnoreCase(filter.getNameFilter());
        return ResponseEntity.ok(countries);
    }
    //
    // @PostMapping("/csi/filter/name")
    // @ResponseBody
    // public ResponseEntity<List<CSIListItem>>
    // getCSIListWithNameFilter(@RequestBody Filter filter) {
    //
    // List<Country> countries =
    // N4JUtil.getInstance().numistaService.countryService.findByNameContainingIgnoreCase(filter.getNameFilter());
    // List<Subject> subjects =
    // N4JUtil.getInstance().numistaService.subjectService.findByNameContainingIgnoreCase(filter.getNameFilter());
    // List<Issuer> issuers =
    // N4JUtil.getInstance().numistaService.issuerService.findByNameContainingIgnoreCase(filter.getNameFilter());
    //
    // Stream<CSIListItem> csiListItemStream = Stream.concat(Stream.concat(
    // countries.stream().map(country -> {
    // CSIListItem csiListItem = new CSIListItem(country.getEid().toString(),
    // country.getName(), CSIListItem.Label.COUNTRY);
    // if (!country.getRuAlternativeNames().isEmpty()) {
    // csiListItem.setRuAlternativeName(country.getRuAlternativeNames().get(0));
    // }
    // csiListItem.setSubjectsCount(country.getSubjects().size());
    // csiListItem.setIssuersCount(country.getIssuers().size());
    // return csiListItem;
    // }),
    // subjects.stream().map(subject -> {
    // CSIListItem csiListItem = new CSIListItem(subject.getEid().toString(),
    // subject.getName(), CSIListItem.Label.SUBJECT);
    // if (!subject.getRuAlternativeNames().isEmpty()) {
    // csiListItem.setRuAlternativeName(subject.getRuAlternativeNames().get(0));
    // }
    // csiListItem.setSubjectsCount(subject.getChildSubjects().size());
    // csiListItem.setIssuersCount(subject.getIssuers().size());
    // return csiListItem;
    // })),
    // issuers.stream().map(issuer -> {
    // CSIListItem csiListItem = new CSIListItem(issuer.getEid().toString(),
    // issuer.getName(), CSIListItem.Label.ISSUER);
    // if (!issuer.getRuAlternativeNames().isEmpty()) {
    // csiListItem.setRuAlternativeName(issuer.getRuAlternativeNames().get(0));
    // }
    // csiListItem.setNTypesCount(issuer.getNTypes().size());
    // return csiListItem;
    // }));
    //
    //
    // return
    // ResponseEntity.ok(csiListItemStream.sorted(Comparator.comparing(CSIListItem::getName)).collect(Collectors.toList()));
    // }
    //

}

@Data
class Filter {
    String nameFilter;
}
