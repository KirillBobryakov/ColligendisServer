package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Series;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class SeriesService extends AbstractService<Series, SeriesRepository> {

    public SeriesService(SeriesRepository repository) {
        super(repository);
    }


    public Series findByNameOrCreate(String name){
        Series series = repository.findByName(name);
        if(series == null){
            return repository.save(new Series(name));
        }
        return series;
    }


    public Series update(Series series, String name){
        if(series == null || !series.getName().equals(name)) {
            series = repository.findByName(name);
        }
        if (series == null) {
            DebugUtil.showInfo(this, String.format("New Series with name=%name was created."));
            return repository.save(new Series(name));
        }
        return series;
    }

}
