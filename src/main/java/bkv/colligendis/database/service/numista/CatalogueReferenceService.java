package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Catalogue;
import bkv.colligendis.database.entity.numista.CatalogueReference;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class CatalogueReferenceService extends AbstractService<CatalogueReference, CatalogueReferenceRepository> {
    public CatalogueReferenceService(CatalogueReferenceRepository repository) {
        super(repository);
    }

    public CatalogueReference findByNumberAndCatalogueNid(String number, Catalogue catalogue){
        CatalogueReference catalogueReference = repository.findByNumberAndCatalogue_Nid(number, catalogue.getNid());
        if (catalogueReference == null) {
            DebugUtil.showInfo(this, "New CatalogueReference with number=" + number + " and references to catalogue=" + catalogue + " was created.");
            return repository.save(new CatalogueReference(catalogue, number));
        }
        return catalogueReference;
    }


}
