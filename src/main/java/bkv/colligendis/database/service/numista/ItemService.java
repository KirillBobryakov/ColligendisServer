package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Item;
import bkv.colligendis.rest.dto.ItemDAO;
import bkv.colligendis.services.AbstractService;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ItemService extends AbstractService<Item, ItemRepository> {

    private final ModelMapper modelMapper;

    public ItemService(ItemRepository repository, ModelMapper modelMapper) {
        super(repository);
        this.modelMapper = modelMapper;
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public List<ItemDAO> findItemsByVariantNid(String variantNid) {
        List<Item> items = repository.findItemsByVariantNid(variantNid);
        return items.stream()
                .map(item -> modelMapper.map(item, ItemDAO.class))
                .collect(Collectors.toList());
    }

    public boolean delete(String uuid) {
        boolean result = repository.deleteByUuid(uuid);
        return result;
    }

    public Integer countItemsByVariantNid(String variantNid) {
        return repository.countItemsByVariantNid(variantNid);
    }

    public boolean save(ItemDAO itemDAO) {
        Item item = modelMapper.map(itemDAO, Item.class);
        item = super.save(item);

        boolean variantConnected = repository.connectItemToVariant(item.getUuid().toString(), itemDAO.getVariantNid());
        boolean userConnected = repository.connectItemToUser(item.getUuid().toString(), itemDAO.getUserUuid());

        return variantConnected && userConnected;
    }

}
