package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.database.entity.numista.CommemoratedEvent;
import bkv.colligendis.database.entity.numista.Series;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class SeriesService extends AbstractService<Series, SeriesRepository> {

    public SeriesService(SeriesRepository repository) {
        super(repository);
    }

    @Override
    public Series setPropertyValue(Long id, String name, String value) {
        return null;
    }

    public Series update(Series series, String name){
        if(series == null || !series.getName().equals(name)) {
            series = repository.findByName(name).block();
        }
        if (series == null) {
            DebugUtil.showInfo(this, String.format("New Series with name=%name was created."));
            return repository.save(new Series(name)).block();
        }
        return series;
    }

}
