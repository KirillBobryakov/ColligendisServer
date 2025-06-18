package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface CategoryRepository extends AbstractNeo4jRepository<Category> {

    Category findByName(String name);
}
