package bkv.colligendis.rest;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.rest.catalogue.CatalogueNTypesRequest2;
import bkv.colligendis.rest.dto.ItemTypeDTO;
import bkv.colligendis.rest.dto.NTypeDTO;
import bkv.colligendis.rest.dto.NTypeVariantDTO;
import bkv.colligendis.utils.N4JUtil;

@RestController
@RequestMapping("/database/ntype")
public class NTypeRestController {

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = "/filtered", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<NTypeDTO>>> getCatalogueNTypes2(
            @RequestBody CatalogueNTypesRequest2 request) {

        List<NTypeDTO> catalogueNTypes = new ArrayList<>();
        List<NType> nTypesList = new ArrayList<>();

        if (request.getIssuerCode() != null && !request.getIssuerCode().isEmpty()) {
            if (request.getItemTypes() != null && !request.getItemTypes().isEmpty()) {
                for (ItemTypeDTO collectableType : request.getItemTypes()) {
                    nTypesList.addAll(N4JUtil.getInstance().numistaService.nTypeService
                            .findNTypesByIssuerCodeWithFilterByCollectableTypeByCurrencyNid(request.getIssuerCode(),
                                    collectableType.getCode(), request.getCurrencyNid()));
                }
            } else {
                nTypesList = N4JUtil.getInstance().numistaService.nTypeService
                        .findNTypesByIssuerCodeWithFilterByCollectableTypeByCurrencyNid(request.getIssuerCode(), null,
                                request.getCurrencyNid());
            }
        } else if (request.getSubjectNumistaCode() != null && !request.getSubjectNumistaCode().isEmpty()) {
            if (request.getItemTypes() != null && !request.getItemTypes().isEmpty()) {
                for (ItemTypeDTO collectableType : request.getItemTypes()) {
                    nTypesList.addAll(N4JUtil.getInstance().numistaService.nTypeService
                            .findNTypesBySubjectNumistaCodeWithFilterByCollectableTypeByCurrencyNid(
                                    request.getSubjectNumistaCode(),
                                    collectableType.getCode(), request.getCurrencyNid()));
                }
            } else {
                nTypesList = N4JUtil.getInstance().numistaService.nTypeService
                        .findNTypesBySubjectNumistaCodeWithFilterByCollectableTypeByCurrencyNid(
                                request.getSubjectNumistaCode(),
                                null, request.getCurrencyNid());
            }
        } else if (request.getCountryNumistaCode() != null && !request.getCountryNumistaCode().isEmpty()) {
            if (request.getItemTypes() != null && !request.getItemTypes().isEmpty()) {
                for (ItemTypeDTO collectableType : request.getItemTypes()) {
                    nTypesList.addAll(N4JUtil.getInstance().numistaService.nTypeService
                            .findNTypesByCountryNumistaCodeWithFilterByCollectableTypeByCurrencyNid(
                                    request.getCountryNumistaCode(),
                                    collectableType.getCode(), request.getCurrencyNid()));
                }
            } else {
                nTypesList = N4JUtil.getInstance().numistaService.nTypeService
                        .findNTypesByCountryNumistaCodeWithFilterByCollectableTypeByCurrencyNid(
                                request.getCountryNumistaCode(),
                                null, request.getCurrencyNid());
            }
        }

        nTypesList.sort((n1, n2) -> {
            return n1.getDenomination().getNumericValue().compareTo(n2.getDenomination().getNumericValue());
        });

        for (NType nType : nTypesList) {
            CollectibleType collectibleTypeParent = nType.getCollectibleType();
            while (collectibleTypeParent.getCollectibleTypeParent() != null) {
                collectibleTypeParent = collectibleTypeParent.getCollectibleTypeParent();
            }
            NTypeDTO catalogueNType = modelMapper.map(nType, NTypeDTO.class);
            catalogueNType.setTopCollectibleType(collectibleTypeParent.getName());

            List<NTypeVariantDTO> variants = N4JUtil.getInstance().numistaService.variantService
                    .getVariantsByNTypeNid(nType.getNid());

            variants.forEach(variant -> {
                Integer itemsCount = N4JUtil.getInstance().numistaService.itemService
                        .countItemsByVariantNid(variant.getNid());
                variant.setItemsCount(itemsCount);
            });

            catalogueNType.setVariants(variants);

            catalogueNTypes.add(catalogueNType);
        }

        return ResponseEntity.ok(new ApiResponse<>(catalogueNTypes, "N-types fetched successfully",
                ApiResponse.Status.SUCCESS));
    }

}

// //
// // @GetMapping(value = "/ntype/filter/string/{filter}")
// // @ResponseBody
// // public ResponseEntity<List<NTypeListItem>>
// getNTypesByTitleFilter(@PathVariable(name = "filter") String filter) {
// // long t = System.currentTimeMillis();
// // List<NType> nTypes =
// N4JUtil.getInstance().numistaService.nTypeService.findByTitleFilter(filter);
// // System.out.println("getNTypesByTitleFilter: " +
// (System.currentTimeMillis() - t));
// // return ResponseEntity.ok(nTypes.stream()
// // .map(nType -> new NTypeListItem(nType.getId(), nType.getEid().toString(),
// nType.getNid(), nType.getTitle()))
// // .collect(Collectors.toList()));
// // }

// //
// // @GetMapping(value = "/ntype/country_eid/{eid}")
// // @ResponseBody
// // public ResponseEntity<List<NTypeListItem>>
// findByCountryEid(@PathVariable(name = "eid") String eid) {
// // long t = System.currentTimeMillis();
// // List<NType> nTypes =
// N4JUtil.getInstance().numistaService.nTypeService.findByCountryEid(eid);
// // System.out.println("getNTypesByCountryEid: " + (System.currentTimeMillis()
// - t));
// // return ResponseEntity.ok(nTypes.stream()
// // .map(nType -> new NTypeListItem(nType.getId(), nType.getEid().toString(),
// nType.getNid(), nType.getTitle()))
// // .collect(Collectors.toList()));
// // }
// //
// // @GetMapping(value =
// "/ntype/country_eid/{countryEid}/filter/string/{filter}")
// // @ResponseBody
// // public ResponseEntity<List<NTypeListItem>>
// findByTitleFilterAndCountryEid(@PathVariable(name = "filter") String filter,
// @PathVariable(name = "countryEid") String countryEid) {
// // long t = System.currentTimeMillis();
// // List<NType> nTypes =
// N4JUtil.getInstance().numistaService.nTypeService.findByTitleFilterAndCountryEid(filter,
// countryEid);
// // System.out.println("getNTypesByTitleFilterAndCountryEid: " +
// (System.currentTimeMillis() - t));
// // return ResponseEntity.ok(nTypes.stream()
// // .map(nType -> new NTypeListItem(nType.getId(), nType.getEid().toString(),
// nType.getNid(), nType.getTitle()))
// // .collect(Collectors.toList()));
// // }
// //
// // @GetMapping(value = "/ntype/subject_eid/{eid}/year/{year}")
// // @ResponseBody
// // public ResponseEntity<List<NTypeListItem>>
// findBySubjectEidAndYear(@PathVariable(name = "eid") String eid,
// @PathVariable(name = "year") int year) {
// // long t = System.currentTimeMillis();
// // List<NType> nTypes =
// N4JUtil.getInstance().numistaService.nTypeService.findBySubjectEidAndYear(eid,
// year);
// // System.out.println("getNTypesBySubjectEid: " + (System.currentTimeMillis()
// - t));
// //
// // return ResponseEntity.ok(new ArrayList<>());
// //// return ResponseEntity.ok(NTypeListItem.fromFlux(nTypes));
// // }
// //
// // @GetMapping(value = "/ntype/subject_eid/{eid}/filter/string/{filter}")
// // @ResponseBody
// // public ResponseEntity<List<NTypeListItem>>
// findByTitleFilterAndSubjectEid(@PathVariable(name = "filter") String filter,
// @PathVariable(name = "eid") String eid) {
// // long t = System.currentTimeMillis();
// // List<NType> nTypes =
// N4JUtil.getInstance().numistaService.nTypeService.findByTitleFilterAndSubjectEid(filter,
// eid);
// // System.out.println("getNTypesByTitleFilterAndSubjectEid: " +
// (System.currentTimeMillis() - t));
// // return ResponseEntity.ok(nTypes.stream()
// // .map(nType -> new NTypeListItem(nType.getId(), nType.getEid().toString(),
// nType.getNid(), nType.getTitle()))
// // .collect(Collectors.toList()));
// // }

// @GetMapping(value = "/ntype/issuer_eid/{eid}/year/{year}")
// @ResponseBody
// public ResponseEntity<List<NTypeListItem>>
// findByIssuerEidAndYear(@PathVariable(name = "eid") String eid,
// @PathVariable(name = "year") int year) {
// long t = System.currentTimeMillis();
// List<NType> nTypes =
// N4JUtil.getInstance().numistaService.nTypeService.findByIssuerEidAndYear(eid,
// year);
// System.out.println("getNTypesByIssuerEidAndYear: " +
// (System.currentTimeMillis() - t));

// return ResponseEntity.ok(new ArrayList<>());
// // return ResponseEntity.ok(NTypeListItem.fromFlux(nTypes));
// }

// @GetMapping(value =
// "/ntype/issuer_eid/{issuerEid}/filter/string/{filter}/year/{year}")
// @ResponseBody
// public ResponseEntity<List<NTypeListItem>>
// findByTitleFilterAndIssuerEid(@PathVariable(name = "filter") String filter,
// @PathVariable(name = "issuerEid") String issuerEid, @PathVariable(name =
// "year") int year) {
// long t = System.currentTimeMillis();
// List<NType> nTypes =
// N4JUtil.getInstance().numistaService.nTypeService.findByTitleFilterAndIssuerEidAndYear(filter,
// issuerEid, year);
// System.out.println("getNTypesByTitleFilterAndIssuerEidAndYear: " +
// (System.currentTimeMillis() - t));

// return ResponseEntity.ok(new ArrayList<>());
// // return ResponseEntity.ok(NTypeListItem.fromFlux(nTypes));
// }

// @GetMapping(value = "/variants/years/issuer/{eid}")
// @ResponseBody
// public ResponseEntity<List<Integer>>
// findVariantsYearsByIssuerEid(@PathVariable(name = "eid") String eid) {
// long t = System.currentTimeMillis();
// List<Integer> years =
// N4JUtil.getInstance().numistaService.variantService.getYearsOfVariantsByIssuerEid(eid);

// System.out.println("findVariantsYearsByIssuerEid: " +
// (System.currentTimeMillis() - t));
// return ResponseEntity.ok(years);
// }

// @GetMapping(value = "/ntype/load/issuer/{eid}")
// @ResponseBody
// public ResponseEntity<Boolean> loadNTypesByIssuerEid(@PathVariable(name =
// "eid") String eid) {
// long t = System.currentTimeMillis();

// List<String> nTypeNidFlux =
// N4JUtil.getInstance().numistaService.nTypeService.findNTypeNidByIssuerEid(eid);

// //
// Objects.requireNonNull(nTypeNidFlux.collectList().block()).forEach(NumistaEditPageUtil::loadInfo);

// System.out.println("loadNTypesByIssuerEid: " + (System.currentTimeMillis() -
// t));
// return ResponseEntity.ok(true);
// }

// }

// class NTypeListItem {
// private long id;
// private String eid;
// private String nid;
// private String title;

// private String obversePicture;
// private String reversePicture;

// private int variantCount;

// public NTypeListItem(long id, String eid, String nid, String title) {
// this.id = id;
// this.eid = eid;
// this.nid = nid;
// this.title = title;
// }
// //
// // public static List<NTypeListItem> fromFlux(Flux<NType> nTypes) {
// // return nTypes.parallel().map(nType -> {
// // NTypeListItem nTypeListItem = new NTypeListItem(nType.getId(),
// nType.getEid().toString(), nType.getNid(), nType.getTitle());
// // nTypeListItem.setObversePicture(nType.getObverse().getPicture());
// // nTypeListItem.setReversePicture(nType.getReverse().getPicture());
// // nTypeListItem.setVariantCount(nType.getVariants().size());
// // return nTypeListItem;
// //
// }).collectSortedList(Comparator.comparing(NTypeListItem::getTitle)).block();
// // }

// public long getId() {
// return id;
// }

// public void setId(long id) {
// this.id = id;
// }

// public String getEid() {
// return eid;
// }

// public void setEid(String eid) {
// this.eid = eid;
// }

// public String getNid() {
// return nid;
// }

// public void setNid(String nid) {
// this.nid = nid;
// }

// public String getTitle() {
// return title;
// }

// public void setTitle(String title) {
// this.title = title;
// }

// public String getObversePicture() {
// return obversePicture;
// }

// public void setObversePicture(String obversePicture) {
// this.obversePicture = obversePicture;
// }

// public String getReversePicture() {
// return reversePicture;
// }

// public void setReversePicture(String reversePicture) {
// this.reversePicture = reversePicture;
// }

// public int getVariantCount() {
// return variantCount;
// }

// public void setVariantCount(int variantCount) {
// this.variantCount = variantCount;
// }
// }