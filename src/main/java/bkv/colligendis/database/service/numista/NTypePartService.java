package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.NTypePart;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class NTypePartService extends AbstractService<NTypePart, NTypePartRepository> {

    public NTypePartService(NTypePartRepository repository) {
        super(repository);
    }


}
