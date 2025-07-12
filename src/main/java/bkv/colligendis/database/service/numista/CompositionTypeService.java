package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CompositionType;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class CompositionTypeService extends AbstractService<CompositionType, CompositionTypeRepository> {
    public CompositionTypeService(CompositionTypeRepository repository) {
        super(repository);
    }

    public CompositionType findByCode(String code) {
        return repository.findByCode(code);
    }

    public CompositionType findByCodeOrSave(String code, String name) {
        CompositionType compositionType = findByCode(code);
        if (compositionType == null) {
            compositionType = new CompositionType(code, name);
            save(compositionType);
        }
        return compositionType;
    }
}
