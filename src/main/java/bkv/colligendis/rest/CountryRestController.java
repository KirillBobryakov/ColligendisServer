package bkv.colligendis.rest;


import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.utils.N4JUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class CountryRestController {


    @GetMapping(value = "/countries/all")
    @ResponseBody
    public ResponseEntity<List<Country>> getMintsWithNameFilter() {
        long t = System.currentTimeMillis();
        List<Country> countries = N4JUtil.getInstance().numistaService.countryService.findAllWithSort();
//        System.out.println("getMintsWithNameFilter:" + (System.currentTimeMillis() - t));
        return ResponseEntity.ok(countries);
    }

    @GetMapping(value = "/countries/filter/name/{nameFilter}")
    @ResponseBody
    public ResponseEntity<List<Country>> getCountriesWithNameFilter(@PathVariable(name = "nameFilter") String nameFilter) {
        List<Country> countries = N4JUtil.getInstance().numistaService.countryService.findByNameFilter(nameFilter);
        return ResponseEntity.ok(countries);
    }

    @GetMapping(value = "/csi/filter/name/{nameFilter}")
    @ResponseBody
    public ResponseEntity<List<CSIListItem>> getCSIListWithNameFilter(@PathVariable(name = "nameFilter") String nameFilter) {
        List<Country> countries = N4JUtil.getInstance().numistaService.countryService.findByNameFilter(nameFilter);
        List<Subject> subjects = N4JUtil.getInstance().numistaService.subjectService.findByNameFilter(nameFilter);
        List<Issuer> issuers = N4JUtil.getInstance().numistaService.issuerService.findByNameFilter(nameFilter);

        Stream<CSIListItem> csiListItemStream = Stream.concat(Stream.concat(
                countries.stream().map(country -> new CSIListItem(country.getEid().toString(), country.getName(), CSIListItem.Label.COUNTRY)),
                subjects.stream().map(subject -> new CSIListItem(subject.getEid().toString(), subject.getName(), CSIListItem.Label.SUBJECT))),
                issuers.stream().map(issuer -> new CSIListItem(issuer.getEid().toString(), issuer.getName(), CSIListItem.Label.ISSUER)));


        return ResponseEntity.ok(csiListItemStream.sorted(Comparator.comparing(CSIListItem::getName)).collect(Collectors.toList()));
    }



}


/**
 * CSI - Country, Subject, Issuer
 */
class CSIListItem {

    enum Label {COUNTRY, SUBJECT, ISSUER}

    String eid;
    String name;
    Label label;

    public CSIListItem(String eid, String name, Label label) {
        this.eid = eid;
        this.name = name;
        this.label = label;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
