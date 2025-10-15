package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CompositionType;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CompositionTypeService extends AbstractService<CompositionType, CompositionTypeRepository> {
    public CompositionTypeService(CompositionTypeRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing

    public UUID findUuidByCode(String code) {
        return findUuidByPropertyStringValue(CompositionType.LABEL, "code", code);
    }

    public void setCode(UUID compositionTypeUuid, String code) {
        setPropertyStringValue(compositionTypeUuid, "code", code);
    }

    public boolean compareCode(UUID compositionTypeUuid, String code) {
        return comparePropertyValue(compositionTypeUuid, "code", code, String.class);
    }

    // End: Methods for Numista parsing

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
