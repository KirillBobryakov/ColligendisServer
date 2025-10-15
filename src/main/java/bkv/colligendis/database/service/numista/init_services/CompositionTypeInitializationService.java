package bkv.colligendis.database.service.numista.init_services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import bkv.colligendis.database.entity.numista.CompositionType;
import bkv.colligendis.database.service.numista.CompositionTypeService;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
public class CompositionTypeInitializationService {
    private static final Logger logger = LogManager.getLogger(CompositionTypeInitializationService.class);

    private final CompositionTypeService compositionTypeService;

    public CompositionTypeInitializationService(CompositionTypeService compositionTypeService) {
        this.compositionTypeService = compositionTypeService;
    }

    public void initializeAllCompositionTypes() {
        logger.info("Initializing composition types...");
        List<CompositionTypeData> compositionTypes = getAllCompositionTypes();
        compositionTypes.forEach(compositionTypeData -> {
            UUID compositionTypeUuid = compositionTypeService.findUuidByCode(compositionTypeData.getCode());
            if (compositionTypeUuid == null) {
                compositionTypeUuid = compositionTypeService
                        .save(new CompositionType(compositionTypeData.getCode(), compositionTypeData.getName()))
                        .getUuid();
            } else {
                if (!compositionTypeService.compareCode(compositionTypeUuid, compositionTypeData.getCode())) {
                    compositionTypeService.setCode(compositionTypeUuid, compositionTypeData.getCode());
                }
            }
        });
        logger.info("Initialized " + compositionTypes.size() + " composition types.");
    }

    @Data
    @AllArgsConstructor
    private static class CompositionTypeData {
        private final String code;
        private final String name;
    }

    private List<CompositionTypeData> getAllCompositionTypes() {
        List<CompositionTypeData> compositionTypes = new ArrayList<>();
        compositionTypes.add(new CompositionTypeData("", "Unknown"));
        compositionTypes.add(new CompositionTypeData("plain", "Single material"));
        compositionTypes.add(new CompositionTypeData("plated", "Plated metal"));
        compositionTypes.add(new CompositionTypeData("clad", "Clad metal"));
        compositionTypes.add(new CompositionTypeData("bimetallic", "Bimetallic"));
        compositionTypes.add(new CompositionTypeData("bimetallic_plated", "Bimetallic with plated metal centre"));
        compositionTypes.add(new CompositionTypeData("bimetallic_plated_ring", "Bimetallic with plated metal ring"));
        compositionTypes
                .add(new CompositionTypeData("bimetallic_plated_plated", "Bimetallic with plated centre and ring"));
        compositionTypes.add(new CompositionTypeData("bimetallic_clad", "Bimetallic with clad metal centre"));
        compositionTypes.add(new CompositionTypeData("trimetallic", "Trimetallic"));
        compositionTypes.add(new CompositionTypeData("other", "Other"));

        compositionTypes.add(new CompositionTypeData("76", "Gold‑deposited polymer"));
        compositionTypes.add(new CompositionTypeData("68", "Hybrid substrate"));
        compositionTypes.add(new CompositionTypeData("73", "Other"));
        compositionTypes.add(new CompositionTypeData("66", "Paper"));
        compositionTypes.add(new CompositionTypeData("67", "Polymer"));
        compositionTypes.add(new CompositionTypeData("69", "Silk"));
        compositionTypes.add(new CompositionTypeData("77", "Silver‑deposited polymer"));

        return compositionTypes;
    }

}
