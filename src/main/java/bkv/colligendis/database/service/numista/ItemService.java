package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Item;
import bkv.colligendis.services.AbstractService;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ItemService extends AbstractService<Item, ItemRepository> {
    public ItemService(ItemRepository repository) {
        super(repository);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public List<Item> findItemsByVariantNid(String variantNid) {
        return repository.findItemsByVariantNid(variantNid);
    }

    public void delete(String uuid) {
        repository.deleteByUuid(uuid);
    }

    public Integer countItemsByVariantNid(String variantNid) {
        return repository.countItemsByVariantNid(variantNid);
    }

}
