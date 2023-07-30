package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Period;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class PeriodService extends AbstractService<Period, PeriodRepository> {
    public PeriodService(PeriodRepository repository) {
        super(repository);
    }

    public Period findByName(String name){
        return repository.findByName(name).block();
    }

    @Override
    public Period setPropertyValue(Long id, String name, String value) {
        return null;
    }
}
