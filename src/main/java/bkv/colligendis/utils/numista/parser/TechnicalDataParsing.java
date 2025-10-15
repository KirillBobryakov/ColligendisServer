package bkv.colligendis.utils.numista.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.entity.numista.Composition;
import bkv.colligendis.database.entity.numista.Metal;
import bkv.colligendis.database.service.numista.CollectibleTypeService;
import bkv.colligendis.database.service.numista.CompositionPartType;
import bkv.colligendis.database.service.numista.CompositionService;
import bkv.colligendis.database.service.numista.CompositionTypeService;
import bkv.colligendis.database.service.numista.MetalService;
import bkv.colligendis.database.service.numista.NTypeService;
import bkv.colligendis.database.service.numista.ShapeService;
import bkv.colligendis.database.service.numista.TechniqueService;
import bkv.colligendis.utils.N4JUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TechnicalDataParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(TechnicalDataParsing.class);

    public TechnicalDataParsing() {
        super((pageParser) -> {

            ParsingResult result = ParsingResult.NOT_CHANGED;

            if ((result = parseComposition(pageParser.getNumistaPage(),
                    pageParser.getNTypeUuid())) == ParsingResult.ERROR) {
                return result;
            }

            if ((result = parseShape(pageParser.getNumistaPage(),
                    pageParser.getNTypeUuid())) == ParsingResult.ERROR) {
                return result;
            }

            if ((result = parseWeight(pageParser.getNumistaPage(),
                    pageParser.getNTypeUuid())) == ParsingResult.ERROR) {
                return result;
            }

            if ((result = parseSize(pageParser.getNumistaPage(),
                    pageParser.getNTypeUuid())) == ParsingResult.ERROR) {
                return result;
            }

            if ((result = parseThickness(pageParser.getNumistaPage(),
                    pageParser.getNTypeUuid())) == ParsingResult.ERROR) {
                return result;
            }

            if ((result = parseTechniques(pageParser.getNumistaPage(),
                    pageParser.getNTypeUuid())) == ParsingResult.ERROR) {
                return result;
            }

            if ((result = parseAlignment(pageParser.getNumistaPage(),
                    pageParser.getNTypeUuid())) == ParsingResult.ERROR) {
                return result;
            }

            return result;
        });

        this.partName = "TechnicalData";
    }

    private static ParsingResult parseComposition(Document page, UUID nTypeUuid) {
        NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;
        CompositionService compositionService = N4JUtil.getInstance().numistaService.compositionService;

        ParsingResult result = ParsingResult.NOT_CHANGED;

        HashMap<String, String> metalType = getAttributeWithTextSelectedOption(page, "#metal_type");

        UUID compositionUuid = null;

        if (metalType != null) {
            String compositionTypeCode = metalType.get("value");
            String compositionTypeName = metalType.get("text");
            compositionUuid = setCompositionType(nTypeUuid, compositionTypeCode, compositionTypeName);

            if (compositionTypeCode.equals("plain")) {
                CompositionMetalParsingData metalParsingData = parseCompositionMetal("#metal1", "#fineness1", page);

                result = setPart1(compositionUuid, metalParsingData, CompositionPartType.material);

                compositionService.clearPart2(compositionUuid);
                compositionService.clearPart3(compositionUuid);
                compositionService.clearPart4(compositionUuid);

            } else if (compositionTypeCode.equals("plated")) {
                // Core
                CompositionMetalParsingData coreMetalParsingData = parseCompositionMetal("#metal1", "#fineness1", page);
                result = setPart1(compositionUuid, coreMetalParsingData, CompositionPartType.core);

                // Plating
                CompositionMetalParsingData platingMetalParsingData = parseCompositionMetal("#metal2", "#fineness2",
                        page);
                result = setPart2(compositionUuid, platingMetalParsingData, CompositionPartType.plating);

                compositionService.clearPart3(compositionUuid);
                compositionService.clearPart4(compositionUuid);

            } else if (compositionTypeCode.equals("clad")) {

                // Core
                CompositionMetalParsingData coreMetalParsingData = parseCompositionMetal("#metal1", "#fineness1", page);
                result = setPart1(compositionUuid, coreMetalParsingData, CompositionPartType.core);

                // Clad
                CompositionMetalParsingData cladMetalParsingData = parseCompositionMetal("#metal2", "#fineness2", page);
                result = setPart2(compositionUuid, cladMetalParsingData, CompositionPartType.clad);

                compositionService.clearPart3(compositionUuid);
                compositionService.clearPart4(compositionUuid);
            } else if (compositionTypeCode.equals("bimetallic")) {

                // Center
                CompositionMetalParsingData centerMetalParsingData = parseCompositionMetal("#metal1", "#fineness1",
                        page);
                result = setPart1(compositionUuid, centerMetalParsingData, CompositionPartType.center);

                // Ring
                CompositionMetalParsingData ringMetalParsingData = parseCompositionMetal("#metal2", "#fineness2", page);
                result = setPart2(compositionUuid, ringMetalParsingData, CompositionPartType.ring);

                compositionService.clearPart3(compositionUuid);
                compositionService.clearPart4(compositionUuid);

            } else if (compositionTypeCode.equals("bimetallic_plated")) {

                // Center Core
                CompositionMetalParsingData centerCoreMetalParsingData = parseCompositionMetal("#metal1", "#fineness1",
                        page);
                result = setPart1(compositionUuid, centerCoreMetalParsingData, CompositionPartType.center_core);

                // Center Plating
                CompositionMetalParsingData centerPlatingMetalParsingData = parseCompositionMetal("#metal2",
                        "#fineness2", page);
                result = setPart2(compositionUuid, centerPlatingMetalParsingData, CompositionPartType.center_plating);

                // Ring
                CompositionMetalParsingData ringMetalParsingData = parseCompositionMetal("#metal3", "#fineness3", page);
                result = setPart3(compositionUuid, ringMetalParsingData, CompositionPartType.ring);

                compositionService.clearPart4(compositionUuid);

            } else if (compositionTypeCode.equals("bimetallic_plated_ring")) {

                // Center
                CompositionMetalParsingData centerMetalParsingData = parseCompositionMetal("#metal1", "#fineness1",
                        page);
                result = setPart1(compositionUuid, centerMetalParsingData, CompositionPartType.center);

                // Ring core
                CompositionMetalParsingData ringCoreMetalParsingData = parseCompositionMetal("#metal2", "#fineness2",
                        page);
                result = setPart2(compositionUuid, ringCoreMetalParsingData, CompositionPartType.ring_core);

                // Ring plating
                CompositionMetalParsingData ringPlatingMetalParsingData = parseCompositionMetal("#metal3", "#fineness3",
                        page);
                result = setPart3(compositionUuid, ringPlatingMetalParsingData, CompositionPartType.ring_plating);

                compositionService.clearPart4(compositionUuid);
            } else if (compositionTypeCode.equals("bimetallic_plated_plated")) {

                // Center Core
                CompositionMetalParsingData centerCoreMetalParsingData = parseCompositionMetal("#metal1", "#fineness1",
                        page);
                result = setPart1(compositionUuid, centerCoreMetalParsingData, CompositionPartType.center_core);

                // Center Plating
                CompositionMetalParsingData centerPlatingMetalParsingData = parseCompositionMetal("#metal2",
                        "#fineness2", page);
                result = setPart2(compositionUuid, centerPlatingMetalParsingData, CompositionPartType.center_plating);

                // Ring core
                CompositionMetalParsingData ringCoreMetalParsingData = parseCompositionMetal("#metal3", "#fineness3",
                        page);
                result = setPart3(compositionUuid, ringCoreMetalParsingData, CompositionPartType.ring_core);

                // Ring plating
                CompositionMetalParsingData ringPlatingMetalParsingData = parseCompositionMetal("#metal4", "#fineness4",
                        page);
                result = setPart4(compositionUuid, ringPlatingMetalParsingData, CompositionPartType.ring_plating);

            } else if (compositionTypeCode.equals("bimetallic_clad")) {
                // Center Core
                CompositionMetalParsingData centerCoreMetalParsingData = parseCompositionMetal("#metal1", "#fineness1",
                        page);
                result = setPart1(compositionUuid, centerCoreMetalParsingData, CompositionPartType.center_core);

                // Center Clad
                CompositionMetalParsingData centerCladMetalParsingData = parseCompositionMetal("#metal2", "#fineness2",
                        page);
                result = setPart2(compositionUuid, centerCladMetalParsingData, CompositionPartType.center_clad);

                // Ring
                CompositionMetalParsingData ringMetalParsingData = parseCompositionMetal("#metal3", "#fineness3", page);
                result = setPart3(compositionUuid, ringMetalParsingData, CompositionPartType.ring);

                compositionService.clearPart4(compositionUuid);

            } else if (compositionTypeCode.equals("trimetallic")) {

                // Center
                CompositionMetalParsingData centerMetalParsingData = parseCompositionMetal("#metal1", "#fineness1",
                        page);
                result = setPart1(compositionUuid, centerMetalParsingData, CompositionPartType.center);

                // Middle ring
                CompositionMetalParsingData middleRingMetalParsingData = parseCompositionMetal("#metal2", "#fineness2",
                        page);
                result = setPart2(compositionUuid, middleRingMetalParsingData, CompositionPartType.middle_ring);

                // Outer ring
                CompositionMetalParsingData outerRingMetalParsingData = parseCompositionMetal("#metal2", "#fineness2",
                        page);
                result = setPart3(compositionUuid, outerRingMetalParsingData, CompositionPartType.outer_ring);

                compositionService.clearPart4(compositionUuid);
            }

        }

        UUID collectibleTypeUuid = nTypeService.getCollectibleTypeUuid(nTypeUuid);
        CollectibleTypeService collectibleTypeService = N4JUtil.getInstance().numistaService.collectibleTypeService;
        UUID topCollectibleTypeUuid = collectibleTypeService.findTopCollectibleTypeUuid(collectibleTypeUuid);
        String topCollectibleTypeCode = collectibleTypeService.getCode(topCollectibleTypeUuid);

        if (topCollectibleTypeCode.equals(CollectibleType.BANKNOTES_CODE)) {
            HashMap<String, String> compositionHashMap = getAttributeWithTextSelectedOption(page,
                    "#metal1");
            if (compositionHashMap != null && isValueAndTextNotNullAndNotEmpty(compositionHashMap)) {

                String compositionTypeCode = compositionHashMap.get("value");
                String compositionTypeName = compositionHashMap.get("text");

                compositionUuid = setCompositionType(nTypeUuid, compositionTypeCode, compositionTypeName);

            }
        }

        // Metal Additional details
        String metalDetails = getAttribute(page.selectFirst("#metal_details"), "value");

        if (metalDetails != null && !metalDetails.isEmpty()) {
            if (!compositionService.compareCompositionAdditionalDetails(compositionUuid, metalDetails)) {
                compositionService.setCompositionAdditionalDetails(compositionUuid, metalDetails);
            }
        }

        return result;
    }

    private static ParsingResult parseShape(Document page, UUID nTypeUuid) {
        NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;
        ShapeService shapeService = N4JUtil.getInstance().numistaService.shapeService;

        ParsingResult result = ParsingResult.NOT_CHANGED;

        HashMap<String, String> shape = getAttributeWithTextSelectedOption(page, "#shape");
        if (shape != null && isValueAndTextNotNullAndNotEmpty(shape)) {

            String shapeNid = shape.get("value");
            String shapeName = shape.get("text");

            UUID shapeUuid = nTypeService.getShapeUuid(nTypeUuid);
            if (shapeUuid == null) {
                shapeUuid = N4JUtil.getInstance().numistaService.shapeService.findUuidByNid(shapeNid);
                if (shapeUuid == null) {
                    // TODO: Throw error
                }
                assert shapeUuid != null;
                nTypeService.setShape(nTypeUuid, shapeUuid);
            } else {
                if (!shapeService.compareName(shapeUuid, shapeName)) {
                    shapeService.setName(shapeUuid, shapeName);
                }
            }

        }

        // Shape Additional details
        String shapeDetails = getAttribute(page.selectFirst("#shape_details"), "value");
        if (shapeDetails != null && !shapeDetails.isEmpty()) {
            if (!nTypeService.compareShapeAdditionalDetails(nTypeUuid, shapeDetails)) {
                nTypeService.setShapeAdditionalDetails(nTypeUuid, shapeDetails);
            }
        }

        return result;
    }

    private static ParsingResult parseWeight(Document page, UUID nTypeUuid) {
        NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;
        ParsingResult result = ParsingResult.NOT_CHANGED;
        String poids = getAttribute(page.selectFirst("#poids"), "value");
        if (poids != null && !poids.isEmpty()) {
            if (!nTypeService.compareWeight(nTypeUuid, Float.parseFloat(poids))) {
                nTypeService.setWeight(nTypeUuid, Float.parseFloat(poids));
                result = ParsingResult.CHANGED;
            }
        }
        return result;
    }

    private static ParsingResult parseSize(Document page, UUID nTypeUuid) {
        ParsingResult result = ParsingResult.NOT_CHANGED;
        NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;
        ShapeService shapeService = N4JUtil.getInstance().numistaService.shapeService;

        String dimension = getAttribute(page.selectFirst("#dimension"), "value");
        if (dimension != null && !dimension.isEmpty()) {
            if (!nTypeService.compareSize(nTypeUuid, Float.parseFloat(dimension))) {
                nTypeService.setSize(nTypeUuid, Float.parseFloat(dimension));
                result = ParsingResult.CHANGED;
            }
        }

        // Second dimension
        // <option value="42">Klippe</option>
        // <option value="50">Other</option>
        // <option value="36">Oval</option>
        // <option value="37">Oval with a loop</option>
        // <option value="4">Rectangular</option>
        // <option value="46">Rectangular (irregular)</option>
        // <option value="75">Sculptural</option>
        // <option value="54">Spade</option>
        // <option value="44">Square</option>
        // <option value="41">Square (irregular)</option>
        // <option value="55">Square with angled corners</option>
        // <option value="40">Square with rounded corners</option>
        // <option value="71">Square with scalloped edges</option>

        // <option value="">Unknown</option>
        // <option value="150">Other</option>
        // <option value="100" selected="selected">Rectangular</option>
        // <option value="102">Rectangular (hand cut)</option>
        // <option value="101">Rectangular with undulating edge</option>
        // <option value="105">Square</option>

        UUID shapeUuid = nTypeService.getShapeUuid(nTypeUuid);
        assert shapeUuid != null;

        String shapeNid = shapeService.getNid(shapeUuid);

        List<String> shapeCodes = Arrays.asList("", "4", "36", "37", "40", "41", "42", "44", "46", "50", "54", "55",
                "71", "75", "100", "101", "102", "105", "150");
        if (shapeCodes.contains(shapeNid)) {
            String dimension2 = getAttribute(page.selectFirst("input[name=dimension2]"), "value");
            if (dimension2 != null && !dimension2.isEmpty()) {
                if (!nTypeService.compareSize2(nTypeUuid, Float.parseFloat(dimension2))) {
                    nTypeService.setSize2(nTypeUuid, Float.parseFloat(dimension2));
                    result = ParsingResult.CHANGED;
                }
            }
        }
        return result;
    }

    private static ParsingResult parseThickness(Document page, UUID nTypeUuid) {
        ParsingResult result = ParsingResult.NOT_CHANGED;
        NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;

        String epaisseur = getAttribute(page.selectFirst("#epaisseur"), "value");
        if (epaisseur != null && !epaisseur.isEmpty()) {
            if (!nTypeService.compareThickness(nTypeUuid, Float.parseFloat(epaisseur))) {
                nTypeService.setThickness(nTypeUuid, Float.parseFloat(epaisseur));
                result = ParsingResult.CHANGED;
            }
        }
        return result;
    }

    private static ParsingResult parseTechniques(Document page, UUID nTypeUuid) {
        ParsingResult result = ParsingResult.NOT_CHANGED;
        TechniqueService techniqueService = N4JUtil.getInstance().numistaService.techniqueService;
        NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;

        List<HashMap<String, String>> techniques = getAttributesWithTextSelectedOptions(
                page.selectFirst("#techniques"));

        if (techniques != null) {
            nTypeService.equateTechniques(nTypeUuid, techniques.stream()
                    .map(t -> techniqueService.findUuidByNid(t.get("value"))).collect(Collectors
                            .toList()));
        }

        // Technique Additional details
        String techniqueDetail = getAttribute(page.selectFirst("#technique_details"), "value");
        if (techniqueDetail != null && !techniqueDetail.isEmpty()) {
            if (!nTypeService.compareTechniqueAdditionalDetails(nTypeUuid, techniqueDetail)) {
                nTypeService.setTechniqueAdditionalDetails(nTypeUuid, techniqueDetail);
                result = ParsingResult.CHANGED;
            }
        }
        return result;
    }

    private static ParsingResult parseAlignment(Document page, UUID nTypeUuid) {
        ParsingResult result = ParsingResult.NOT_CHANGED;
        NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;

        String alignementCode = getAttribute(page.selectFirst("input[name=alignement][checked=checked]"), "value");
        if (alignementCode != null && !alignementCode.isEmpty()) {
            if (!nTypeService.compareAlignment(nTypeUuid, alignementCode)) {
                nTypeService.setAlignment(nTypeUuid, alignementCode);
                result = ParsingResult.CHANGED;
            }
        }
        return result;
    }

    private static CompositionMetalParsingData parseCompositionMetal(String metalId, String finenessId,
            Document document) {

        String metalCode = "";
        String metalName = "Unknown";
        String fineness = "";

        Element metalElement = document.selectFirst(metalId);
        if (metalElement != null) {
            Elements metalsElements = metalElement.select("option");
            for (Element metal : metalsElements) {
                if (metal.attributes().get("selected").equals("selected")) {
                    metalCode = metal.attributes().get("value");
                    metalName = metal.text();
                }
            }
        }

        Element fineness1Element = document.selectFirst(finenessId);
        if (fineness1Element != null) {
            // pattern="[0-9]{1,3}(\.[0-9]+)?"
            fineness = fineness1Element.attributes().get("value");
        }

        return new CompositionMetalParsingData(metalCode, metalName, fineness);
    }

    public static UUID setCompositionType(UUID nTypeUuid, String compositionTypeCode, String compositionTypeName) {
        CompositionTypeService compositionTypeService = N4JUtil.getInstance().numistaService.compositionTypeService;
        CompositionService compositionService = N4JUtil.getInstance().numistaService.compositionService;
        NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;

        UUID compositionUuid = nTypeService.getCompositionUuid(nTypeUuid);
        if (compositionUuid != null) {
            UUID compositionTypeUuid = compositionService.getCompositionTypeUuid(compositionUuid);
            if (compositionTypeUuid == null) {
                compositionTypeUuid = compositionTypeService.findUuidByCode(compositionTypeCode);
                if (compositionTypeUuid == null) {
                    // TODO: Throw error
                }
                assert compositionTypeUuid != null;
                compositionService.setCompositionType(compositionUuid, compositionTypeUuid);

            } else {
                if (!compositionTypeService.compareCode(compositionTypeUuid, compositionTypeCode)) {
                    compositionTypeService.setCode(compositionTypeUuid, compositionTypeCode);
                }
            }
        } else {
            compositionUuid = compositionService.save(new Composition()).getUuid();
            UUID compositionTypeUuid = compositionTypeService.findUuidByCode(compositionTypeCode);
            if (compositionTypeUuid == null) {
                // TODO: Throw error
            }
            assert compositionTypeUuid != null;
            compositionService.setCompositionType(compositionUuid, compositionTypeUuid);
            nTypeService.setComposition(nTypeUuid, compositionUuid);
        }

        return compositionUuid;
    }

    // private static boolean isMetalCorrect(HashMap<String, String> hashMap) {
    // return hashMap.get("metalCode") != null &&
    // !hashMap.get("metalCode").isEmpty()
    // && hashMap.get("metalName") != null && !hashMap.get("metalName").isEmpty();
    // }

    private static ParsingResult setPart1(UUID compositionUuid, CompositionMetalParsingData metalParsingData,
            CompositionPartType compositionMetalType) {
        CompositionService compositionService = N4JUtil.getInstance().numistaService.compositionService;
        MetalService metalService = N4JUtil.getInstance().numistaService.metalService;

        ParsingResult result = ParsingResult.NOT_CHANGED;

        if (metalParsingData == null) {

        }

        UUID part1MetalUuid = compositionService.getPart1MetalUuid(compositionUuid);
        if (part1MetalUuid != null) {
            if (!metalService.compareName(part1MetalUuid, metalParsingData.metalName())) {
                metalService.setName(part1MetalUuid, metalParsingData.metalName());
            }
        } else {
            part1MetalUuid = metalService.save(new Metal(metalParsingData.metalCode(), metalParsingData.metalName()))
                    .getUuid();
            compositionService.setPart1Metal(compositionUuid, part1MetalUuid);
        }

        if (!compositionService.comparePart1Type(compositionUuid, compositionMetalType)) {
            compositionService.setPart1Type(compositionUuid, compositionMetalType);
        }

        if (!compositionService.comparePart1MetalFineness(compositionUuid, metalParsingData.fineness())) {
            compositionService.setPart1MetalFineness(compositionUuid, metalParsingData.fineness());
        }

        return result;
    }

    private static ParsingResult setPart2(UUID compositionUuid, CompositionMetalParsingData metalParsingData,
            CompositionPartType compositionMetalType) {
        CompositionService compositionService = N4JUtil.getInstance().numistaService.compositionService;
        MetalService metalService = N4JUtil.getInstance().numistaService.metalService;
        ParsingResult result = ParsingResult.NOT_CHANGED;

        UUID part2MetalUuid = compositionService.getPart2MetalUuid(compositionUuid);
        if (part2MetalUuid != null) {
            if (!metalService.compareName(part2MetalUuid, metalParsingData.metalName())) {
                metalService.setName(part2MetalUuid, metalParsingData.metalName());
            }
        } else {
            part2MetalUuid = metalService.save(new Metal(metalParsingData.metalCode(), metalParsingData.metalName()))
                    .getUuid();
            compositionService.setPart2Metal(compositionUuid, part2MetalUuid);
        }

        if (!compositionService.comparePart2Type(compositionUuid, compositionMetalType)) {
            compositionService.setPart2Type(compositionUuid, compositionMetalType);
        }

        if (!compositionService.comparePart2MetalFineness(compositionUuid, metalParsingData.fineness())) {
            compositionService.setPart2MetalFineness(compositionUuid, metalParsingData.fineness());
        }

        return result;
    }

    private static ParsingResult setPart3(UUID compositionUuid, CompositionMetalParsingData metalParsingData,
            CompositionPartType compositionMetalType) {
        CompositionService compositionService = N4JUtil.getInstance().numistaService.compositionService;
        MetalService metalService = N4JUtil.getInstance().numistaService.metalService;
        ParsingResult result = ParsingResult.NOT_CHANGED;

        UUID part3MetalUuid = compositionService.getPart3MetalUuid(compositionUuid);
        if (part3MetalUuid != null) {
            if (!metalService.compareName(part3MetalUuid, metalParsingData.metalName())) {
                metalService.setName(part3MetalUuid, metalParsingData.metalName());
            }
        } else {
            part3MetalUuid = metalService.save(new Metal(metalParsingData.metalCode(), metalParsingData.metalName()))
                    .getUuid();
            compositionService.setPart3Metal(compositionUuid, part3MetalUuid);
        }

        if (!compositionService.comparePart3Type(compositionUuid, compositionMetalType)) {
            compositionService.setPart3Type(compositionUuid, compositionMetalType);
        }

        if (!compositionService.comparePart3MetalFineness(compositionUuid, metalParsingData.fineness())) {
            compositionService.setPart3MetalFineness(compositionUuid, metalParsingData.fineness());
        }

        return result;
    }

    private static ParsingResult setPart4(UUID compositionUuid, CompositionMetalParsingData metalParsingData,
            CompositionPartType compositionMetalType) {
        CompositionService compositionService = N4JUtil.getInstance().numistaService.compositionService;
        MetalService metalService = N4JUtil.getInstance().numistaService.metalService;
        ParsingResult result = ParsingResult.NOT_CHANGED;

        UUID part4MetalUuid = compositionService.getPart4MetalUuid(compositionUuid);
        if (part4MetalUuid != null) {
            if (!metalService.compareName(part4MetalUuid, metalParsingData.metalName())) {
                metalService.setName(part4MetalUuid, metalParsingData.metalName());
            }
        } else {
            part4MetalUuid = metalService.save(new Metal(metalParsingData.metalCode(), metalParsingData.metalName()))
                    .getUuid();
            compositionService.setPart4Metal(compositionUuid, part4MetalUuid);
        }

        if (!compositionService.comparePart4Type(compositionUuid, compositionMetalType)) {
            compositionService.setPart4Type(compositionUuid, compositionMetalType);
        }

        if (!compositionService.comparePart4MetalFineness(compositionUuid, metalParsingData.fineness())) {
            compositionService.setPart4MetalFineness(compositionUuid, metalParsingData.fineness());
        }

        return result;
    }

}