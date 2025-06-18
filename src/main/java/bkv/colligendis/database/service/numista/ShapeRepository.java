package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Shape;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface ShapeRepository extends AbstractNeo4jRepository<Shape> {

    Shape findByNid(String nid);
}
