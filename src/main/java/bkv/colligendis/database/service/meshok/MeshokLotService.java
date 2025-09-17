package bkv.colligendis.database.service.meshok;

import bkv.colligendis.database.entity.meshok.MeshokLot;
import bkv.colligendis.services.AbstractService;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MeshokLotService extends AbstractService<MeshokLot, MeshokLotRepository> {

    public MeshokLotService(MeshokLotRepository repository) {
        super(repository);
    }

    public MeshokLot findByMid(int mid) {
        return repository.findByMid(mid);
    }

    public List<MeshokLot> findByCategoryMid(int mid) {
        return repository.findByCategoryMid(mid);
    }

    public List<MeshokLot> findAllLimitedWithCategory(int limit) {
        return repository.findAllLimitedWithCategory(limit);
    }

}
