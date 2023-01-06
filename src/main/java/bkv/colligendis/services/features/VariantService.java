package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.Variant;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class VariantService extends AbstractService<Variant, VariantRepository> {
    public VariantService(VariantRepository repository) {
        super(repository);
    }
}
