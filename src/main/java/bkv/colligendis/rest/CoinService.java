package bkv.colligendis.rest;


import bkv.colligendis.services.features.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoinService {


    private final ItemService coinInformationService;

    public CoinService(ItemService coinInformationService) {
        this.coinInformationService = coinInformationService;
    }

    @GetMapping(value = "/coin/cointype/{coinId}/{newCoinType}")
    public ResponseEntity<Boolean> readCoinInformation(@PathVariable(name = "coinId") Long coinId, @PathVariable(name = "newCoinType") String newCoinType){
        boolean result = coinInformationService.setItemType(coinId, newCoinType);
        return result
                ? ResponseEntity.ok(true)
                : ResponseEntity.notFound().build();
    }





}
