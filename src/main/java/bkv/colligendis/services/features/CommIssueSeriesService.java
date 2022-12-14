package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.CommIssueSeries;
import bkv.colligendis.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class CommIssueSeriesService extends AbstractService<CommIssueSeries, CommIssueSeriesRepository> {

    public CommIssueSeriesService(CommIssueSeriesRepository repository) {
        super(repository);
    }

    public CommIssueSeries findByName(String name){
        return repository.findByName(name).block();
    }
}
