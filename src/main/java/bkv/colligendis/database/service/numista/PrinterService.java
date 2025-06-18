package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Printer;
import bkv.colligendis.services.AbstractService;
import bkv.colligendis.utils.DebugUtil;
import org.springframework.stereotype.Service;

@Service
public class PrinterService extends AbstractService<Printer, PrinterRepository> {
    public PrinterService(PrinterRepository repository) {
        super(repository);
    }

    public Printer findByNid(String nid, String name){
        Printer printer = repository.findByNid(nid);
        if (printer != null) {
            if(!printer.getName().equals(name)){
                DebugUtil.showServiceMessage(this, "Trying to find Printer with nid=" + nid + " and name=" + name
                        + ". But there is a Printer with the same nid and other name = " + printer.getName() + " in DB already.", DebugUtil.MESSAGE_LEVEL.WARNING);
                DebugUtil.showWarning(this, "Printer.name was updated.");
                printer.setName(name);
                return repository.save(printer);
            }
        } else {
            DebugUtil.showInfo(this, "New Printer with nid=" + nid + " and name=" + name + " was created.");
            return repository.save(new Printer(nid, name));
        }
        return printer;
    }

}
