package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Composition;
import bkv.colligendis.database.entity.numista.Metal;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class CompositionService extends AbstractService<Composition, CompositionRepository> {
    public CompositionService(CompositionRepository repository) {
        super(repository);
    }

    @Override
    public Composition setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
