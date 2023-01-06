package bkv.colligendis.rest;


import bkv.colligendis.database.entity.piece.Item;
import bkv.colligendis.database.entity.piece.ItemSide;
import bkv.colligendis.services.features.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemRestService {


    private final ItemService itemService;

    public ItemRestService(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping(value = "/item/{numistaNumber}")
    public ResponseEntity<Item> getItem(@PathVariable(name = "numistaNumber") int numistaNumber){
        final Item item = itemService.findByNumistaNumber(String.valueOf(numistaNumber));
        return item != null
                ? ResponseEntity.ok(item)
                : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/item/side/{itemSideId}")
    @ResponseBody
    public ResponseEntity<ItemSide> getItemSide(@PathVariable(name = "itemSideId") Long itemSideId){
        final ItemSide itemSide = itemService.getItemSide(itemSideId);
        return itemSide != null
                ? ResponseEntity.ok(itemSide)
                : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/item/side/reload/{itemSideId}")
    @ResponseBody
    public ResponseEntity<ItemSide> reloadItemSide(@PathVariable(name = "itemSideId") Long itemSideId){
        ItemSide itemSide = itemService.reloadItemSide(itemSideId);
        return itemSide != null
                ? ResponseEntity.ok(itemSide)
                : ResponseEntity.notFound().build();
    }







}
