package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Catalogue;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class CatalogueService extends AbstractService<Catalogue, CatalogueRepository> {
    public CatalogueService(CatalogueRepository repository) {
        super(repository);
    }

    public Catalogue findByNid(String nid, String code){
        Catalogue catalogue = repository.findByNid(nid).block();
        if (catalogue != null) {
            if(!catalogue.getCode().equals(code)){
                DebugUtil.showServiceMessage(this, "Trying to find Catalogue with nid=" + nid + " and code=" + code
                        + ". But there is a Catalogue with the same nid and other code = " + catalogue.getCode() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);

                DebugUtil.showWarning(this, "Catalogue.code was updated.");
                return repository.save(catalogue).block();
            }
        } else {
            DebugUtil.showInfo(this, "New Catalogue with nid=" + nid + " and code=" + code + " was created.");
            return repository.save(new Catalogue(nid, code)).block();
        }
        return catalogue;
    }



    @Override
    public Catalogue setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
