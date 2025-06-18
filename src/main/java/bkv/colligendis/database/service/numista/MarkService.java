package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Mark;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class MarkService extends AbstractService<Mark, MarkRepository> {
    public MarkService(MarkRepository repository) {
        super(repository);
    }

    public Mark findByNid(String nid){
        Mark mark = repository.findByNid(nid);
        if (mark == null) {
            return repository.save(new Mark(nid));
        }
        return mark;
    }

}
