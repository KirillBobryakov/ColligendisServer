package bkv.colligendis.rest;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bkv.colligendis.database.entity.User;
import bkv.colligendis.database.service.users.UserService;
import bkv.colligendis.rest.dto.ItemDAO;
import bkv.colligendis.utils.N4JUtil;

@RestController
@RequestMapping("/database/item")
public class ItemRestController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public ItemRestController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    /**
     * Get the current authenticated user from Spring Security context
     * 
     * @return User object or null if not authenticated
     */
    private String getCurrentUserUuid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String userUuid = ((User) authentication.getPrincipal()).getUuid().toString();
            return userUuid;
        }
        return null;
    }

    @GetMapping(value = "/byVariant")
    public ResponseEntity<ApiResponse<List<ItemDAO>>> getItemsByVariant(
            @RequestParam(name = "variantNid") String variantNid) {

        List<ItemDAO> items = N4JUtil.getInstance().numistaService.itemService.findItemsByVariantNid(variantNid);

        return ResponseEntity.ok(new ApiResponse<>(items, "Items fetched successfully", ApiResponse.Status.SUCCESS));
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Boolean>> saveItem(@RequestBody ItemDAO itemDAO) {
        // Get current authenticated user
        String currentUserUuid = getCurrentUserUuid();
        if (currentUserUuid == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "User not authenticated", ApiResponse.Status.ERROR));
        }

        // Set the user UUID in the itemDAO
        itemDAO.setUserUuid(currentUserUuid);

        boolean result = N4JUtil.getInstance().numistaService.itemService.save(itemDAO);
        if (!result) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Item not saved", ApiResponse.Status.ERROR));
        }
        return ResponseEntity.ok(new ApiResponse<>(result, "Item saved successfully", ApiResponse.Status.SUCCESS));
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<ApiResponse<Boolean>> deleteItem(@RequestParam(name = "uuid") String uuid) {
        boolean result = N4JUtil.getInstance().numistaService.itemService.delete(uuid);
        if (!result) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Item not deleted", ApiResponse.Status.ERROR));
        }
        return ResponseEntity.ok(new ApiResponse<>(result, "Item deleted successfully", ApiResponse.Status.SUCCESS));
    }

}
//
//
// private final ServiceUtils serviceUtils;
//
// public ItemRestService(ServiceUtils serviceUtils) {
// this.serviceUtils = serviceUtils;
// }
//
// @GetMapping(value = "/item/id/{itemId}")
// public ResponseEntity<Item> getItemById(@PathVariable(name = "itemId") String
// itemId) {
// UUID eid = UUID.fromString(itemId);
// final Item item = serviceUtils.itemService.findByEid(eid);
// return item != null
// ? ResponseEntity.ok(item)
// : ResponseEntity.notFound().build();
// }
//
// @GetMapping(value = "/item/{numistaNumber}")
// public ResponseEntity<Item> getItemByNumistaNumber(@PathVariable(name =
// "numistaNumber") String numistaNumber) {
// final Item item =
// serviceUtils.itemService.findByNumistaNumber(numistaNumber);
// return item != null
// ? ResponseEntity.ok(item)
// : ResponseEntity.notFound().build();
// }
//
// @GetMapping(value = "/item/load/{numistaNumber}")
// public ResponseEntity<Item> loadItemFromNumista(@PathVariable(name =
// "numistaNumber") String numistaNumber) {
//
//// if(itemService.exists(numistaNumber)){
//// return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).build();
//// }
// Item item = serviceUtils.itemService.loadFromNumista(numistaNumber);
// return item != null
// ? ResponseEntity.ok(item)
// : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
// }
//
// @GetMapping(value = "/item/side/{itemSideId}")
// @ResponseBody
// public ResponseEntity<ItemSide> getItemSide(@PathVariable(name =
// "itemSideId") long itemSideId) {
// final ItemSide itemSide = serviceUtils.itemService.getItemSide(itemSideId);
// return itemSide != null
// ? ResponseEntity.ok(itemSide)
// : ResponseEntity.notFound().build();
// }
//
//
// @GetMapping(value = "/item/side/reload/{itemSideId}")
// @ResponseBody
// public ResponseEntity<ItemSide> reloadItemSide(@PathVariable(name =
// "itemSideId") long itemSideId) {
// ItemSide itemSide = serviceUtils.itemService.reloadItemSide(itemSideId);
// return itemSide != null
// ? ResponseEntity.ok(itemSide)
// : ResponseEntity.notFound().build();
// }
//
// @GetMapping(value = "/item/properties/reload/{itemId}")
// @ResponseBody
// public ResponseEntity<Item> reloadItemProperties(@PathVariable(name =
// "itemId") long itemId) {
// Item item = serviceUtils.itemService.reloadItemProperties(itemId);
// return item != null
// ? ResponseEntity.ok(item)
// : ResponseEntity.notFound().build();
// }
//
//
// @GetMapping(value = "/item/itemtype/{itemId}/{newItemType}")
// public ResponseEntity<Boolean> changeItemType(@PathVariable(name = "itemId")
// long itemId, @PathVariable(name = "newItemType") String newItemType) {
// boolean result = serviceUtils.itemService.setItemType(itemId, newItemType);
// return result
// ? ResponseEntity.ok(true)
// : ResponseEntity.notFound().build();
// }
//
//
//
// @GetMapping(value =
// "{nodeName}/property/set/{nodeId}/{propertyName}/{value}")
// public ResponseEntity<Boolean> changeItemType(@PathVariable(name =
// "nodeName") String nodeName, @PathVariable(name = "nodeId") String nodeId,
// @PathVariable(name = "propertyName") String propertyName,
// @PathVariable(name = "value") String value) {
// if (value.equals("|")) value = "";
// value = value.replaceAll("~~", "/");
// value = value.replaceAll("@@", "?");
//
// UUID eid = UUID.fromString(nodeId);
//
// switch (nodeName) {
// case "ITEM" -> {
// Item item = serviceUtils.itemService.findByEid(eid);
// if (item != null) {
// if (set(item, propertyName, value)) {
// return serviceUtils.itemService.save(item) != null ? ResponseEntity.ok(true)
// : ResponseEntity.notFound().build();
// }
// }
// }
// case "TERRITORY" -> {
// Territory territory = serviceUtils.territoryService.findByEid(eid);
// if (territory != null) {
// if (set(territory, propertyName, value)) {
// return serviceUtils.territoryService.save(territory) != null ?
// ResponseEntity.ok(true)
// : ResponseEntity.notFound().build();
// }
//
// }
// }
// case "ISSUER" -> {
// Issuer issuer = serviceUtils.issuerService.findByEid(eid);
// if (issuer != null) {
// if (set(issuer, propertyName, value)) {
// return serviceUtils.issuerService.save(issuer) != null ?
// ResponseEntity.ok(true)
// : ResponseEntity.notFound().build();
// }
//
// }
// }
// case "COMM_ISSUE_SERIES" -> {
// CommIssueSeries commIssueSeries =
// serviceUtils.commIssueSeriesService.findByEid(eid);
// if (commIssueSeries != null) {
// if (set(commIssueSeries, propertyName, value)) {
// return serviceUtils.commIssueSeriesService.save(commIssueSeries) != null ?
// ResponseEntity.ok(true)
// : ResponseEntity.notFound().build();
// }
//
// }
// }
// case "PERIOD" -> {
// Period period = serviceUtils.periodService.findByEid(eid);
// if (period != null) {
// if (set(period, propertyName, value)) {
// return serviceUtils.periodService.save(period) != null ?
// ResponseEntity.ok(true)
// : ResponseEntity.notFound().build();
// }
//
// }
// }
// case "VALUE" -> {
// Value val = serviceUtils.valueService.findByEid(eid);
// if (val != null) {
// if (set(val, propertyName, value)) {
// return serviceUtils.valueService.save(val) != null ? ResponseEntity.ok(true)
// : ResponseEntity.notFound().build();
// }
//
// }
// }
// case "CURRENCY" -> {
// Currency currency = serviceUtils.currencyService.findByEid(eid);
// if (currency != null) {
// if (set(currency, propertyName, value)) {
// return serviceUtils.currencyService.save(currency) != null ?
// ResponseEntity.ok(true)
// : ResponseEntity.notFound().build();
// }
//
// }
// }
// case "ITEM_SIDE" -> {
// ItemSide itemSide = serviceUtils.itemSideService.findByEid(eid);
// if (itemSide != null) {
// if (set(itemSide, propertyName, value)) {
// return serviceUtils.itemSideService.save(itemSide) != null ?
// ResponseEntity.ok(true)
// : ResponseEntity.notFound().build();
// }
//
// }
// }
// case "MINT" -> {
// Mint mint = serviceUtils.mintService.findByEid(eid);
// if (mint != null) {
// if (set(mint, propertyName, value)) {
// Mint savedMint = serviceUtils.mintService.save(mint);
//
// return savedMint != null ? ResponseEntity.ok(true)
// : ResponseEntity.notFound().build();
// }
//
// }
// }
// }
// return ResponseEntity.notFound().build();
// }
//
//
//
// public static boolean set(Object object, String fieldName, Object fieldValue)
// {
// Class<?> clazz = object.getClass();
// while (clazz != null) {
// try {
// Field field = clazz.getDeclaredField(fieldName);
// field.setAccessible(true);
// if (field.getType().getName().equals("int")) {
// if (fieldValue.toString().isEmpty()) {
// field.set(object, 0);
// } else {
// field.set(object, Integer.valueOf(fieldValue.toString()));
// }
// } else if (field.getType().getName().equals("float")) {
// if (fieldValue.toString().isEmpty()) {
// field.set(object, (float) 0);
// } else {
// field.set(object, Float.valueOf(fieldValue.toString()));
// }
// } else {
// field.set(object, fieldValue);
// }
// return true;
// } catch (NoSuchFieldException e) {
// clazz = clazz.getSuperclass();
// } catch (Exception e) {
// throw new IllegalStateException(e);
// }
// }
// return false;
// }
//
//
// @PostMapping(value = "/mint/save")
// public ResponseEntity<Mint> saveMint(@RequestBody Mint mint) {
// return ResponseEntity.ok(serviceUtils.mintService.save(mint));
// }
//
// @GetMapping(value = "/item/{itemId}/mints/new")
// @ResponseBody
// public ResponseEntity<Set<Mint>> createNewMint(@PathVariable(name = "itemId")
// String itemId) {
// UUID eid = UUID.fromString(itemId);
//
// Item item = serviceUtils.itemService.findByEid(eid);
// if (item != null) {
// item.getMints().add(new Mint("none", "none"));
// serviceUtils.itemService.save(item);
// return ResponseEntity.ok(item.getMints());
// } else {
// return ResponseEntity.notFound().build();
// }
// }
//
// @GetMapping(value = "/item/{itemId}/mints/detachAndDelete/{mintId}")
// @ResponseBody
// public ResponseEntity<Boolean> detachAndDeleteMint(@PathVariable(name =
// "itemId") String itemId, @PathVariable(name = "mintId") String mintId) {
// UUID itemEid = UUID.fromString(itemId);
// UUID mintEid = UUID.fromString(mintId);
//
// Item item = serviceUtils.itemService.findByEid(itemEid);
// Mint mint = serviceUtils.mintService.findByEid(mintEid);
// if(item != null && mint != null){
// item.getMints().remove(mint);
//
// if(serviceUtils.mintService.relationCount(mintId) <= 1){
// serviceUtils.mintService.delete(mintId);
// }
// serviceUtils.itemService.save(item);
// }
// return ResponseEntity.ok(true);
// }
// @GetMapping(value = "/item/{itemId}/mints/detach/{mintId}")
// @ResponseBody
// public ResponseEntity<Boolean> detachMint(@PathVariable(name = "itemId")
// String itemId, @PathVariable(name = "mintId") String mintId) {
// UUID itemEid = UUID.fromString(itemId);
// UUID mintEid = UUID.fromString(mintId);
//
// Item item = serviceUtils.itemService.findByEid(itemEid);
// Mint mint = serviceUtils.mintService.findByEid(mintEid);
// if(item != null && mint != null){
// item.getMints().remove(mint);
// serviceUtils.itemService.save(item);
// }
// return ResponseEntity.ok(true);
// }
//
// @GetMapping(value = "/mints/filter/name/{nameFilter}")
// @ResponseBody
// public ResponseEntity<List<Mint>> getMintsWithNameFilter(@PathVariable(name =
// "nameFilter") String nameFilter) {
//
// List<Mint> mints = serviceUtils.mintService.findByNameFilter(nameFilter);
// if(mints == null || mints.isEmpty()){
// return ResponseEntity.ok(new ArrayList<>());
// } else return ResponseEntity.ok(mints);
// }
//
// @GetMapping(value = "/item/{itemId}/mints/add/{mintId}")
// @ResponseBody
// public ResponseEntity<Boolean> addMintToItem(@PathVariable(name = "itemId")
// String itemId, @PathVariable(name = "mintId") String mintId) {
// UUID itemEid = UUID.fromString(itemId);
// UUID mintEid = UUID.fromString(mintId);
//
// Item item = serviceUtils.itemService.findByEid(itemEid);
// Mint mint = serviceUtils.mintService.findByEid(mintEid);
// if(item != null && mint != null && !item.getMints().contains(mint)){
// item.getMints().add(mint);
// serviceUtils.itemService.save(item);
// }
// return ResponseEntity.ok(true);
// }
//
//// @PostMapping(value = "/mark/save")
//// public ResponseEntity<Mintmark> saveMark(@RequestBody Mintmark mintmark) {
//// Mintmark mintmark1 = serviceUtils.markService.save(mintmark);
//// return ResponseEntity.ok(mintmark1);
//// }
//
// @GetMapping(value = "/item/{itemId}/marks/new")
// @ResponseBody
// public ResponseEntity<Set<SpecifiedMint>> createNewMark(@PathVariable(name =
// "itemId") String itemId) {
// UUID itemEid = UUID.fromString(itemId);
//
// Item item = serviceUtils.itemService.findByEid(itemEid);
// if (item != null) {
// item.getMarks().add(new SpecifiedMint("none"));
// serviceUtils.itemService.save(item);
// return ResponseEntity.ok(item.getMarks());
// } else {
// return ResponseEntity.notFound().build();
// }
// }
//
//// @GetMapping(value = "/marks/filter/name/{nameFilter}")
//// @ResponseBody
//// public ResponseEntity<List<Mintmark>>
// getMarksWithNameFilter(@PathVariable(name = "nameFilter") String nameFilter)
// {
//// return
// ResponseEntity.ok(serviceUtils.markService.findByNameFilter(nameFilter));
//// }
////
//// @GetMapping(value = "/item/{itemId}/marks/add/{markId}")
//// @ResponseBody
//// public ResponseEntity<Boolean> addMarkToItem(@PathVariable(name = "itemId")
// Long itemId, @PathVariable(name = "markId") Long markId) {
//// Item item = serviceUtils.itemService.findById(itemId);
//// Mintmark mintmark = serviceUtils.markService.findById(markId);
//// if(item != null && mintmark != null &&
// !item.getMarks().contains(mintmark)){
//// item.getMarks().add(mintmark);
//// serviceUtils.itemService.save(item);
//// }
//// return ResponseEntity.ok(true);
//// }
//////
////
//// @GetMapping(value = "/item/{itemId}/marks/detachAndDelete/{markId}")
//// @ResponseBody
//// public ResponseEntity<Boolean> detachAndDeleteMark(@PathVariable(name =
// "itemId") Long itemId, @PathVariable(name = "markId") Long markId) {
//// Item item = serviceUtils.itemService.findById(itemId);
//// Mintmark mintmark = serviceUtils.markService.findById(markId);
//// if(item != null && mintmark != null){
//// item.getMarks().remove(mintmark);
////
//// if(serviceUtils.markService.relationCount(markId) <= 1){
//// serviceUtils.markService.delete(markId);
//// }
//// serviceUtils.itemService.save(item);
//// }
//// return ResponseEntity.ok(true);
////// }
//// @GetMapping(value = "/item/{itemId}/marks/detach/{markId}")
//// @ResponseBody
//// public ResponseEntity<Boolean> detachMark(@PathVariable(name = "itemId")
// Long itemId, @PathVariable(name = "markId") Long markId) {
//// Item item = serviceUtils.itemService.findById(itemId);
//// Mintmark mintmark = serviceUtils.markService.findById(markId);
//// if(item != null && mintmark != null){
//// item.getMarks().remove(mintmark);
//// serviceUtils.itemService.save(item);
//// }
//// return ResponseEntity.ok(true);
//// }
////
//
// @PostMapping(value = "/variant/save")
// public ResponseEntity<Variant> saveVariant(@RequestBody Variant variant) {
// Variant variant1 = serviceUtils.variantService.save(variant);
// return ResponseEntity.ok(variant1);
// }
//
//
// @GetMapping(value = "/variant/{variantId}/mint/attach/{mintId}")
// @ResponseBody
// public ResponseEntity<Boolean> attachMintToVariant(@PathVariable(name =
// "variantId") String variantId, @PathVariable(name = "mintId") String mintId)
// {
// UUID variantEid = UUID.fromString(variantId);
// UUID mintEid = UUID.fromString(mintId);
//
//
// Variant variant = serviceUtils.variantService.findByEid(variantEid);
// Mint mint = serviceUtils.mintService.findByEid(mintEid);
//
// if(variant != null && mint != null && variant.getMint() != mint){
// variant.setMint(mint);
// serviceUtils.variantService.save(variant);
// }
// return ResponseEntity.ok(true);
// }
//
// @GetMapping(value = "/variant/{variantId}/mint/detach")
// @ResponseBody
// public ResponseEntity<Boolean> detachMintFromVariant(@PathVariable(name =
// "variantId") String variantId) {
// UUID variantEid = UUID.fromString(variantId);
//
// Variant variant = serviceUtils.variantService.findByEid(variantEid);
// if(variant != null && variant.getMint() != null){
// variant.setMint(null);
// serviceUtils.variantService.save(variant);
// return ResponseEntity.ok(true);
// }
//
// return ResponseEntity.notFound().build();
// }
////
//// @GetMapping(value = "/variant/{variantId}/mark/attach/{markId}")
//// @ResponseBody
//// public ResponseEntity<Boolean> attachMarkToVariant(@PathVariable(name =
// "variantId") Long variantId, @PathVariable(name = "markId") Long markId) {
//// Variant variant = serviceUtils.variantService.findById(variantId);
//// Mintmark mintmark = serviceUtils.markService.findById(markId);
////
//// if(variant != null && mintmark != null && variant.getMintmark() !=
// mintmark){
//// variant.setMintmark(mintmark);
//// serviceUtils.variantService.save(variant);
//// }
//// return ResponseEntity.ok(true);
//// }
//
// @GetMapping(value = "/variant/{variantId}/mark/detach")
// @ResponseBody
// public ResponseEntity<Boolean> detachMarkFromVariant(@PathVariable(name =
// "variantId") String variantId) {
// UUID variantEid = UUID.fromString(variantId);
//
// Variant variant = serviceUtils.variantService.findByEid(variantEid);
//// if(variant != null && variant.getMintmark() != null){
//// variant.setMintmark(null);
//// serviceUtils.variantService.save(variant);
//// return ResponseEntity.ok(true);
//// }
// return ResponseEntity.notFound().build();
// }
//
//
// @PostMapping(value = "/variant/difference/save")
// public ResponseEntity<VariantDifference> saveVariantDifference(@RequestBody
// VariantDifference variantDifference) {
// return
// ResponseEntity.ok(serviceUtils.variantDifferenceService.save(variantDifference));
// }
//
// @GetMapping(value = "/variant/{variantId}/difference/new")
// @ResponseBody
// public ResponseEntity<VariantDifference>
// createNewVariantDifference(@PathVariable(name = "variantId") String
// variantId) {
// UUID variantEid = UUID.fromString(variantId);
//
// Variant variant = serviceUtils.variantService.findByEid(variantEid);
// if (variant != null && variant.getVariantDifference() == null) {
// variant.setVariantDifference(new VariantDifference());
// serviceUtils.variantService.save(variant);
// return ResponseEntity.ok(variant.getVariantDifference());
// } else {
// return ResponseEntity.notFound().build();
// }
// }
//
////
////
//// @GetMapping(value = "/variant/{variantId}/difference/delete")
//// @ResponseBody
//// public ResponseEntity<Boolean> deleteVariantDifference(@PathVariable(name =
// "variantId") long variantId) {
//// Variant variant = serviceUtils.variantService.findByEid(variantId);
//// if(variant != null && variant.getVariantDifference() != null){
//// serviceUtils.variantDifferenceService.delete(variant.getVariantDifference().getId());
//// return ResponseEntity.ok(true);
//// }
//// return ResponseEntity.notFound().build();
//// }
//////
//// @GetMapping(value = "/image/{territory}/difference/delete")
//// public ResponseEntity<Resource> download(@PathVariable(name = "territory")
// Long territory) throws IOException {
////
////
//// InputStreamResource resource = new InputStreamResource(new
// FileInputStream(file));
////
//// return ResponseEntity.ok()
////// .headers(headers)
//// .contentLength(file.length())
//// .contentType(MediaType.APPLICATION_OCTET_STREAM)
//// .body(resource);
//// }
//
// @GetMapping(value = "/n4jdb/init")
// public ResponseEntity<Boolean> initN4JDB() {
// N4JUtil.getInstance().initN4JDB();
// return ResponseEntity.ok(true);
// }
// }
