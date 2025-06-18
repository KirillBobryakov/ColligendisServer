package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Composition;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class CompositionService extends AbstractService<Composition, CompositionRepository> {
    public CompositionService(CompositionRepository repository) {
        super(repository);
    }

}
