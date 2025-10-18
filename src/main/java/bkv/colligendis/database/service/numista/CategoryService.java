package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CategoryService extends AbstractService<Category, CategoryRepository> {

    private static final Logger logger = LogManager.getLogger(CategoryService.class);

    public CategoryService(CategoryRepository repository) {
        super(repository);
    }

    public Category findByName(String name) {
        Category category = repository.findByName(name);

        if (category == null) {
            logger.info("New Category with name: " + name + " was created.");
            return save(new Category(name));
        }
        return category;
    }

}
