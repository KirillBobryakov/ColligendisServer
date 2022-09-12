package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.PieceSide;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class CoinPartService extends AbstractService<PieceSide, CoinPartRepository> {
    public CoinPartService(CoinPartRepository repository) {
        super(repository);
    }
}
