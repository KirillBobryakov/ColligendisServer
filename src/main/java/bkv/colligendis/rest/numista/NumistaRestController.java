package bkv.colligendis.rest.numista;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.rest.ApiResponse;
import bkv.colligendis.rest.catalogue.ParsingNumistaData;
import bkv.colligendis.utils.numista.item.NumistaAllItemsParser;

@RestController
@RequestMapping("/numista")
public class NumistaRestController {

    @GetMapping(value = "/parse-numista")
    public ResponseEntity<ApiResponse<Boolean>> parseNumista(
            @RequestParam(name = "issuerCode") String issuerCode,
            @RequestParam(name = "topCollectableType") String topCollectableType) {

        if (issuerCode == null || topCollectableType == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false, "Invalid request, null values",
                    ApiResponse.Status.ERROR));
        }
        if (issuerCode.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false, "Invalid request, empty Issuer code",
                    ApiResponse.Status.ERROR));
        }
        if (topCollectableType.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false, "Invalid request, empty Top collectable type",
                    ApiResponse.Status.ERROR));
        }
        try {
            NumistaAllItemsParser numistaAllItemsParser = new NumistaAllItemsParser();
            numistaAllItemsParser.fetchAndProcessCatalog(issuerCode,
                    topCollectableType);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>(
                    false, "Error parsing numista data",
                    ApiResponse.Status.ERROR));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Parsing numista data completed",
                ApiResponse.Status.SUCCESS));
    }

}
