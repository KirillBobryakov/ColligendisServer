package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.services.AbstractService2;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Service
public class CategoryService extends AbstractService<Category, CategoryRepository> {

    public CategoryService(CategoryRepository repository) {
        super(repository);
    }

    @Override
    public Category setPropertyValue(Long id, String name, String value) {
        return null;
    }

    public Category findByName(String name){
        if(name.equals(Category.COIN) || name.equals(Category.BANKNOTE) || name.equals(Category.EXONUMIA)){
            Category category = repository.findByName(name).block();
            if(category == null){
                return repository.save(new Category(name)).block();
            }
            return category;
        }
        return null;
    }


}
