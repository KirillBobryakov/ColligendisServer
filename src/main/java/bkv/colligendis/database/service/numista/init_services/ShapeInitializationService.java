package bkv.colligendis.database.service.numista.init_services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import bkv.colligendis.database.entity.numista.Shape;
import bkv.colligendis.database.service.numista.ShapeService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class ShapeInitializationService {
    private static final Logger logger = LogManager.getLogger(ShapeInitializationService.class);

    private final ShapeService shapeService;

    public ShapeInitializationService(ShapeService shapeService) {
        this.shapeService = shapeService;
    }

    public void initializeAllShapes() {
        logger.info("Initializing shapes...");
        List<ShapeData> shapes = getAllShapeData();
        shapes.forEach(shapeData -> {
            UUID shapeUuid = shapeService.findUuidByNid(shapeData.getNid());
            if (shapeUuid == null) {
                shapeUuid = shapeService.save(new Shape(shapeData.getNid(), shapeData.getName())).getUuid();
            } else {
                if (!shapeService.compareName(shapeUuid, shapeData.getName())) {
                    shapeService.setName(shapeUuid, shapeData.getName());
                }
            }
        });
        logger.info("Initialized " + shapes.size() + " shapes.");
    }

    // Inner class for calendar data
    @Data
    @AllArgsConstructor
    private static class ShapeData {
        private final String nid;
        private final String name;
    }

    private List<ShapeData> getAllShapeData() {
        List<ShapeData> shapes = new ArrayList<>();
        shapes.add(new ShapeData("", "Unknown"));

        // Coins, Tokens, Medals
        shapes.add(new ShapeData("51", "Annular sector"));
        shapes.add(new ShapeData("49", "Cob"));
        shapes.add(new ShapeData("35", "Concave"));
        shapes.add(new ShapeData("10", "Decagonal (10-sided)"));
        shapes.add(new ShapeData("12", "Dodecagonal (12-sided)"));
        shapes.add(new ShapeData("47", "Equilateral curve heptagon (7-sided)"));
        shapes.add(new ShapeData("62", "Half circle"));
        shapes.add(new ShapeData("53", "Heart"));
        shapes.add(new ShapeData("11", "Hendecagonal (11-sided)"));
        shapes.add(new ShapeData("7", "Heptagonal (7-sided)"));
        shapes.add(new ShapeData("57", "Hexadecagonal (16-sided)"));
        shapes.add(new ShapeData("6", "Hexagonal (6-sided)"));
        shapes.add(new ShapeData("58", "Icosagonal (20-sided)"));
        shapes.add(new ShapeData("59", "Icosidigonal (22-sided)"));
        shapes.add(new ShapeData("66", "Icosihenagonal (21-sided)"));
        shapes.add(new ShapeData("65", "Icosipentagonal (25-sided)"));
        shapes.add(new ShapeData("56", "Icositetragonal (24-sided)"));
        shapes.add(new ShapeData("45", "Irregular"));
        shapes.add(new ShapeData("42", "Klippe"));
        shapes.add(new ShapeData("72", "Knife"));
        shapes.add(new ShapeData("9", "Nonagonal (9-sided)"));
        shapes.add(new ShapeData("8", "Octagonal (8-sided)"));
        shapes.add(new ShapeData("48", "Octagonal (8-sided) with a hole"));
        shapes.add(new ShapeData("68", "Octodecagonal (18-sided)"));
        shapes.add(new ShapeData("50", "Other"));
        shapes.add(new ShapeData("36", "Oval"));
        shapes.add(new ShapeData("37", "Oval with a loop"));
        shapes.add(new ShapeData("60", "Pentadecagonal (15-sided)"));
        shapes.add(new ShapeData("5", "Pentagonal (5-sided)"));
        shapes.add(new ShapeData("63", "Quarter circle"));
        shapes.add(new ShapeData("4", "Rectangular"));
        shapes.add(new ShapeData("46", "Rectangular (irregular)"));
        shapes.add(new ShapeData("43", "Reuleaux triangle"));
        shapes.add(new ShapeData("64", "Rhombus"));
        shapes.add(new ShapeData("1", "Round"));
        shapes.add(new ShapeData("2", "Round (irregular)"));
        shapes.add(new ShapeData("34", "Round with 4 pinches"));
        shapes.add(new ShapeData("33", "Round with a loop"));
        shapes.add(new ShapeData("31", "Round with a round hole"));
        shapes.add(new ShapeData("32", "Round with a square hole"));
        shapes.add(new ShapeData("38", "Round with cutouts"));
        shapes.add(new ShapeData("39", "Round with groove(s)"));
        shapes.add(new ShapeData("15", "Scalloped"));
        shapes.add(new ShapeData("20", "Scalloped (with 10 notches)"));
        shapes.add(new ShapeData("21", "Scalloped (with 11 notches)"));
        shapes.add(new ShapeData("22", "Scalloped (with 12 notches)"));
        shapes.add(new ShapeData("23", "Scalloped (with 13 notches)"));
        shapes.add(new ShapeData("24", "Scalloped (with 14 notches)"));
        shapes.add(new ShapeData("25", "Scalloped (with 15 notches)"));
        shapes.add(new ShapeData("26", "Scalloped (with 16 notches)"));
        shapes.add(new ShapeData("27", "Scalloped (with 17 notches)"));
        shapes.add(new ShapeData("29", "Scalloped (with 20 notches)"));
        shapes.add(new ShapeData("14", "Scalloped (with 4 notches)"));
        shapes.add(new ShapeData("18", "Scalloped (with 8 notches)"));
        shapes.add(new ShapeData("30", "Scalloped with a hole"));
        shapes.add(new ShapeData("75", "Sculptural"));
        shapes.add(new ShapeData("54", "Spade"));
        shapes.add(new ShapeData("73", "Spanish flower"));
        shapes.add(new ShapeData("44", "Square"));
        shapes.add(new ShapeData("41", "Square (irregular)"));
        shapes.add(new ShapeData("55", "Square with angled corners"));
        shapes.add(new ShapeData("40", "Square with rounded corners"));
        shapes.add(new ShapeData("71", "Square with scalloped edges"));
        shapes.add(new ShapeData("74", "Tetradecagonal (14-sided)"));
        shapes.add(new ShapeData("3", "Triangular"));
        shapes.add(new ShapeData("13", "Tridecagonal (13-sided)"));

        // Paper, Paper Exonumia
        shapes.add(new ShapeData("100", "Rectangular"));
        shapes.add(new ShapeData("102", "Rectangular (hand cut)"));
        shapes.add(new ShapeData("101", "Rectangular with undulating edge"));
        shapes.add(new ShapeData("103", "Round"));
        shapes.add(new ShapeData("105", "Square"));
        shapes.add(new ShapeData("104", "Triangular"));

        shapes.add(new ShapeData("150", "Other"));

        return shapes;
    }

}
