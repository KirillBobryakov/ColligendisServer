package bkv.colligendis.rest.market;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.database.entity.meshok.MeshokCategory;
import bkv.colligendis.database.entity.meshok.MeshokLot;
import bkv.colligendis.rest.ApiResponse;
import bkv.colligendis.utils.ImageUtil;
import bkv.colligendis.utils.N4JUtil;

@RestController
public class ClientMarketRestController {

    // @GetMapping(value = "/market/countries/all")
    // public ResponseEntity<ApiResponse<List<Country>>> getCountries() {
    // List<Country> countries =
    // N4JUtil.getInstance().numistaService.countryService.findAll().stream()
    // .map(country -> new Country(country.getName(), country.getNumistaCode()))
    // .sorted(Comparator.comparing(Country::getName))
    // .collect(Collectors.toList());
    // return ResponseEntity
    // .ok(new ApiResponse<>(countries, "Countries fetched successfully",
    // ApiResponse.Status.SUCCESS));
    // }

    @GetMapping(value = "/market/subjects/all")
    public ResponseEntity<ApiResponse<List<Subject>>> getSubjects() {
        List<Subject> subjects = N4JUtil.getInstance().numistaService.subjectService.findAll().stream()
                .map(subject -> new Subject(subject.getName(), subject.getNumistaCode()))
                .sorted(Comparator.comparing(Subject::getName))
                .collect(Collectors.toList());
        return ResponseEntity
                .ok(new ApiResponse<>(subjects, "Subjects fetched successfully", ApiResponse.Status.SUCCESS));
    }

    @GetMapping(value = "/market/subjects")
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

    @GetMapping(value = "/market/meshok/categories/all")
    public ResponseEntity<ApiResponse<List<MeshokCategoryRest>>> getMeshokCategories() {

        List<bkv.colligendis.database.entity.meshok.MeshokCategory> categories = N4JUtil
                .getInstance().meshokService.meshokCategoryService.findAll();
        List<MeshokCategoryRest> categories2 = categories.stream()
                .map(category -> {
                    if (category.getMid() == 12851) {
                        System.out.println(category.getName());
                    }
                    return new MeshokCategoryRest(category.getMid(), category.getName(),
                            category.getParent() != null ? category.getParent().getMid() : null,
                            category.getLevel());
                })
                .sorted(Comparator.comparing(MeshokCategoryRest::getName))
                .collect(Collectors.toList());
        return ResponseEntity
                .ok(new ApiResponse<>(categories2, "Meshok categories fetched successfully",
                        ApiResponse.Status.SUCCESS));
    }

    @GetMapping(value = "/market/meshok/categories/under")
    public ResponseEntity<ApiResponse<List<MeshokCategoryRest>>> getMeshokCategoriesUnder(
            @RequestParam(name = "parentMid") int parentMid) {
        try {
            List<MeshokCategory> categories = N4JUtil.getInstance().meshokService.meshokCategoryService
                    .findByParentMid(
                            parentMid);

            List<MeshokCategoryRest> meshokCategoryRestList = categories
                    .stream()
                    .map(category -> new MeshokCategoryRest(category.getMid(), category.getName(),
                            category.getParent() != null ? category.getParent().getMid() : null,
                            category.getLevel()))
                    .sorted(Comparator.comparing(MeshokCategoryRest::getName))
                    .collect(Collectors.toList());
            return ResponseEntity
                    .ok(new ApiResponse<>(
                            meshokCategoryRestList, "Meshok categories fetched successfully",
                            ApiResponse.Status.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .ok(new ApiResponse<>(null, "Error loading Categories by parent category with min = "
                            + parentMid + ". " + e.getMessage(), ApiResponse.Status.ERROR));

        }
    }

    @GetMapping(value = "/market/meshok/lots/load/bycategory/{mid}")
    public ResponseEntity<ApiResponse<Boolean>> loadLotsByCategory(@PathVariable(name = "mid") int mid) {
        try {
            System.out.println("Loading lots by category: " + mid);
            // MeshokUtil.saveLotsToDatabase(mid);
            return ResponseEntity
                    .ok(new ApiResponse<>(true, "Lots loaded successfully", ApiResponse.Status.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity
                    .ok(new ApiResponse<>(false, "Error loading lots by category", ApiResponse.Status.ERROR));
        }
    }

    // @GetMapping(value = "/market/meshok/categories/count/{mid}")
    // public ResponseEntity<ApiResponse<Integer>>
    // getCountLotsByCategory(@PathVariable(name = "mid") int mid) {
    // int count = MeshokUtil.getCountLotsByCategory(mid);
    // return ResponseEntity
    // .ok(new ApiResponse<>(count, "Count lots by category fetched successfully",
    // ApiResponse.Status.SUCCESS));
    // }

    // @GetMapping(value = "/market/meshok/lots/bycategory/{categoryMid}")
    // public ResponseEntity<ApiResponse<List<MeshokLot>>> getLotsByCategory(
    // @PathVariable(name = "categoryMid") int categoryMid) {
    // List<MeshokLot> lots =
    // N4JUtil.getInstance().meshokService.meshokLotService.findByCategoryMid(categoryMid);

    // return ResponseEntity
    // .ok(new ApiResponse<>(lots, "Lots fetched successfully",
    // ApiResponse.Status.SUCCESS));
    // }

    @GetMapping(value = "/market/meshok/lots/bycategory")
    public ResponseEntity<ApiResponse<List<MeshokLot>>> getLotsByCategory(
            @RequestParam(name = "categoryMid") int categoryMid) {
        List<MeshokLot> lots = N4JUtil.getInstance().meshokService.meshokLotService.findByCategoryMid(categoryMid);

        return ResponseEntity
                .ok(new ApiResponse<>(lots, "Lots fetched successfully", ApiResponse.Status.SUCCESS));
    }

    @GetMapping(value = "/market/meshok/lot/image/{mid}/{imageNumber}")
    public ResponseEntity<Resource> getMeshokLotImage(
            @PathVariable(name = "mid") int mid,
            @PathVariable(name = "imageNumber") int imageNumber) {
        try {

            MeshokLot lot = N4JUtil.getInstance().meshokService.meshokLotService.findByMid(mid);

            Resource resource = ImageUtil.getMeshokLotLocalImage(lot, imageNumber);

            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type based on file extension
            String contentType = determineContentType(resource.getFilename());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(resource.contentLength());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String determineContentType(String imagePath) {
        String lowerCasePath = imagePath.toLowerCase();
        if (lowerCasePath.endsWith(".jpg") || lowerCasePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerCasePath.endsWith(".png")) {
            return "image/png";
        } else if (lowerCasePath.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerCasePath.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerCasePath.endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            // Default to octet-stream for unknown types
            return "application/octet-stream";
        }
    }
}
