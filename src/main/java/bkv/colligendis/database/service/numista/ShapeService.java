package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Shape;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ShapeService extends AbstractService<Shape, ShapeRepository> {
    public ShapeService(ShapeRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing
    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Shape.LABEL, "nid", nid);
    }

    public String getNid(UUID shapeUuid) {
        return getPropertyValue(shapeUuid, "nid", String.class);
    }

    public boolean compareName(UUID shapeUuid, String name) {
        return comparePropertyValue(shapeUuid, "name", name, String.class);
    }

    public boolean setName(UUID shapeUuid, String name) {
        return setPropertyStringValue(shapeUuid, "name", name);
    }

    // End: Methods for Numista parsing

    // public Shape findByNid(String nid, String name) {
    // Shape shape = repository.findByNid(nid);
    // if (shape == null || !shape.getName().equals(name)) {
    // shape = repository.save(new Shape(nid, name));
    // DebugUtil.showServiceMessage(this, "New Shape with nid=" + nid + " and name="
    // + name + " was created.",
    // DebugUtil.MESSAGE_LEVEL.INFO);
    // }
    // return shape;
    // }

    // public Shape update(Shape shape, String nid, String name) {
    // if (shape == null || !shape.getNid().equals(nid)) {
    // shape = repository.findByNid(nid);
    // }
    // if (shape != null) {
    // if (!shape.getName().equals(name)) {
    // DebugUtil.showServiceMessage(this, "Trying to find Shape with nid=" + nid + "
    // and name=" + name
    // + ". But there is a Shape with the same nid and other name = " +
    // shape.getName()
    // + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
    // DebugUtil.showWarning(this, "Shape.name was updated.");
    // shape.setName(name);
    // return repository.save(shape);
    // }
    // } else {
    // DebugUtil.showInfo(this, "New Shape with nid=" + nid + " and name=" + name +
    // " was created.");
    // return repository.save(new Shape(nid, name));
    // }
    // return shape;
    // }

}
