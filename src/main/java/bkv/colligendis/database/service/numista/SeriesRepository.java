package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Series;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface SeriesRepository extends AbstractNeo4jRepository<Series> {

    Series findByName(String name);
}
