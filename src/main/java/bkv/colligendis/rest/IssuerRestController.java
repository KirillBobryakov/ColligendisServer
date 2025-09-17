package bkv.colligendis.rest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.utils.N4JUtil;

@RestController
@RequestMapping("/database/issuer")
public class IssuerRestController {

    @GetMapping(value = "/all")
    public ResponseEntity<ApiResponse<List<Issuer>>> getAllIssuers() {
        try {

            List<Issuer> issuers = N4JUtil.getInstance().numistaService.issuerService.findAll().stream()
                    .sorted(Comparator.comparing(Issuer::getName))
                    .collect(Collectors.toList());
            return ResponseEntity
                    .ok(new ApiResponse<>(issuers, "Issuers fetched successfully", ApiResponse.Status.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new ApiResponse<>(null, "Error fetching issuers", ApiResponse.Status.ERROR));
        }
    }

    @GetMapping(value = "/filtered")
    public ResponseEntity<ApiResponse<List<Issuer>>> getIssuers(@RequestParam(name = "q", required = true) String q,
            @RequestParam(name = "countryNumistaCode", required = true) String countryNumistaCode,
            @RequestParam(name = "subjectNumistaCode", required = true) String subjectNumistaCode) {

        List<Issuer> issuers = new ArrayList<>();
        if (subjectNumistaCode != null && !subjectNumistaCode.isEmpty()) {
            issuers.addAll(N4JUtil.getInstance().numistaService.issuerService
                    .findChildrenIssuersBySubjectNumistaCodeWithFilterByIssuerName(subjectNumistaCode, q).stream()
                    .map(issuer -> new Issuer(issuer.getName(), issuer.getCode()))
                    .sorted(Comparator.comparing(Issuer::getName))
                    .collect(Collectors.toList()));
        } else if (countryNumistaCode != null && !countryNumistaCode.isEmpty()) {
            issuers.addAll(N4JUtil.getInstance().numistaService.issuerService
                    .findChildrenIssuersByCountryNumistaCodeWithFilterByIssuerName(countryNumistaCode, q).stream()
                    .map(issuer -> new Issuer(issuer.getName(), issuer.getCode()))
                    .sorted(Comparator.comparing(Issuer::getName))
                    .collect(Collectors.toList()));
        } else if (!q.isEmpty()) {
            issuers.addAll(N4JUtil.getInstance().numistaService.issuerService
                    .findIssuersByName("(?i).*" + q.toLowerCase() + ".*").stream()
                    .map(issuer -> new Issuer(issuer.getName(), issuer.getCode()))
                    .sorted(Comparator.comparing(Issuer::getName))
                    .collect(Collectors.toList()));
        }
        return ResponseEntity
                .ok(new ApiResponse<>(issuers, "Issuers fetched successfully", ApiResponse.Status.SUCCESS));
    }

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
