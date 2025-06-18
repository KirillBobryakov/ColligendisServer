package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.Composition;
import bkv.colligendis.database.entity.numista.CompositionType;
import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.service.numista.CompositionMetalType;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class TechnicalDataParser extends NumistaPartParser {

    public TechnicalDataParser() {
        super((page, nType) -> {
            ParseEvent result = ParseEvent.NOT_CHANGED;

            ParseEvent[] parseFunctions = new ParseEvent[] {
                    parseComposition(page, nType),
                    parseShape(page, nType),
                    parseWeight(page, nType),
                    parseSize(page, nType),
                    parseThickness(page, nType),
                    parseTechniques(page, nType),
                    parseAlignment(page, nType),
            };

            for (ParseEvent partResult : parseFunctions) {
                if (partResult == ParseEvent.ERROR) {
                    return ParseEvent.ERROR;
                } else if (partResult == ParseEvent.CHANGED) {
                    result = ParseEvent.CHANGED;
                }
            }

            return result;
        });

        this.partName = "TechnicalData";
    }

    private static ParseEvent parseComposition(Document page, NType nType) {
        ParseEvent  result = ParseEvent.NOT_CHANGED;

        HashMap<String, String> metalType = getAttributeWithTextSelectedOption(page, "#metal_type");
        if (metalType != null) {

            if (metalType.get("value") != null && !metalType.get("value").isEmpty()) {
                if (nType.getComposition() == null) {
                    nType.setComposition(new Composition());
                }
                nType.getComposition().setCompositionType(CompositionType.valueOf(metalType.get("value")));
            }


            // Metals
//            String metal_value_empty = "Unknown";
//            String metal_value_38 = "Acmonital";
//            String metal_value_45 = "Aluminium";
//            String metal_value_50 = "Aluminium-brass";
//            String metal_value_10 = "Aluminium-bronze";
//            String metal_value_31 = "Aluminium-magnesium";
//            String metal_value_30 = "Aluminium-nickel-bronze";
//            String metal_value_54 = "Aluminium-zinc-bronze";
//            String metal_value_34 = "Bakelite";
//            String metal_value_7 = "Billon";
//            String metal_value_4 = "Brass";
//            String metal_value_5 = "Bronze";
//            String metal_value_65 = "Bronze-nickel";
//            String metal_value_37 = "Bronzital";
//            String metal_value_24 = "Cardboard";
//            String metal_value_63 = "Ceramic";
//            String metal_value_41 = "Chromium";
//            String metal_value_39 = "Clay composite";
//            String metal_value_3 = "Copper";
//            String metal_value_32 = "Copper-aluminium";
//            String metal_value_46 = "Copper-aluminium-nickel";
//            String metal_value_2 = "Copper-nickel";
//            String metal_value_36 = "Copper-nickel-iron";
//            String metal_value_17 = "Electrum";
            //  <option value="60">Fiber</option>
            //  <option value="59">Florentine bronze</option>
            //  <option value="48">Gilding metal</option>
            //  <option value="64">Glass</option>
            //  <option value="6">Gold</option>
            //  <option value="74">Iridium</option>
            //  <option value="13">Iron</option>
            //  <option value="21">Lead</option>
            //  <option value="62">Leaded copper</option>
            //  <option value="56">Magnesium</option>
            //  <option value="28">Manganese-brass</option>
            //  <option value="8">Nickel</option>
            //  <option value="16">Nickel brass</option>
            //  <option value="12">Nickel silver</option>
            //  <option value="53">Nickel-steel</option>
            //  <option value="55">Nickel-zinc</option>
            //  <option value="49">Niobium</option>
            //  <option value="18">Nordic gold</option>
            //  <option value="52">Orichalcum</option>
            //  <option value="72">Other</option>
            //  <option value="44">Palladium</option>
            //  <option value="25">Pewter</option>
            //  <option value="14">Plastic</option>
            //  <option value="22">Platinum</option>
            //  <option value="26">Porcelain</option>
            //  <option value="33">Potin</option>
            //  <option value="43">Resin</option>
            //  <option value="70">Rhodium</option>
            //  <option value="71">Ruthenium</option>
            //  <option value="1">Silver</option>
            //  <option value="15">Stainless steel</option>
            //  <option value="9">Steel</option>
            //  <option value="40">Tantalum</option>
            //  <option value="19">Tin</option>
            //  <option value="58">Tin-brass</option>
            //  <option value="57">Tin-lead</option>
            //  <option value="61">Tin-zinc</option>
            //  <option value="35">Titanium</option>
            //  <option value="23">Tombac</option>
            //  <option value="47">Virenium</option>
            //  <option value="20">Wood</option>
            //  <option value="27">Zamak</option>
            //  <option value="11">Zinc</option>
            //  <option value="42">Zinc-aluminium</option> </select>


            if (metalType.get("value").equals("plain")) {
                HashMap<String, String> metal = parseCompositionMetal("#metal1", "#fineness1", page);
                setMetal1(nType.getComposition(), metal, null);

            } else if (metalType.get("value").equals("plated")) {
                //Core
                HashMap<String, String> coreMetal = parseCompositionMetal("#metal1", "#fineness1", page);
                setMetal1(nType.getComposition(), coreMetal, CompositionMetalType.core);

                //Plating
                HashMap<String, String> platingMetal = parseCompositionMetal("#metal2", "#fineness2", page);
                setMetal2(nType.getComposition(), platingMetal, CompositionMetalType.plating);
            } else if (metalType.get("value").equals("clad")) {

                //Core
                HashMap<String, String> coreMetal = parseCompositionMetal("#metal1", "#fineness1", page);
                setMetal1(nType.getComposition(), coreMetal, CompositionMetalType.core);

                //Clad
                HashMap<String, String> cladMetal = parseCompositionMetal("#metal2", "#fineness2", page);
                setMetal2(nType.getComposition(), cladMetal, CompositionMetalType.clad);

            } else if (metalType.get("value").equals("bimetallic")) {

                //Center
                HashMap<String, String> centerMetal = parseCompositionMetal("#metal1", "#fineness1", page);
                setMetal1(nType.getComposition(), centerMetal, CompositionMetalType.center);

                //Ring
                HashMap<String, String> ringMetal = parseCompositionMetal("#metal2", "#fineness2", page);
                setMetal2(nType.getComposition(), ringMetal, CompositionMetalType.ring);

            } else if (metalType.get("value").equals("bimetallic_plated")) {

                //Center Core
                HashMap<String, String> centerCoreMetal = parseCompositionMetal("#metal1", "#fineness1", page);
                setMetal1(nType.getComposition(), centerCoreMetal, CompositionMetalType.center_core);

                //Center Plating
                HashMap<String, String> centerPlatingMetal = parseCompositionMetal("#metal2", "#fineness2", page);
                setMetal2(nType.getComposition(), centerPlatingMetal, CompositionMetalType.center_plating);

                //Ring
                HashMap<String, String> ringMetal = parseCompositionMetal("#metal3", "#fineness3", page);
                setMetal3(nType.getComposition(), ringMetal, CompositionMetalType.ring);

            } else if (metalType.get("value").equals("bimetallic_plated_ring")) {

                //Center
                HashMap<String, String> centerMetal = parseCompositionMetal("#metal1", "#fineness1", page);
                setMetal1(nType.getComposition(), centerMetal, CompositionMetalType.center);

                //Ring core
                HashMap<String, String> ringCoreMetal = parseCompositionMetal("#metal2", "#fineness2", page);
                setMetal2(nType.getComposition(), ringCoreMetal, CompositionMetalType.ring_core);

                //Ring plating
                HashMap<String, String> ringPlatingMetal = parseCompositionMetal("#metal3", "#fineness3", page);
                setMetal3(nType.getComposition(), ringPlatingMetal, CompositionMetalType.ring_plating);

            } else if (metalType.get("value").equals("bimetallic_plated_plated")) {

                //Center Core
                HashMap<String, String> centerCoreMetal = parseCompositionMetal("#metal1", "#fineness1", page);
                setMetal1(nType.getComposition(), centerCoreMetal, CompositionMetalType.center_core);

                //Center Plating
                HashMap<String, String> centerPlatingMetal = parseCompositionMetal("#metal2", "#fineness2", page);
                setMetal2(nType.getComposition(), centerPlatingMetal, CompositionMetalType.center_plating);

                //Ring core
                HashMap<String, String> ringCoreMetal = parseCompositionMetal("#metal3", "#fineness3", page);
                setMetal3(nType.getComposition(), ringCoreMetal, CompositionMetalType.ring_core);

                //Ring plating
                HashMap<String, String> ringPlatingMetal = parseCompositionMetal("#metal4", "#fineness4", page);
                setMetal4(nType.getComposition(), ringPlatingMetal, CompositionMetalType.ring_plating);

            } else if (metalType.get("value").equals("bimetallic_clad")) {
                //Center Core
                HashMap<String, String> centerCoreMetal = parseCompositionMetal("#metal1", "#fineness1", page);
                setMetal1(nType.getComposition(), centerCoreMetal, CompositionMetalType.center_core);

                //Center Clad
                HashMap<String, String> centerCladMetal = parseCompositionMetal("#metal2", "#fineness2", page);
                setMetal2(nType.getComposition(), centerCladMetal, CompositionMetalType.center_clad);

                //Ring
                HashMap<String, String> ringMetal = parseCompositionMetal("#metal3", "#fineness3", page);
                setMetal3(nType.getComposition(), ringMetal, CompositionMetalType.ring);

            } else if (metalType.get("value").equals("trimetallic")) {

                //Center
                HashMap<String, String> centerMetal = parseCompositionMetal("#metal1", "#fineness1", page);
                setMetal1(nType.getComposition(), centerMetal, CompositionMetalType.center);

                //Middle ring
                HashMap<String, String> middleRingMetal = parseCompositionMetal("#metal2", "#fineness2", page);
                setMetal2(nType.getComposition(), middleRingMetal, CompositionMetalType.middle_ring);

                //Outer ring
                HashMap<String, String> outerRingMetal = parseCompositionMetal("#metal2", "#fineness2", page);
                setMetal3(nType.getComposition(), outerRingMetal, CompositionMetalType.outer_ring);
            }

            result = ParseEvent.CHANGED;
        }




        //Banknote Composition
        //<option value="68">Hybrid substrate</option>
        // <option value="73">Other</option>
        // <option value="66">Paper</option>
        // <option value="67">Polymer</option>
        // <option value="69">Silk</option>


        if (nType.getCollectibleType().getCollectibleTypeParent().getName().equals("Banknotes")) {
            HashMap<String, String> compositionHashMap = getAttributeWithTextSelectedOption(page, "#metal1");
            if (compositionHashMap != null) {

                if (isValueAndTextNotNullAndNotEmpty(compositionHashMap)) {
                    if (nType.getComposition() == null) {
                        nType.setComposition(new Composition());
                    }
                    setMetal1(nType.getComposition(), compositionHashMap, null);
                    result = ParseEvent.CHANGED;
                }
            }
        }


        //Metal Additional details
        String metalDetails = getAttribute(page.selectFirst("#metal_details"), "value");
        if (metalDetails != null && !metalDetails.isEmpty()) {
            nType.getComposition().setCompositionAdditionalDetails(metalDetails);
            result = ParseEvent.CHANGED;
        }


        return result;
    }




    private static ParseEvent parseShape(Document page, NType nType) {
        //coins
        //<option value="">Unknown</option> <option value="51">Annular sector</option> <option value="49">Cob</option> <option value="10">Decagonal (10-sided)</option> <option value="12">Dodecagonal (12-sided)</option> <option value="47">Equilateral curve heptagon (7-sided)</option> <option value="62">Half circle</option> <option value="53">Heart</option> <option value="11">Hendecagonal (11-sided)</option> <option value="7">Heptagonal (7-sided)</option> <option value="57">Hexadecagonal (16-sided)</option> <option value="6">Hexagonal (6-sided)</option> <option value="58">Icosagonal (20-sided)</option> <option value="59">Icosidigonal (22-sided)</option> <option value="66">Icosihenagonal (21-sided)</option> <option value="65">Icosipentagonal (25-sided)</option> <option value="56">Icositetragonal (24-sided)</option> <option value="45">Irregular</option> <option value="42">Klippe</option> <option value="72">Knife</option> <option value="9">Nonagonal (9-sided)</option> <option value="8">Octagonal (8-sided)</option> <option value="48">Octagonal (8-sided) with a hole</option> <option value="68">Octodecagonal (18-sided)</option> <option value="50">Other</option> <option value="36">Oval</option> <option value="37">Oval with a loop</option> <option value="5">Pentagonal (5-sided)</option> <option value="63">Quarter circle</option> <option value="4">Rectangular</option> <option value="46">Rectangular (irregular)</option> <option value="43">Reuleaux triangle</option> <option value="64">Rhombus</option> <option value="1" selected>Round</option> <option value="2">Round (irregular)</option> <option value="34">Round with 4 pinches</option> <option value="33">Round with a loop</option> <option value="31">Round with a round hole</option> <option value="32">Round with a square hole</option> <option value="38">Round with cutouts</option> <option value="39">Round with groove(s)</option> <option value="15">Scalloped</option> <option value="20">Scalloped (with 10 notches)</option> <option value="21">Scalloped (with 11 notches)</option> <option value="22">Scalloped (with 12 notches)</option> <option value="23">Scalloped (with 13 notches)</option> <option value="24">Scalloped (with 14 notches)</option> <option value="26">Scalloped (with 16 notches)</option> <option value="27">Scalloped (with 17 notches)</option> <option value="29">Scalloped (with 20 notches)</option> <option value="14">Scalloped (with 4 notches)</option> <option value="18">Scalloped (with 8 notches)</option> <option value="30">Scalloped with a hole</option> <option value="35">Scyphate</option> <option value="54">Spade</option> <option value="73">Spanish flower</option> <option value="44">Square</option> <option value="41">Square (irregular)</option> <option value="55">Square with angled corners</option> <option value="40">Square with rounded corners</option> <option value="71">Square with scalloped edges</option> <option value="74">Tetradecagonal (14-sided)</option> <option value="3">Triangular</option> <option value="13">Tridecagonal (13-sided)</option>

        //banknotes
        // <option value="">Unknown</option> <option value="150">Other</option> <option value="100" selected>Rectangular</option> <option value="102">Rectangular (hand cut)</option> <option value="101">Rectangular with undulating edge</option> <option value="103">Round</option> <option value="105">Square</option> <option value="104">Triangular</option>

        ParseEvent result = ParseEvent.NOT_CHANGED;

        HashMap<String, String> shape = getAttributeWithTextSelectedOption(page, "#shape");
        if (shape != null && isValueAndTextNotNullAndNotEmpty(shape)) {
            nType.setShape(N4JUtil.getInstance().numistaService.shapeService.update(nType.getShape(), shape.get("value"), shape.get("text")));
            result = ParseEvent.CHANGED;
        }

        //Shape Additional details
        String shapeDetails = getAttribute(page.selectFirst("#shape_details"), "value");
        if (shapeDetails != null && !shapeDetails.isEmpty()) {
            nType.setShapeAdditionalDetails(shapeDetails);
            result = ParseEvent.CHANGED;
        }

        return result;
    }



    private static ParseEvent parseWeight(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        String poids = getAttribute(page.selectFirst("#poids"), "value");
        if (poids != null && !poids.isEmpty()) {
            nType.setWeight(poids);
            result = ParseEvent.CHANGED;
        }
        return result;
    }


    private static ParseEvent parseSize(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String dimension = getAttribute(page.selectFirst("#dimension"), "value");
        if (dimension != null && !dimension.isEmpty()) {
            nType.setSize(dimension);
            result = ParseEvent.CHANGED;
        }

        //Second dimension
        //<option value="42">Klippe</option>
        //<option value="50">Other</option>
        //<option value="36">Oval</option>
        //<option value="37">Oval with a loop</option>
        //<option value="4">Rectangular</option>
        //<option value="46">Rectangular (irregular)</option>
        //<option value="54">Spade</option>
        //<option value="44">Square</option>
        //<option value="41">Square (irregular)</option>
        //<option value="55">Square with angled corners</option>
        //<option value="40">Square with rounded corners</option>
        //<option value="71">Square with scalloped edges</option>

        if (nType.getShape() != null && nType.getShape().getNid() != null && !nType.getShape().getNid().isEmpty()) {
            List<String> shapeCodes = Arrays.asList("4", "36", "37", "40", "41", "42", "44", "46", "50", "54", "55", "71", "100", "101", "102", "105", "150");
            if (shapeCodes.contains(nType.getShape().getNid())) {
                String dimension2 = getAttribute(page.selectFirst("input[name=dimension2]"), "value");
                if (dimension2 != null && !dimension2.isEmpty()) {
                    nType.setSize2(dimension2);
                    result = ParseEvent.CHANGED;
                }
            }
        }
        return result;
    }

    private static ParseEvent parseThickness(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String epaisseur = getAttribute(page.selectFirst("#epaisseur"), "value");
        if (epaisseur != null && !epaisseur.isEmpty()) {
            nType.setThickness(epaisseur);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseTechniques(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        nType.getTechniques().clear();

        List<HashMap<String, String>> techniques = getAttributesWithTextSelectedOptions(page.selectFirst("#techniques"));
        if (techniques != null) {
            for (HashMap<String, String> technique : techniques) {
                if (isValueAndTextNotNullAndNotEmpty(technique)) {
                    nType.getTechniques().add(N4JUtil.getInstance().numistaService.techniqueService.findByNid(technique.get("value"), technique.get("text")));
                    result = ParseEvent.CHANGED;
                }
            }
        }

        // Technique Additional details
        String techniqueDetail = getAttribute(page.selectFirst("#technique_details"), "value");
        if (techniqueDetail != null && !techniqueDetail.isEmpty()) {
            nType.setTechniqueAdditionalDetails(techniqueDetail);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseAlignment(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String alignementCode = getAttribute(page.selectFirst("input[name=alignement][checked=checked]"), "value");
        if (alignementCode != null && !alignementCode.isEmpty()) {
            nType.setAlignment(alignementCode);
            result = ParseEvent.CHANGED;
        }
        return result;
    }


    private static HashMap<String, String> parseCompositionMetal(String metalId, String finenessId, Document document) {

        HashMap<String, String> parsedCompositionMetal = new HashMap<>();

        Element metalElement = document.selectFirst(metalId);
        if (metalElement != null) {
            Elements metalsElements = metalElement.select("option");
            String metalCode = "";
            String metalName = "Unknown";
            for (Element metal : metalsElements) {
                if (metal.attributes().get("selected").equals("selected")) {
                    metalCode = metal.attributes().get("value");
                    metalName = metal.text();
                }
            }
            parsedCompositionMetal.put("metalCode", metalCode);
            parsedCompositionMetal.put("metalName", metalName);

        }

        Element fineness1Element = document.selectFirst(finenessId);
        if (fineness1Element != null) {
            //pattern="[0-9]{1,3}(\.[0-9]+)?"
            String fineness = fineness1Element.attributes().get("value");
            parsedCompositionMetal.put("fineness", fineness);
        }

        return parsedCompositionMetal;
    }

    private static boolean isMetalCorrect(HashMap<String, String> hashMap) {
        return hashMap.get("metalCode") != null && !hashMap.get("metalCode").isEmpty() && hashMap.get("metalName") != null && !hashMap.get("metalName").isEmpty();
    }


    private static void setMetal1(Composition composition, HashMap<String, String> metalHashMap, CompositionMetalType compositionMetalType) {
        if (isMetalCorrect(metalHashMap)) {
            composition.setMetal1(N4JUtil.getInstance().numistaService.metalService.findByNid(metalHashMap.get("metalCode"), metalHashMap.get("metalName")));
            composition.setMetal1Type(compositionMetalType);
            if (metalHashMap.get("fineness") != null && !metalHashMap.get("fineness").isEmpty()) {
                composition.setMetal1Fineness(metalHashMap.get("fineness"));
            }
        } else {
            composition.setMetal1(N4JUtil.getInstance().numistaService.metalService.findByNid(metalHashMap.get("value"), metalHashMap.get("text")));
        }
    }

    private static void setMetal2(Composition composition, HashMap<String, String> metalHashMap, CompositionMetalType compositionMetalType) {
        if (isMetalCorrect(metalHashMap)) {
            composition.setMetal2(N4JUtil.getInstance().numistaService.metalService.findByNid(metalHashMap.get("metalCode"), metalHashMap.get("metalName")));
            composition.setMetal2Type(compositionMetalType);
            if (metalHashMap.get("fineness") != null && !metalHashMap.get("fineness").isEmpty()) {
                composition.setMetal2Fineness(metalHashMap.get("fineness"));
            }
        }
    }

    private static void setMetal3(Composition composition, HashMap<String, String> metalHashMap, CompositionMetalType compositionMetalType) {
        if (isMetalCorrect(metalHashMap)) {
            composition.setMetal3(N4JUtil.getInstance().numistaService.metalService.findByNid(metalHashMap.get("metalCode"), metalHashMap.get("metalName")));
            composition.setMetal3Type(compositionMetalType);
            if (metalHashMap.get("fineness") != null && !metalHashMap.get("fineness").isEmpty()) {
                composition.setMetal3Fineness(metalHashMap.get("fineness"));
            }
        }
    }

    private static void setMetal4(Composition composition, HashMap<String, String> metalHashMap, CompositionMetalType compositionMetalType) {
        if (isMetalCorrect(metalHashMap)) {
            composition.setMetal4(N4JUtil.getInstance().numistaService.metalService.findByNid(metalHashMap.get("metalCode"), metalHashMap.get("metalName")));
            composition.setMetal4Type(compositionMetalType);
            if (metalHashMap.get("fineness") != null && !metalHashMap.get("fineness").isEmpty()) {
                composition.setMetal4Fineness(metalHashMap.get("fineness"));
            }
        }
    }


}
