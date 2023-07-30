package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.NTag;
import bkv.colligendis.database.entity.numista.Printer;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class NTagService extends AbstractService<NTag, NTagRepository> {
    public NTagService(NTagRepository repository) {
        super(repository);
    }

    public NTag findByNid(String nid, String name){
        NTag nTag = repository.findByNid(nid).block();
        if (nTag != null) {
            if(!nTag.getName().equals(name)){
                DebugUtil.showServiceMessage(this, "Trying to find NTag with nid=" + nid + " and name=" + name
                        + ". But there is a NTag with the same nid and other name = " + nTag.getName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "NTag.name was updated.");
                nTag.setName(name);
                return repository.save(nTag).block();
            }
        } else {
            DebugUtil.showInfo(this, "New NTag with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new NTag(nid, name)).block();
        }
        return nTag;
    }


    @Override
    public NTag setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
