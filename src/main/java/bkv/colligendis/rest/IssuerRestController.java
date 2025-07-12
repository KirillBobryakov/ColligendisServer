package bkv.colligendis.rest;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssuerRestController {

    // @GetMapping(value = "/issuer/filter/country/{eid}")
    // @ResponseBody
    // public ResponseEntity<List<IssuerNude>>
    // getIssuersOfCountry(@PathVariable(name = "eid") String ied) {
    // List<IssuerNude> subjects =
    // N4JUtil.getInstance().numistaService.issuerService.findByCountryEid(ied).stream()
    // .map(issuer -> new IssuerNude(issuer.getId(), issuer.getEid().toString(),
    // issuer.getName()))
    // .collect(Collectors.toList());
    // return ResponseEntity.ok(subjects);
    // }
    //
    // @GetMapping(value = "/issuer/filter/subject/{eid}")
    // @ResponseBody
    // public ResponseEntity<List<IssuerNude>>
    // getIssuersOfSubject(@PathVariable(name = "eid") String ied) {
    // List<IssuerNude> subjects =
    // N4JUtil.getInstance().numistaService.issuerService.findBySubjectEid(ied).stream()
    // .map(issuer -> new IssuerNude(issuer.getId(), issuer.getEid().toString(),
    // issuer.getName()))
    // .collect(Collectors.toList());
    // return ResponseEntity.ok(subjects);
    // }

}

class IssuerEntity {
    List<String> names;
    String type;

}

class IssuerNude {

    long id;
    String eid;
    String name;

    public IssuerNude(long id, String eid, String name) {
        this.id = id;
        this.eid = eid;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
