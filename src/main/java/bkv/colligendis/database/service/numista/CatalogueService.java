package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Catalogue;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class CatalogueService extends AbstractService<Catalogue, CatalogueRepository> {
    public CatalogueService(CatalogueRepository repository) {
        super(repository);
    }

    public Catalogue findByNid(String nid, String code){
        Catalogue catalogue = repository.findByNid(nid);
        if (catalogue != null) {
            if(!catalogue.getCode().equals(code)){
                DebugUtil.showServiceMessage(this, "Trying to find Catalogue with nid=" + nid + " and code=" + code
                        + ". But there is a Catalogue with the same nid and other code = " + catalogue.getCode() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);

                DebugUtil.showWarning(this, "Catalogue.code was updated.");
                return repository.save(catalogue);
            }
        } else {
            DebugUtil.showInfo(this, "New Catalogue with nid=" + nid + " and code=" + code + " was created.");
            return repository.save(new Catalogue(nid, code));
        }
        return catalogue;
    }

    public Catalogue create(String nid, String code, String bibliography){
        Catalogue catalogue = findByNid(nid, code);
        if(catalogue != null) {
            catalogue.setBibliography(bibliography);
            return repository.save(catalogue);
        }
        return null;
    }

}
