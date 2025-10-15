package bkv.colligendis.database.service.numista.init_services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import bkv.colligendis.database.entity.numista.Technique;
import bkv.colligendis.database.service.numista.TechniqueService;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
public class TechniqueInitializationService {

    private static final Logger logger = LogManager.getLogger(TechniqueInitializationService.class);

    private final TechniqueService techniqueService;

    public TechniqueInitializationService(TechniqueService techniqueService) {
        this.techniqueService = techniqueService;
    }

    public void initializeAllTechniques() {
        logger.info("Initializing techniques...");
        List<TechniqueData> techniques = getAllTechniques();
        techniques.forEach(techniqueData -> {
            UUID techniqueUuid = techniqueService.findUuidByNid(techniqueData.getNid());
            if (techniqueUuid == null) {
                techniqueUuid = techniqueService
                        .save(new Technique(techniqueData.getNid(), techniqueData.getName()))
                        .getUuid();
            } else {
                if (!techniqueService.compareName(techniqueUuid, techniqueData.getName())) {
                    techniqueService.setName(techniqueUuid, techniqueData.getName());
                }
            }
        });
        logger.info("Initialized " + techniques.size() + " techniques.");
    }

    @Data
    @AllArgsConstructor
    private static class TechniqueData {
        private final String nid;
        private final String name;
    }

    private List<TechniqueData> getAllTechniques() {
        List<TechniqueData> techniques = new ArrayList<>();

        // Coins, Tokens, Medals
        techniques.add(new TechniqueData("1", "Cast"));
        techniques.add(new TechniqueData("36", "Coloured"));
        techniques.add(new TechniqueData("5", "Countermarked"));
        techniques.add(new TechniqueData("6", "Counterstamped"));
        techniques.add(new TechniqueData("7", "Cut"));
        techniques.add(new TechniqueData("4", "Engraved"));
        techniques.add(new TechniqueData("2", "Hammered"));
        techniques.add(new TechniqueData("24", "Hammered (bean)"));
        techniques.add(new TechniqueData("20", "Hammered (bracteate)"));
        techniques.add(new TechniqueData("25", "Hammered (bullet)"));
        techniques.add(new TechniqueData("23", "Hammered (cob)"));
        techniques.add(new TechniqueData("21", "Hammered (scyphate)"));
        techniques.add(new TechniqueData("22", "Hammered (wire)"));
        techniques.add(new TechniqueData("11", "Incuse"));
        techniques.add(new TechniqueData("41", "Injection moulding"));
        techniques.add(new TechniqueData("38", "Inlaid"));
        techniques.add(new TechniqueData("12", "Klippe"));
        techniques.add(new TechniqueData("3", "Milled"));
        techniques.add(new TechniqueData("39", "Milled (high relief)"));
        techniques.add(new TechniqueData("10", "Roller milled"));

        // Paper, Paper Exonumia
        techniques.add(new TechniqueData("34", "Cut"));
        techniques.add(new TechniqueData("29", "Intaglio"));
        techniques.add(new TechniqueData("30", "Letterpress"));
        techniques.add(new TechniqueData("32", "Lithography"));
        techniques.add(new TechniqueData("37", "Offset"));
        techniques.add(new TechniqueData("33", "Overprinted"));
        techniques.add(new TechniqueData("35", "Perforated"));
        techniques.add(new TechniqueData("31", "Screen printing"));

        return techniques;
    }

}
