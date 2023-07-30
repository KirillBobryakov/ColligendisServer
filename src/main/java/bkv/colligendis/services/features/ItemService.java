package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.ITEM_TYPE;
import bkv.colligendis.database.entity.item.Item;
import bkv.colligendis.database.entity.item.ItemSide;
import bkv.colligendis.database.entity.numista.Variant;
import bkv.colligendis.database.service.numista.VariantRepository;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.NumistaUtil;
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

    public boolean exists(String numistaNumber){
        return Boolean.TRUE.equals(repository.existsByNumistaNumber(numistaNumber).block());
    }

    public Item findByNumistaNumber(String numistaNumber) {
        return repository.findByNumistaNumber(numistaNumber).block();
    }


    public Item loadFromNumista(String numistaNumber) {
        return NumistaUtil.getInstance().loadItem(numistaNumber);
    }

    public boolean deleteExistingVariants(Item item) {
        if (item.getVariants().size() > 0) {
            for (Variant variant : item.getVariants()) {
                variantRepository.delete(variant).block();
            }
            item.getVariants().clear();
            return true;
        }
        return false;
    }

    public boolean setItemType(long itemId, String newItemType){
        Item item = repository.findById(itemId).block();
        if(item == null) return false;

        item.setItemType(ITEM_TYPE.valueOf(newItemType));
        repository.save(item).block();

        return true;
    }
    public ItemSide getItemSide(long itemSideId) {
        return itemSideRepository.findById(itemSideId).block();
    }

    public ItemSide reloadItemSide(long itemSideId) {

        Item item = repository.findByPieceSideId(itemSideId).block();
        if (item == null) return null;

        ItemSide itemSide = itemSideRepository.findById(itemSideId).block();
        if (itemSide == null) return null;

        ItemSide freshItemSide = NumistaUtil.getInstance().loadItemSide(item.getNumistaNumber(), itemSide.getSideType());

        itemSide.merge(freshItemSide);

        itemSideRepository.save(itemSide).block();

        return itemSide;
    }

    public Item reloadItemProperties(long itemId) {

        Item item = repository.findById(itemId).block();
        if (item == null) return null;

        item = NumistaUtil.getInstance().loadItemProperties(item.getNumistaNumber(), item);

        repository.save(item).block();

        return item;
    }

    @Override
    public Item setPropertyValue(Long id, String name, String value) {
        return repository.setPropertyValue(id, name, value).block();
    }
}
