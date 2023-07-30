package bkv.colligendis.rest;


import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.NumistaEditPageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class NTypeRestController {


    @GetMapping(value = "/ntype/filter/string/{filter}")
    @ResponseBody
    public ResponseEntity<List<NTypeListItem>> getNTypesByTitleFilter(@PathVariable(name = "filter") String filter) {
        long t = System.currentTimeMillis();
        List<NType> nTypes = N4JUtil.getInstance().numistaService.nTypeService.findByTitleFilter(filter);
        System.out.println("getNTypesByTitleFilter: " + (System.currentTimeMillis() - t));
        return ResponseEntity.ok(nTypes.stream()
                .map(nType -> new NTypeListItem(nType.getId(), nType.getEid().toString(), nType.getNid(), nType.getTitle()))
                .collect(Collectors.toList()));
    }


    @GetMapping(value = "/ntype/country_eid/{eid}")
    @ResponseBody
    public ResponseEntity<List<NTypeListItem>> findByCountryEid(@PathVariable(name = "eid") String eid) {
        long t = System.currentTimeMillis();
        List<NType> nTypes = N4JUtil.getInstance().numistaService.nTypeService.findByCountryEid(eid);
        System.out.println("getNTypesByCountryEid: " + (System.currentTimeMillis() - t));
        return ResponseEntity.ok(nTypes.stream()
                .map(nType -> new NTypeListItem(nType.getId(), nType.getEid().toString(), nType.getNid(), nType.getTitle()))
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/ntype/country_eid/{countryEid}/filter/string/{filter}")
    @ResponseBody
    public ResponseEntity<List<NTypeListItem>> findByTitleFilterAndCountryEid(@PathVariable(name = "filter") String filter, @PathVariable(name = "countryEid") String countryEid) {
        long t = System.currentTimeMillis();
        List<NType> nTypes = N4JUtil.getInstance().numistaService.nTypeService.findByTitleFilterAndCountryEid(filter, countryEid);
        System.out.println("getNTypesByTitleFilterAndCountryEid: " + (System.currentTimeMillis() - t));
        return ResponseEntity.ok(nTypes.stream()
                .map(nType -> new NTypeListItem(nType.getId(), nType.getEid().toString(), nType.getNid(), nType.getTitle()))
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/ntype/subject_eid/{eid}/year/{year}")
    @ResponseBody
    public ResponseEntity<List<NTypeListItem>> findBySubjectEidAndYear(@PathVariable(name = "eid") String eid, @PathVariable(name = "year") int year) {
        long t = System.currentTimeMillis();
        Flux<NType> nTypes = N4JUtil.getInstance().numistaService.nTypeService.findBySubjectEidAndYear(eid, year);
        System.out.println("getNTypesBySubjectEid: " + (System.currentTimeMillis() - t));

        return ResponseEntity.ok(NTypeListItem.fromFlux(nTypes));
    }

    @GetMapping(value = "/ntype/subject_eid/{eid}/filter/string/{filter}")
    @ResponseBody
    public ResponseEntity<List<NTypeListItem>> findByTitleFilterAndSubjectEid(@PathVariable(name = "filter") String filter, @PathVariable(name = "eid") String eid) {
        long t = System.currentTimeMillis();
        List<NType> nTypes = N4JUtil.getInstance().numistaService.nTypeService.findByTitleFilterAndSubjectEid(filter, eid);
        System.out.println("getNTypesByTitleFilterAndSubjectEid: " + (System.currentTimeMillis() - t));
        return ResponseEntity.ok(nTypes.stream()
                .map(nType -> new NTypeListItem(nType.getId(), nType.getEid().toString(), nType.getNid(), nType.getTitle()))
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/ntype/issuer_eid/{eid}/year/{year}")
    @ResponseBody
    public ResponseEntity<List<NTypeListItem>> findByIssuerEidAndYear(@PathVariable(name = "eid") String eid, @PathVariable(name = "year") int year) {
        long t = System.currentTimeMillis();
        Flux<NType> nTypes = N4JUtil.getInstance().numistaService.nTypeService.findByIssuerEidAndYear(eid, year);
        System.out.println("getNTypesByIssuerEidAndYear: " + (System.currentTimeMillis() - t));

        return ResponseEntity.ok(NTypeListItem.fromFlux(nTypes));
    }

    @GetMapping(value = "/ntype/issuer_eid/{issuerEid}/filter/string/{filter}/year/{year}")
    @ResponseBody
    public ResponseEntity<List<NTypeListItem>> findByTitleFilterAndIssuerEid(@PathVariable(name = "filter") String filter, @PathVariable(name = "issuerEid") String issuerEid, @PathVariable(name = "year") int year) {
        long t = System.currentTimeMillis();
        Flux<NType> nTypes = N4JUtil.getInstance().numistaService.nTypeService.findByTitleFilterAndIssuerEidAndYear(filter, issuerEid, year);
        System.out.println("getNTypesByTitleFilterAndIssuerEidAndYear: " + (System.currentTimeMillis() - t));

        return ResponseEntity.ok(NTypeListItem.fromFlux(nTypes));
    }

    @GetMapping(value = "/variants/years/issuer/{eid}")
    @ResponseBody
    public ResponseEntity<List<Integer>> findVariantsYearsByIssuerEid(@PathVariable(name = "eid") String eid) {
        long t = System.currentTimeMillis();
        List<Integer> years = N4JUtil.getInstance().numistaService.variantService.getYearsOfVariantsByIssuerEid(eid);

        System.out.println("findVariantsYearsByIssuerEid: " + (System.currentTimeMillis() - t));
        return ResponseEntity.ok(years);
    }

    @GetMapping(value = "/ntype/load/issuer/{eid}")
    @ResponseBody
    public ResponseEntity<Boolean> loadNTypesByIssuerEid(@PathVariable(name = "eid") String eid) {
        long t = System.currentTimeMillis();

        Flux<String> nTypeNidFlux = N4JUtil.getInstance().numistaService.nTypeService.findNTypeNidByIssuerEid(eid);

        Objects.requireNonNull(nTypeNidFlux.collectList().block()).forEach(NumistaEditPageUtil::loadInfo);

        System.out.println("loadNTypesByIssuerEid: " + (System.currentTimeMillis() - t));
        return ResponseEntity.ok(true);
    }


}

class NTypeListItem {
    private long id;
    private String eid;
    private String nid;
    private String title;

    private String obversePicture;
    private String reversePicture;

    private int variantCount;


    public NTypeListItem(long id, String eid, String nid, String title) {
        this.id = id;
        this.eid = eid;
        this.nid = nid;
        this.title = title;
    }

    public static List<NTypeListItem> fromFlux(Flux<NType> nTypes) {
        return nTypes.parallel().map(nType -> {
            NTypeListItem nTypeListItem = new NTypeListItem(nType.getId(), nType.getEid().toString(), nType.getNid(), nType.getTitle());
            nTypeListItem.setObversePicture(nType.getObverse().getPicture());
            nTypeListItem.setReversePicture(nType.getReverse().getPicture());
            nTypeListItem.setVariantCount(nType.getVariants().size());
            return nTypeListItem;
        }).collectSortedList(Comparator.comparing(NTypeListItem::getTitle)).block();
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

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObversePicture() {
        return obversePicture;
    }

    public void setObversePicture(String obversePicture) {
        this.obversePicture = obversePicture;
    }

    public String getReversePicture() {
        return reversePicture;
    }

    public void setReversePicture(String reversePicture) {
        this.reversePicture = reversePicture;
    }

    public int getVariantCount() {
        return variantCount;
    }

    public void setVariantCount(int variantCount) {
        this.variantCount = variantCount;
    }
}