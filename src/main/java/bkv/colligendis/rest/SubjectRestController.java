package bkv.colligendis.rest;


import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.utils.N4JUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class SubjectRestController {

//
//    @GetMapping(value = "/subject/filter/country/{eid}")
//    @ResponseBody
//    public ResponseEntity<List<SubjectNude>> getSubjectsOfCountry(@PathVariable(name = "eid") String ied) {
//        List<SubjectNude> subjects = N4JUtil.getInstance().numistaService.subjectService.findByCountryEid(ied).stream()
//                .map(subject -> new SubjectNude(subject.getId(), subject.getEid().toString(), subject.getName()))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(subjects);
//    }


}

class SubjectNude {

    long id;
    String eid;
    String name;

    public SubjectNude(long id, String eid, String name) {
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
