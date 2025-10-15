package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Artist;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ArtistService extends AbstractService<Artist, ArtistRepository> {

    public ArtistService(ArtistRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing
    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Artist.LABEL, "nid", nid);
    }

    public UUID findUuidByName(String name) {
        return findUuidByPropertyStringValue(Artist.LABEL, "name", name);
    }

    public Artist findByNid(String nid) {
        return repository.findByNid(nid);
    }
    // End: Methods for Numista parsing

}
