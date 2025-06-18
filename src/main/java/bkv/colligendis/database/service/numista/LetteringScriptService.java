package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.LetteringScript;
import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.database.entity.numista.Shape;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class LetteringScriptService extends AbstractService<LetteringScript, LetteringScriptRepository> {
    public LetteringScriptService(LetteringScriptRepository repository) {
        super(repository);
    }

    public LetteringScript findByNid(String nid, String name){
        LetteringScript letteringScript = repository.findByNid(nid);
        if (letteringScript != null) {
            if(!letteringScript.getName().equals(name)){
                DebugUtil.showServiceMessage(this, "Trying to find LetteringScript with nid=" + nid + " and name=" + name
                        + ". But there is a LetteringScript with the same nid and other name = " + letteringScript.getName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "LetteringScript.name was updated.");
                letteringScript.setName(name);
            }
        } else {
            DebugUtil.showInfo(this, "New LetteringScript with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new LetteringScript(nid, name));
        }
        return letteringScript;
    }


}
