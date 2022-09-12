package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.CoinVariant;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class CoinVariantService extends AbstractService<CoinVariant, CoinVariantRepository> {
    public CoinVariantService(CoinVariantRepository repository) {
        super(repository);
    }
}
