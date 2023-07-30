package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.item.CoinComment;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class CoinCommentService extends AbstractService<CoinComment, CoinCommentRepository> {
    public CoinCommentService(CoinCommentRepository repository) {
        super(repository);
    }


    @Override
    public CoinComment setPropertyValue(Long id, String name, String value) {
        return repository.setPropertyValue(id, name, value).block();
    }
}
