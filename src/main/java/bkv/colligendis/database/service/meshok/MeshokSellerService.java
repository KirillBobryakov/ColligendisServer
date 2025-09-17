package bkv.colligendis.database.service.meshok;

import bkv.colligendis.database.entity.meshok.MeshokSeller;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class MeshokSellerService extends AbstractService<MeshokSeller, MeshokSellerRepository> {

    public MeshokSellerService(MeshokSellerRepository repository) {
        super(repository);
    }

    public MeshokSeller findByMid(int mid) {
        return repository.findByMid(mid);
    }

}
