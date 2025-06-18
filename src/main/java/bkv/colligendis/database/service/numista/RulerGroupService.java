package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.RulerGroup;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RulerGroupService extends AbstractService<RulerGroup, RulerGroupRepository> {

    public RulerGroupService(RulerGroupRepository repository) {
        super(repository);
    }

    /**
     *  Find RulerGroup by {@code nid}.
     *
     * @param nid RulerGroup's nid (Numista RulerGroup id)
     * @return If RulerGroup with {@code nid} exists, then return RulerGroup
     */
    public RulerGroup findRulerGroupByNid(String nid){
        return repository.findRulerGroupByNid(nid);
    }

    /**
     *  Find RulerGroup's {@code eid} UUID by {@code name}.
     *
     * @param name RulerGroup's name (Numista RulerGroup name)
     * @return If RulerGroup with {@code name} exists, then return RulerGroup's UUID
     */
    public UUID findRulerGroupUuidByName(String name){
        String rulerGroupUuid = repository.findUuidByName(name);
        return rulerGroupUuid != null ? UUID.fromString(rulerGroupUuid) : null;
    }

}
