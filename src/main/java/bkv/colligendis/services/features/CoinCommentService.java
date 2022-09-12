package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.CoinComment;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class CoinCommentService extends AbstractService<CoinComment, CoinCommentRepository> {
    public CoinCommentService(CoinCommentRepository repository) {
        super(repository);
    }
}
