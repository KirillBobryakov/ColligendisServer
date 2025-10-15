package bkv.colligendis.database.service.numista.init_services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import bkv.colligendis.database.entity.numista.Metal;
import bkv.colligendis.database.service.numista.MetalService;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
public class MetalInitializationService {
    private static final Logger logger = LogManager.getLogger(MetalInitializationService.class);

    private final MetalService metalService;

    public MetalInitializationService(MetalService metalService) {
        this.metalService = metalService;
    }

    public void initializeAllMetals() {
        logger.info("Initializing metals...");
        List<MetalData> metals = getAllMetals();
        metals.forEach(metalData -> {
            UUID metalUuid = metalService.findUuidByNid(metalData.getNid());
            if (metalUuid == null) {
                metalUuid = metalService
                        .save(new Metal(metalData.getNid(), metalData.getName()))
                        .getUuid();
            } else {
                if (!metalService.compareName(metalUuid, metalData.getName())) {
                    metalService.setName(metalUuid, metalData.getName());
                }
            }
        });
        logger.info("Initialized " + metals.size() + " metals.");
    }

    @Data
    @AllArgsConstructor
    private static class MetalData {
        private final String nid;
        private final String name;
    }

    private List<MetalData> getAllMetals() {
        List<MetalData> metals = new ArrayList<>();

        metals.add(new MetalData("", "Unknown"));
        metals.add(new MetalData("38", "Acmonital"));
        metals.add(new MetalData("45", "Aluminium"));
        metals.add(new MetalData("50", "Aluminium-brass"));
        metals.add(new MetalData("10", "Aluminium-bronze"));
        metals.add(new MetalData("31", "Aluminium-magnesium"));
        metals.add(new MetalData("30", "Aluminium-nickel-bronze"));
        metals.add(new MetalData("54", "Aluminium-zinc-bronze"));
        metals.add(new MetalData("34", "Bakelite"));
        metals.add(new MetalData("7", "Billon"));
        metals.add(new MetalData("4", "Brass"));
        metals.add(new MetalData("5", "Bronze"));
        metals.add(new MetalData("65", "Bronze-nickel"));
        metals.add(new MetalData("37", "Bronzital"));
        metals.add(new MetalData("24", "Cardboard"));
        metals.add(new MetalData("63", "Ceramic"));
        metals.add(new MetalData("41", "Chromium"));
        metals.add(new MetalData("39", "Clay composite"));
        metals.add(new MetalData("3", "Copper"));
        metals.add(new MetalData("32", "Copper-aluminium"));
        metals.add(new MetalData("46", "Copper-aluminium-nickel"));
        metals.add(new MetalData("2", "Copper-nickel"));
        metals.add(new MetalData("36", "Copper-nickel-iron"));
        metals.add(new MetalData("17", "Electrum"));
        metals.add(new MetalData("60", "Fiber"));
        metals.add(new MetalData("59", "Florentine bronze"));
        metals.add(new MetalData("48", "Gilding metal"));
        metals.add(new MetalData("64", "Glass"));
        metals.add(new MetalData("49", "Niobium"));
        metals.add(new MetalData("18", "Nordic gold"));
        metals.add(new MetalData("52", "Orichalcum"));
        metals.add(new MetalData("72", "Other"));
        metals.add(new MetalData("44", "Palladium"));
        metals.add(new MetalData("25", "Pewter"));
        metals.add(new MetalData("14", "Plastic"));
        metals.add(new MetalData("22", "Platinum"));
        metals.add(new MetalData("26", "Porcelain"));
        metals.add(new MetalData("33", "Potin"));
        metals.add(new MetalData("43", "Resin"));
        metals.add(new MetalData("70", "Rhodium"));
        metals.add(new MetalData("71", "Ruthenium"));
        metals.add(new MetalData("1", "Silver"));
        metals.add(new MetalData("15", "Stainless steel"));
        metals.add(new MetalData("9", "Steel"));
        metals.add(new MetalData("40", "Tantalum"));
        metals.add(new MetalData("19", "Tin"));
        metals.add(new MetalData("58", "Tin brass"));
        metals.add(new MetalData("57", "Tin-lead"));
        metals.add(new MetalData("61", "Tin-zinc"));
        metals.add(new MetalData("35", "Titanium"));
        metals.add(new MetalData("23", "Tombac"));
        metals.add(new MetalData("47", "Virenium"));
        metals.add(new MetalData("20", "Wood"));
        metals.add(new MetalData("27", "Zamak"));
        metals.add(new MetalData("11", "Zinc"));
        metals.add(new MetalData("42", "Zinc-aluminium"));

        return metals;
    }

}
