//package bkv.colligendis.rest;
//
//
//import bkv.colligendis.services.features.ItemService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.UUID;
//
//@RestController
//public class CoinService {
//
//    private final ItemService coinInformationService;
//
//    public CoinService(ItemService coinInformationService) {
//        this.coinInformationService = coinInformationService;
//    }
//
//    @GetMapping(value = "/coin/cointype/{coinId}/{newCoinType}")
//    public ResponseEntity<Boolean> readCoinInformation(@PathVariable(name = "coinId") String coinId, @PathVariable(name = "newCoinType") String newCoinType){
//        UUID coinEid = UUID.fromString(coinId);
//
//        boolean result = coinInformationService.setItemType(coinEid, newCoinType);
//        return result
//                ? ResponseEntity.ok(true)
//                : ResponseEntity.notFound().build();
//    }
//
//
//
//
//
//}
