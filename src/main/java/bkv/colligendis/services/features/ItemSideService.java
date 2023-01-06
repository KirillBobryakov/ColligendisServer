package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.ItemSide;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class ItemSideService extends AbstractService<ItemSide, ItemSideRepository> {
    public ItemSideService(ItemSideRepository repository) {
        super(repository);
    }
}
