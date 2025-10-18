package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Metal;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class MetalService extends AbstractService<Metal, MetalRepository> {

    public MetalService(MetalRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing
    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Metal.LABEL, "nid", nid);
    }

    public boolean compareName(UUID uuid, String propertyValue) {
        return comparePropertyValue(uuid, "name", propertyValue, String.class);
    }

    public boolean setName(UUID uuid, String propertyValue) {
        return setPropertyStringValue(uuid, "name", propertyValue);
    }

    public String getName(UUID uuid) {
        return getPropertyValue(uuid, "name", String.class);
    }

    // End: Methods for Numista parsing

    public Metal findByNid(String nid, String name) {
        if (nid == null || nid.isEmpty()) {
            return null;
        }
        Metal metal = repository.findByNid(nid);
        if (metal != null) {
            if (!metal.getName().equals(name)) {
                DebugUtil.showServiceMessage(this, "Trying to find Metal with nid=" + nid + " and name=" + name
                        + ". But there is a Metal with the same nid and other name= " + metal.getName()
                        + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Metal.name was updated.");
                metal.setName(name);
                return repository.save(metal);
            }
        } else {
            DebugUtil.showInfo(this, "New Metal with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new Metal(nid, name));
        }
        return metal;
    }

}
