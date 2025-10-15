package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Series;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class SeriesService extends AbstractService<Series, SeriesRepository> {

    public SeriesService(SeriesRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing

    public UUID findUuidByName(String name) {
        return findUuidByPropertyStringValue(Series.LABEL, "name", name);
    }

    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Series.LABEL, "nid", nid);
    }

    // End: Methods for Numista parsing

    public Series findByNameOrCreate(String name) {
        Series series = repository.findByName(name);
        if (series == null) {
            return repository.save(new Series(name));
        }
        return series;
    }

    public Series update(Series series, String name) {
        if (series == null || !series.getName().equals(name)) {
            series = repository.findByName(name);
        }
        if (series == null) {
            DebugUtil.showInfo(this, String.format("New Series with name=%name was created."));
            return repository.save(new Series(name));
        }
        return series;
    }

}
