package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mark;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class MarkService extends AbstractService<Mark, MarkRepository> {
    public MarkService(MarkRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing
    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Mark.LABEL, "nid", nid);
    }
    // End: Methods for Numista parsing

    public Mark findByNid(String nid) {
        Mark mark = repository.findByNid(nid);
        if (mark == null) {
            return repository.save(new Mark(nid));
        }
        return mark;
    }

}
