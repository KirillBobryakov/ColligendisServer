package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Shape;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class ShapeService extends AbstractService<Shape, ShapeRepository> {
    public ShapeService(ShapeRepository repository) {
        super(repository);
    }

    public Shape findByNid(String nid, String name) {
        Shape shape = repository.findByNid(nid);
        if (shape == null || !shape.getName().equals(name)) {
            shape = repository.save(new Shape(nid, name));
            DebugUtil.showServiceMessage(this, "New Shape with nid=" + nid + " and name=" + name + " was created.",
                    DebugUtil.MESSAGE_LEVEL.INFO);
        }
        return shape;
    }

    public Shape update(Shape shape, String nid, String name) {
        if (shape == null || !shape.getNid().equals(nid)) {
            shape = repository.findByNid(nid);
        }
        if (shape != null) {
            if (!shape.getName().equals(name)) {
                DebugUtil.showServiceMessage(this, "Trying to find Shape with nid=" + nid + " and name=" + name
                        + ". But there is a Shape with the same nid and other name = " + shape.getName()
                        + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Shape.name was updated.");
                shape.setName(name);
                return repository.save(shape);
            }
        } else {
            DebugUtil.showInfo(this, "New Shape with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new Shape(nid, name));
        }
        return shape;
    }

}
