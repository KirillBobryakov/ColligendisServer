package bkv.colligendis.rest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.utils.N4JUtil;

@RestController
@RequestMapping("/database/subject")
public class SubjectRestController {

    @GetMapping(value = "/all")
    public ResponseEntity<ApiResponse<List<Subject>>> getAllSubjects() {
        try {
            List<Subject> subjects = N4JUtil.getInstance().numistaService.subjectService.findAll().stream()
                    .sorted(Comparator.comparing(Subject::getName))
                    .collect(Collectors.toList());
            return ResponseEntity
                    .ok(new ApiResponse<>(subjects, "Subjects fetched successfully", ApiResponse.Status.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new ApiResponse<>(null, "Error fetching subjects", ApiResponse.Status.ERROR));
        }
    }

    @GetMapping(value = "/filtered")
    public ResponseEntity<ApiResponse<List<Subject>>> getSubjects(
            @RequestParam(name = "countryNumistaCode") String countryNumistaCode) {
        List<Subject> subjects = N4JUtil.getInstance().numistaService.subjectService
                .findByCountryNumistaCode(countryNumistaCode).stream()
                .map(subject -> new Subject(subject.getName(), subject.getNumistaCode()))
                .sorted(Comparator.comparing(Subject::getName))
                .collect(Collectors.toList());
        return ResponseEntity
                .ok(new ApiResponse<>(subjects, "Subjects fetched successfully", ApiResponse.Status.SUCCESS));

    }

    //
    // @GetMapping(value = "/subject/filter/country/{eid}")
    // @ResponseBody
    // public ResponseEntity<List<SubjectNude>>
    // getSubjectsOfCountry(@PathVariable(name = "eid") String ied) {
    // List<SubjectNude> subjects =
    // N4JUtil.getInstance().numistaService.subjectService.findByCountryEid(ied).stream()
    // .map(subject -> new SubjectNude(subject.getId(), subject.getEid().toString(),
    // subject.getName()))
    // .collect(Collectors.toList());
    // return ResponseEntity.ok(subjects);
    // }

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
