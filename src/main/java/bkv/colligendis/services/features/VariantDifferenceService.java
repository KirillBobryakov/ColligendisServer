package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.item.VariantDifference;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class VariantDifferenceService extends AbstractService<VariantDifference, VariantDifferenceRepository> {
    public VariantDifferenceService(VariantDifferenceRepository repository) {
        super(repository);
    }

    public VariantDifference setPropertyValue(Long id, String name, String value) {
        return null;
    }

}
