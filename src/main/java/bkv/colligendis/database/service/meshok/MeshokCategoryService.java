package bkv.colligendis.database.service.meshok;

import bkv.colligendis.database.entity.meshok.MeshokCategory;
import bkv.colligendis.services.AbstractService;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MeshokCategoryService extends AbstractService<MeshokCategory, MeshokCategoryRepository> {

    public MeshokCategoryService(MeshokCategoryRepository repository) {
        super(repository);
    }

    public MeshokCategory findByName(String name) {
        return repository.findByName(name);
    }

    public MeshokCategory findByMid(int mid) {
        return repository.findByMid(mid);
    }

    public List<MeshokCategory> findByParentMid(int parentMid) {
        List<MeshokCategory> children = repository.findByParentMid(parentMid);
        for (MeshokCategory child : children) {
            MeshokCategory parent = repository.findParentByChildMid(child.getMid());
            child.setParent(parent);
        }
        return children;
    }

}
