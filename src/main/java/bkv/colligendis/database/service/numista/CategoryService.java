package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends AbstractService<Category, CategoryRepository> {


    public CategoryService(CategoryRepository repository) {
        super(repository);
    }



    public Category findByName(String name){
        Category category = repository.findByName(name);

        if(category == null){
            DebugUtil.showInfo(CategoryService.class, "Create new Category with name: " + name);
            return save(new Category(name));
        }
        return category;
    }


}
