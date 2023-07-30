package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Metal;
import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.database.entity.numista.Shape;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class ShapeService extends AbstractService<Shape, ShapeRepository> {
    public ShapeService(ShapeRepository repository) {
        super(repository);
    }


    public Shape update(Shape shape, String nid, String name){
        if(shape == null || !shape.getNid().equals(nid)) {
            shape = repository.findByNid(nid).block();
        }
        if (shape != null) {
            if(!shape.getName().equals(name)){
                DebugUtil.showServiceMessage(this, "Trying to find Shape with nid=" + nid + " and name=" + name
                        + ". But there is a Shape with the same nid and other name = " + shape.getName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Shape.name was updated.");
                shape.setName(name);
                return repository.save(shape).block();
            }
        } else {
            DebugUtil.showInfo(this, "New Shape with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new Shape(nid, name)).block();
        }
        return shape;
    }

    @Override
    public Shape setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
