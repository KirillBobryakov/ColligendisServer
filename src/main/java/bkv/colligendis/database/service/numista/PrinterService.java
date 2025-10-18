package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Printer;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class PrinterService extends AbstractService<Printer, PrinterRepository> {

    private static final Logger logger = LogManager.getLogger(PrinterService.class);

    public PrinterService(PrinterRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing
    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Printer.LABEL, "nid", nid);
    }

    public boolean compareName(UUID printerUuid, String name) {
        return comparePropertyValue(printerUuid, "name", name, String.class);
    }

    public void setName(UUID printerUuid, String name) {
        setPropertyStringValue(printerUuid, "name", name);
    }
    // End: Methods for Numista parsing

    public Printer findByNid(String nid, String name) {
        Printer printer = repository.findByNid(nid);
        if (printer != null) {
            if (!printer.getName().equals(name)) {
                logger.warn("Trying to find Printer with nid=" + nid + " and name=" + name
                        + ". But there is a Printer with the same nid and other name = " + printer.getName()
                        + " in DB already.");
                logger.warn("Printer.name was updated.");
                printer.setName(name);
                return repository.save(printer);
            }
        } else {
            logger.info("New Printer with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new Printer(nid, name));
        }
        return printer;
    }

}
