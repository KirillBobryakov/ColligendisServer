package bkv.colligendis.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.rest.dto.NTypeVariantDTO;
import bkv.colligendis.utils.N4JUtil;

@RestController
@RequestMapping("/database/variant")
public class VariantRestController {

    @GetMapping(value = "/byNtype")
    public ResponseEntity<ApiResponse<List<NTypeVariantDTO>>> getVariantsByNType(
            @RequestParam(name = "nTypeNid") String nTypeNid) {
        List<NTypeVariantDTO> variants = N4JUtil.getInstance().numistaService.variantService
                .getVariantsByNTypeNid(nTypeNid);
        variants.forEach(variant -> {
            Integer itemsCount = N4JUtil.getInstance().numistaService.itemService
                    .countItemsByVariantNid(variant.getNid());
            variant.setItemsCount(itemsCount);
        });
        return ResponseEntity
                .ok(new ApiResponse<>(variants, "Variants fetched successfully", ApiResponse.Status.SUCCESS));
    }
}
