package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.ITEM_TYPE;
import bkv.colligendis.database.entity.piece.Item;
import bkv.colligendis.database.entity.piece.Variant;
import bkv.colligendis.database.entity.piece.ItemSide;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.NumistaUtil;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class ItemService extends AbstractService<Item, ItemRepository> {

    private final VariantRepository variantRepository;
    private final CoinCommentRepository coinCommentRepository;

    private final ItemSideRepository itemSideRepository;

    public ItemService(ItemRepository repository,
                       VariantRepository variantRepository,
                       CoinCommentRepository coinCommentRepository,
                       ItemSideRepository itemSideRepository) {
        super(repository);
        this.variantRepository = variantRepository;
        this.coinCommentRepository = coinCommentRepository;
        this.itemSideRepository = itemSideRepository;
    }

    public Item findByNumistaNumber(String numistaNumber) {
        return repository.findByNumistaNumber(numistaNumber).block();
    }

    public boolean deleteExistCoinVariants(Item item) {
        if (item.getVariants().size() > 0) {
            for (Variant variant : item.getVariants()) {
                variantRepository.delete(variant).block();
            }
            item.getVariants().clear();
            return true;
        }
        return false;
    }

    public boolean setItemType(Long itemId, String newItemType){
        Item item = repository.findById(itemId).block();
        if(item == null) return false;

        item.setItemType(ITEM_TYPE.valueOf(newItemType));
        repository.save(item).block();

        return true;
    }
    public ItemSide getItemSide(Long itemSideId) {
        return itemSideRepository.findById(itemSideId).block();
    }

    public ItemSide reloadItemSide(Long itemSideId) {

        Item item = repository.findByPieceSideId(itemSideId).block();
        if (item == null) return null;

        Document numistaPage = NumistaUtil.getNumistaPage(item.getNumistaNumber());
        if (numistaPage == null) return null;

        ItemSide itemSide = itemSideRepository.findById(itemSideId).block();
        if (itemSide == null) return null;

        ItemSide freshItemSide = NumistaUtil.loadPieceSide(numistaPage, itemSide.getSideType());
        if (freshItemSide == null) return null;

        itemSide.merge(freshItemSide);

        itemSideRepository.save(itemSide).block();

        return itemSide;
    }

}
