package bkv.colligendis.utils;

import bkv.colligendis.database.entity.numista.*;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.service.numista.CompositionMetalType;
import bkv.colligendis.database.service.numista.NTypeService;
import bkv.colligendis.utils.numista.PART_TYPE;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class NumistaEditPageUtil {

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.3 Safari/605.1.15";


    public NumistaEditPageUtil() {
    }

    public static boolean loadInfo(String nid) {
        return parse(nid);
    }

    public void parseAllCoins(Document document) {
        int totalPieces = 0;

        if (document == null) return;

        Elements elements = document.select("details[class=all_coins]");
        for (Element element : elements) {
            Element summaryElement = element.selectFirst("summary");
            if (summaryElement != null) {

                String location = summaryElement.text();

                List<String> splitLocations = Arrays.asList(location.split(" › "));


                UUID countryUuid = null;
                List<UUID> subjectUuidList = new ArrayList<>();
                UUID issuerUuid = null;


                for (int i = 0; i < splitLocations.size(); i++) {
                    if (i == 0) { // first element = Country
                        String countryName = splitLocations.get(i);
                        if (countryName == null || countryName.isEmpty()) {
                            System.out.println("ERROR country =" + element);
                            return;
                        }

                        System.out.println(countryName);

                        countryUuid = N4JUtil.getInstance().numistaService.countryService.findCountryUuidByNameOrCreate(countryName);

                        if (splitLocations.size() == 1) { // if only one element? then Issuer = Country
                            issuerUuid = N4JUtil.getInstance().numistaService.issuerService.findIssuerUuidByNameOrCreate(countryName);

                            if (!N4JUtil.getInstance().numistaService.countryService.hasContainsIssuerRelationshipToIssuer(countryUuid, issuerUuid)) {
                                N4JUtil.getInstance().numistaService.countryService.setContainsIssuer(countryUuid, issuerUuid);
                            }
                        }

                    } else if (i == splitLocations.size() - 1) { // last element = Issuer
                        String issuerName = splitLocations.get(i);
                        System.out.println("     " + "     " + "     " + issuerName);

                        issuerUuid = N4JUtil.getInstance().numistaService.issuerService.findIssuerUuidByNameOrCreate(issuerName);

                        if (subjectUuidList.isEmpty()) { // have not any Subjects
                            if (!N4JUtil.getInstance().numistaService.countryService.hasContainsIssuerRelationshipToIssuer(countryUuid, issuerUuid)) {
                                N4JUtil.getInstance().numistaService.countryService.setContainsIssuer(countryUuid, issuerUuid);
                            }
                        } else { // have any Subjects

                            if(!N4JUtil.getInstance().numistaService.subjectService.hasContainsChildSubjectToSubject(subjectUuidList.get(subjectUuidList.size() - 1), issuerUuid)){
                                N4JUtil.getInstance().numistaService.subjectService.setContainsChildSubject(subjectUuidList.get(subjectUuidList.size() - 1), issuerUuid);
                            }

                        }

                    } else if (i == 1) {
                        String firstSubjectName = splitLocations.get(i);
                        System.out.println("     " + firstSubjectName);

                        UUID subUuid = N4JUtil.getInstance().numistaService.subjectService.findSubjectUuidByNameOrCreate(firstSubjectName);
                        subjectUuidList.add(subUuid);

                        if (!N4JUtil.getInstance().numistaService.subjectService.hasContainsChildSubjectToSubject(countryUuid, subUuid)) {
                            N4JUtil.getInstance().numistaService.subjectService.setContainsChildSubject(countryUuid, subUuid);
                        }
                    } else {
                        String subjectName = splitLocations.get(i);
                        System.out.println("     " + "     " + subjectName);

                        UUID subUuid = N4JUtil.getInstance().numistaService.subjectService.findSubjectUuidByNameOrCreate(subjectName);

                        if (!N4JUtil.getInstance().numistaService.subjectService.hasContainsChildSubjectToSubject(subjectUuidList.get(i - 1), subUuid)) {
                            N4JUtil.getInstance().numistaService.subjectService.setContainsChildSubject(subjectUuidList.get(i - 1), subUuid);
                        }

                        subjectUuidList.add(subUuid);
                    }
                }

                Elements typesElements = element.select("a");

                UUID finalIssuerUuid = issuerUuid;

                assert finalIssuerUuid != null;

                totalPieces += typesElements.size();
                typesElements.stream().parallel().forEach(type -> {
                    String typeNid = type.attributes().get("href").replace("pieces", "").replace(".html", "");
                    N4JUtil.getInstance().numistaService.nTypeService.findNTypeUuidByTitleOrCreate(typeNid, type.text(), finalIssuerUuid);
                });

                System.out.println("     " + "     " + "     " + "     " + "pieces: " + typesElements.size() + "(" + totalPieces + ")");

            }
        }
    }

    public void parseAndCheckNotActualCoins() {
        Document document = getNumistaAllCoins();
        List<String> notActualNTypeNidList = N4JUtil.getInstance().numistaService.nTypeService.findNotActualNTypeNidList();

        for (String nid : notActualNTypeNidList) {
            Element element = document.selectFirst("a[href*=" + nid + "]");
            if (element != null) {
                System.out.println(nid + " find!");
            }

        }

    }


    private static String getAttribute(Element element, String key) {
        if (element != null && !element.attributes().get(key).isEmpty()) {
            return element.attributes().get(key);
        }
        return null;
    }

    private static String getTagText(Element element) {
        if (element != null && !element.text().isEmpty()) {
            return element.text();
        }
        return null;
    }

    private static HashMap<String, String> getAttributeWithTextSingleOption(Element element, String key) {
        if (element != null) {
            Element option = element.selectFirst("option");
            if (option != null) {
                HashMap<String, String> result = new HashMap<>();
                result.put(key, option.attributes().get(key));
                result.put("text", option.text());
                return result;
            }
        }
        return null;
    }

    private static HashMap<String, String> getAttributeWithTextSelectedOption(Element element) {
        if (element != null) {
            return element.select("option").stream().filter(option -> option.attributes().hasKey("selected")).findFirst().map(option -> {
                HashMap<String, String> r = new HashMap<>();
                r.put("value", option.attributes().get("value"));
                r.put("text", option.text());
                return r;
            }).orElse(null);
        }
        return null;
    }

    private static List<HashMap<String, String>> getAttributesWithTextSelectedOptions(Element element) {
        if (element != null) {
            return element.select("option").stream().filter(option -> option.attributes().hasKey("selected")).findFirst().map(option -> {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("value", option.attributes().get("value"));
                hashMap.put("text", option.text());
                return hashMap;
            }).stream().collect(Collectors.toList());
        }
        return null;
    }


    private static List<String> getTextsSelectedOptions(Element element) {
        if (element != null) {
            return element.select("option").stream().filter(option -> option.attributes().hasKey("selected")).map(Element::text).collect(Collectors.toList());
        }
        return null;
    }


    /**
     * Find in Map {@code hashMap} values with keys ("value", "text") and check on {@code null} and {@code empty}
     *
     * @param hashMap Map with elements with "value" and "text" keys
     * @return {@code true} if values by "value" and "text" keys in Map is not null and is not empty, else - {@code false}.
     */
    private static boolean isValueAndTextNotNullAndNotEmpty(HashMap<String, String> hashMap) {
        return hashMap.get("value") != null && !hashMap.get("value").isEmpty() && hashMap.get("text") != null && !hashMap.get("text").isEmpty();
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


    public static boolean parse(String nid) {
        DebugUtil.showInfo(NumistaEditPageUtil.class, "Parsing page nid=" + nid);

        Document page = getNumistaPage(nid);
        System.out.println(page);

        if (page == null) return false;

        NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;


        boolean exists = nTypeService.existsByNid(nid);

        if (exists) {
            DebugUtil.showWarning(NumistaEditPageUtil.class, "Parsing existing Numista Type with nid=" + nid);
        } else {
            DebugUtil.showError(NumistaEditPageUtil.class, "Parsing new Numista Type with nid=" + nid);

            //todo временное решение до заполнения базы через списки всех монет, банкнот и экзонимии
            String designation = getAttribute(page.selectFirst("#designation"), "value");
            nTypeService.save(new NType(nid, designation));
//            return false;
        }

        UUID nTypeUuid = nTypeService.findNTypeUuidByNid(nid);

        if (!parseCategory(page, nTypeUuid)) {
            DebugUtil.showError(NumistaEditPageUtil.class, "Parsing Numista Type with nid=" + nid + ". Category parsing error");
            return false;
        }

        // Title (designation) | title
        if (!checkTitle(page, nTypeUuid)) return false;


        // Issuing authority | issuer
        UUID issuerUuid = parseIssuer(page, nTypeUuid, nid);
        if (issuerUuid == null) return false;

        //Ruling authority | ruler
        loadRulers(page, nTypeUuid, nid);

        //Issuing entity | issuing_entity
        loadIssuingEntity(page, nTypeUuid, nid, issuerUuid);

        //Currency | currency
        UUID currencyUuid = loadCurrency(page, nTypeUuid, nid, issuerUuid);
        if (currencyUuid == null) return false;


        //Face value in word form | value text
        loadDenomination(page, nTypeUuid, nid, currencyUuid);

        NType nType = null;
        //in figure form | value numeric_value
        //pattern="([0-9 ]+([./⁄][0-9]+)?|½|⅓|⅔|¼|¾|⅕|⅖|⅗|⅘|⅙|⅚|⅐|⅛|⅜|⅝|⅞|⅑|⅒)
        loadNumericValue(page, nType);


        loadType(page, nType);

        //Commemorated event | commemorated_topic
        loadCommemoratedEvent(page, nType);

        //Series
        loadSeries(page, nType);

        //Demonetized
        loadDemonetized(page, nType);

        //Reference number | references
        loadReferences(page, nType);

        /*
           Technical data
         */


        //Composition
        loadComposition(page, nType);

        // Shape
        loadShape(page, nType);


        //Weight in grams | weight
        loadWeight(page, nType);


        //Size | size
        loadSize(page, nType);


        //Thickness | thickness
        loadThickness(page, nType);

        //Manufacturing technique(s)
        loadTechniques(page, nType);


        //Alignment
        loadAlignment(page, nType);

        /*
         *  Obverse (head) | obverse
         */
        if (nType != null && nType.getObverse() == null) nType.setObverse(new NTypePart(PART_TYPE.OBVERSE));

        //Engraver(s)
        loadObverseEngravers(page, nType);

        //Designer(s)
        loadObverseDesigners(page, nType);

        // Description with keywords
        loadObverseDescription(page, nType);

        //Lettering
        loadObverseLettering(page, nType);

        //Script(s)
        loadObverseScripts(page, nType);

        //Unabridged legend
        loadObverseUnabridgedLegend(page, nType);

        //Translation of the lettering
        loadObverseLetteringTranslation(page, nType);

        //Picture
        loadObversePicture(page, nType);

        N4JUtil.getInstance().numistaService.nTypePartService.save(nType.getObverse());

        /*
         *  Reverse (back)
         */
        if (nType.getReverse() == null) nType.setReverse(new NTypePart(PART_TYPE.REVERSE));


        //Engraver(s)
        loadReverseEngravers(page, nType);

        //Designer(s)
        loadReverseDesigners(page, nType);

        // Description with keywords
        loadReverseDescription(page, nType);

        //Lettering
        loadReverseLettering(page, nType);

        //Script(s)
        loadReverseScripts(page, nType);

        //Unabridged legend
        loadReverseUnabridgedLegend(page, nType);

        //Translation of the lettering
        loadReverseLetteringTranslation(page, nType);

        //Picture
        loadReversePicture(page, nType);

        N4JUtil.getInstance().numistaService.nTypePartService.save(nType.getReverse());

        /*
         * Edge
         */

        if (nType.getCategory().getName().equals(Category.COIN) || nType.getCategory().getName().equals(Category.EXONUMIA)) {

            if (nType.getEdge() == null) nType.setEdge(new NTypePart(PART_TYPE.EDGE));

            //Description with keywords
            loadEdgeDescription(page, nType);

            //Lettering
            loadEdgeLettering(page, nType);

            //Script(s)
            loadEdgeScripts(page, nType);

            //Unabridged legend
            loadEdgeUnabridgedLegend(page, nType);

            //Translation of the lettering
            loadEdgeLetteringTranslation(page, nType);

            //Picture
            loadEdgePicture(page, nType);

            N4JUtil.getInstance().numistaService.nTypePartService.save(nType.getEdge());
        }

        /*
         * Watermark
         */

        if (nType.getCategory().getName().equals(Category.BANKNOTE)) {
            if (nType.getWatermark() == null) nType.setWatermark(new NTypePart(PART_TYPE.WATERMARK));

            //Description
            loadWatermarkDescription(page, nType);

            // Picture of watermark
            loadWatermarkPicture(page, nType);

            if (nType.getWatermark().getDescription() == null && nType.getWatermark().getPicture() == null) {
                nType.setWatermark(null);
            } else {
                N4JUtil.getInstance().numistaService.nTypePartService.save(nType.getWatermark());
            }
        }

        /*
         *  Mint(s)
         */

        loadMints(page, nType);

        /*
         * Printer(s)
         */

        if (nType.getCategory().getName().equals(Category.BANKNOTE)) {
            loadPrinters(page, nType);
        }

        /*
          Comments
         */

        loadComments(page, nType);

        /*
         * Tag References
         */

        loadTagReferences(page, nType);

        /*
         * Links
         */

        loadLinks(page, nType);


        /*
         * Varieties
         */

        //Mintage calendar
        loadMintageCalendar(page, nType);

        //Issues

        loadIssues(page, nType);


        N4JUtil.getInstance().numistaService.nTypeService.save(nType);
        return true;
    }


    private static boolean parseCategory(Document page, UUID nTypeUuid) {
//        NType nType = new NType("ff");

        String category = "";
        String nid = "";

        Element mainBreadcrumb = page.selectFirst("#main_breadcrumb");
        if (mainBreadcrumb != null) {
            Element categoryElement = mainBreadcrumb.selectFirst("a[href^=../index.php?ct=]");
            if (categoryElement != null) {
                category = categoryElement.attributes().get("href").replace("../index.php?ct=", "");
                DebugUtil.printProperty("category", category, true, true, true);
            }

            switch (category) {
                case "coin" -> {
                    Element piecesElement = mainBreadcrumb.selectFirst("a[href^=/catalogue/pieces]");
                    if (piecesElement != null) {
                        nid = piecesElement.attributes().get("href").replace("/catalogue/pieces", "").replace(".html", "");
                        DebugUtil.printProperty("id", nid, true, true, true);
                    }
                }
                case "banknote" -> {
                    Element piecesElement = mainBreadcrumb.selectFirst("a[href^=/catalogue/note]");
                    if (piecesElement != null) {
                        nid = piecesElement.attributes().get("href").replace("/catalogue/note", "").replace(".html", "");
                        DebugUtil.printProperty("id", nid, true, true, true);
                    }
                }
                case "exonumia" -> {
                    Element piecesElement = mainBreadcrumb.selectFirst("a[href^=/catalogue/exonumia]");
                    if (piecesElement != null) {
                        nid = piecesElement.attributes().get("href").replace("/catalogue/exonumia", "").replace(".html", "");
                        DebugUtil.printProperty("id", nid, true, true, true);
                    }
                }
            }

            Category c = N4JUtil.getInstance().numistaService.categoryService.findByName(category);
            if (c == null) {
                DebugUtil.showError(NumistaEditPageUtil.class, "Unknown Category with name=" + category);
                DebugUtil.showError(NumistaEditPageUtil.class, "Skip parsing NType with Numista number=" + nid);
                return false;
            }

            return N4JUtil.getInstance().numistaService.nTypeService.setUnderCategory(nTypeUuid, c.getUuid());
        }

        DebugUtil.showError(NumistaEditPageUtil.class, "Can't find #main_breadcrumb on page eid: " + nTypeUuid);
        return false;
    }

    private static boolean checkTitle(Document page, UUID nTypeUuid) {
        String designation = getAttribute(page.selectFirst("#designation"), "value");
        DebugUtil.printProperty("title", designation, true, true, true);


        if (N4JUtil.getInstance().numistaService.nTypeService.compareTitle(nTypeUuid, designation)) {
            return true;
        } else {
            DebugUtil.showError(NumistaEditPageUtil.class, "The Title of existing NType is not equals with Title on the page. Ntype's title with eid=" + nTypeUuid + " != " + designation);
            return false;
        }
    }

    private static UUID parseIssuer(Document page, UUID nTypeUuid, String pageNid) {
        HashMap<String, String> emetteur = getAttributeWithTextSingleOption(page.selectFirst("#emetteur"), "value");

        if (emetteur != null) {
            DebugUtil.printProperty("issuer code", emetteur.get("value"), true, true, true);
            DebugUtil.printProperty("issuer name", emetteur.get("text"), true, true, true);

            String code = emetteur.get("value");
            String name = emetteur.get("text") != null ? emetteur.get("text").trim() : null;

            if (isValueAndTextNotNullAndNotEmpty(emetteur)) {
                UUID issuerUuid = N4JUtil.getInstance().numistaService.issuerService.findIssuerUuidByCodeThenName(code, name);

                if (issuerUuid == null) {  //create new Issuer with code and name
                    issuerUuid = N4JUtil.getInstance().numistaService.issuerService.save(new Issuer(code, name)).getUuid();
                }

                if (!N4JUtil.getInstance().numistaService.issuerService.isIssuerCodeEqualsTo(issuerUuid, code)) { // Some Issuers can be without code value (during parsing AllCoins Numista page)
                    N4JUtil.getInstance().numistaService.issuerService.updateIssuerCode(issuerUuid, code);
                    DebugUtil.showWarning(NumistaEditPageUtil.class, "For Issuer: " + name + " find new code. New Code is " + code);
                }

                N4JUtil.getInstance().numistaService.nTypeService.setIssuedBy(nTypeUuid, issuerUuid);
                return issuerUuid;
            } else {
                if (code == null || code.isEmpty()) {
                    DebugUtil.showError(NumistaEditPageUtil.class, "The Issuer's code is null or empty on the page nid: " + pageNid);
                }
                if (name == null || name.isEmpty()) {
                    DebugUtil.showError(NumistaEditPageUtil.class, "The Issuer's name is null or empty on the page nid: " + pageNid);
                }
                return null;
            }
        }

        DebugUtil.showError(NumistaEditPageUtil.class, "Can't find Issuer on the page nid: " + pageNid);

        return null;
    }

    private static void loadRulers(Document page, UUID nTypeUuid, String pageNid) {
        int index = 0;
        HashMap<String, String> rulerHashMap;

        do {
            rulerHashMap = getAttributeWithTextSingleOption(page.selectFirst("#ruler" + index), "value");
            if (rulerHashMap != null) {
                DebugUtil.printProperty("ruler[" + index + "] id", rulerHashMap.get("value"), true, true, true);
                DebugUtil.printProperty("ruler[" + index + "] name", rulerHashMap.get("text"), true, true, true);
                final String rulerNid = rulerHashMap.get("value");
                final String rulerName = rulerHashMap.get("text");

//                if (isValueAndTextNotNullAndNotEmpty(rulerHashMap)) {
//                    UUID rulerUuid = N4JUtil.getInstance().numistaService.rulerService.findByNid(rulerNid);
//
//                    if (rulerUuid == null) {
//                        rulerUuid = N4JUtil.getInstance().numistaService.rulerService.save(new Ruler(rulerNid, rulerName)).getUuid();
//                    }
//
//                    N4JUtil.getInstance().numistaService.nTypeService.setDuringOfRuler(nTypeUuid, rulerUuid);
//
//                } else {
//                    if (rulerNid == null || rulerNid.isEmpty()) {
//                        DebugUtil.showError(NumistaEditPageUtil.class, "The Ruler's code is null or empty on the page nid: " + pageNid);
//                    }
//                    if (rulerName == null || rulerName.isEmpty()) {
//                        DebugUtil.showError(NumistaEditPageUtil.class, "The Ruler's name is null or empty on the page nid: " + pageNid);
//                    }
//                }
            }
            index++;
        } while (rulerHashMap != null);

    }


    /**
     * Load code from
     * "$.get("../get_issuing_entities.php", { country: selection, prefill: "2325"})"
     * line in Numista page. Then load name from get-php request
     *
     * @param page
     * @param nTypeUuid
     * @param nid
     */
    private static boolean loadIssuingEntity(Document page, UUID nTypeUuid, String nid, UUID issuerUuid) {
        Elements elements = page.select("script");


        // try to find a prefill value for IssuingEntity on page
        String issuingEntityCode = null;
        for (Element element : elements) {
            if (!element.childNodes().isEmpty()) {
                String line = Arrays.stream(element.childNodes().get(0).toString().split("\n")).filter(s -> s.contains("$.get(\"../get_issuing_entities.php\"")).findFirst().orElse(null);
                if (line != null) {
                    issuingEntityCode = line.substring(line.indexOf("prefill:") + 10, line.indexOf("\"})"));
                    break;
                }
                System.out.println(element.text());
            }
        }

        if (issuingEntityCode == null || issuingEntityCode.isEmpty()) { //value doesn't set on page
            return false;
        }

        //Value set on page, looking for a node in the database by Code

//        UUID issuingEntityUuid = N4JUtil.getInstance().numistaService.issuingEntityService.findEidByCode(issuingEntityCode);
        UUID issuingEntityUuid = null;

        if (issuingEntityUuid == null) { //didn't find a node in the database

            // load IssuingEntities from https://en.numista.com/catalogue/get_issuing_entities.php?country= ny Issuer

            String issuerCode = N4JUtil.getInstance().numistaService.issuerService.findIssuerCodeByEid(issuerUuid);

            if (issuerCode == null) {
                DebugUtil.showError(NumistaEditPageUtil.class, "Can't find Issuer's Code by Issuer's Eid while loading Issuing Entity for Ntype with nid : " + nid);
                return false;
            }

            Document issuingEntitiesPHPDocument = getNumistaIssuingEntitiesByPHP(issuerCode);
            if (issuingEntitiesPHPDocument != null) {
                Elements optgroups = issuingEntitiesPHPDocument.select("optgroup");

                if (!optgroups.isEmpty()) {  //need to understand what to do with OPTGROUP in IssuingEntities
                    DebugUtil.showError(NumistaEditPageUtil.class, "Find OPTGROUP while parsing IssuingEntities from PHP request with issuer's code: " + issuerCode);
                }


                Elements options = issuingEntitiesPHPDocument.select("option");
                for (Element element : options) {
                    String ieCode = element.attributes().get("value");
                    String ieName = element.text();

//                    UUID ieUuid = N4JUtil.getInstance().numistaService.issuingEntityService.findEidByCode(ieCode);
                    UUID ieUuid = null;
                    if (ieUuid == null) {
                        ieUuid = N4JUtil.getInstance().numistaService.issuingEntityService.save(new IssuingEntity(ieCode, ieName)).getUuid();
                        if (!N4JUtil.getInstance().numistaService.issuerService.setContainsIssuingEntity(issuerUuid, ieUuid)) {  //Every IssuingEntities mast to connect to Issuer
                            DebugUtil.showError(NumistaEditPageUtil.class, "Some error while creating relationship between ISSUER and ISSUING_ENTITY while parsing page with nid: " + nid);
                            return false;
                        }
//                        if(issuingEntityCode.equals(ieCode))
                    } else { //need to check a connection to the Issuer
                        if (!N4JUtil.getInstance().numistaService.issuerService.hasContainsIssuingEntityRelationshipToIssuingEntity(issuerUuid, ieUuid)) {
                            N4JUtil.getInstance().numistaService.issuerService.setContainsIssuingEntity(issuerUuid, ieUuid);
                        }
                    }
                }
            }

//            issuingEntityUuid = N4JUtil.getInstance().numistaService.issuingEntityService.findEidByCode(issuingEntityCode);
            issuingEntityUuid = null;
        }

        if (issuingEntityUuid == null) {
            DebugUtil.showError(NumistaEditPageUtil.class, "Find a IssuingEntity's Code but can't find IssuingEntity in database or create new one while parsing page with nid: " + nid);
            return false;
        }

//        DebugUtil.printProperty("issuing_entity", issuingEntity, true, false, true);
        return N4JUtil.getInstance().numistaService.nTypeService.setIssuedBy(nTypeUuid, issuingEntityUuid);

    }

    private static boolean loadDenomination(Document page, UUID nTypeUuid, String nid, UUID currencyUuid) {
        HashMap<String, String> denomination = getAttributeWithTextSingleOption(page.selectFirst("#denomination"), "value");
        DebugUtil.printProperty("value text", "denomination", false, false, false);
        if (denomination != null) {
            DebugUtil.printProperty("value denomination id", denomination.get("value"), true, true, false);
            DebugUtil.printProperty("value denomination name", denomination.get("text"), true, true, false);
        }
        if (denomination != null && isValueAndTextNotNullAndNotEmpty(denomination)) {
            String denominationNid = denomination.get("value");
            UUID denominationUuid = null;
//            UUID denominationUuid = N4JUtil.getInstance().numistaService.denominationService.findDenominationUuidByNid(denominationNid);

            if (denominationUuid == null) {

                String currencyNid = N4JUtil.getInstance().numistaService.currencyService.findCurrencyNidByUuid(currencyUuid);

                if (currencyNid == null) {
                    DebugUtil.showError(NumistaEditPageUtil.class, "Can't find Currency's Nid by Currency's Eid while loading Denomination for Ntype with nid : " + nid);
                    return false;
                }

                Document denominationsPHPDocument = getNumistaDenominationsByPHP(currencyNid);
                if (denominationsPHPDocument != null) {
                    Elements optgroups = denominationsPHPDocument.select("optgroup");

                    if (!optgroups.isEmpty()) {  //need to understand what to do with OPTGROUP in IssuingEntities
                        DebugUtil.showError(NumistaEditPageUtil.class, "Find OPTGROUP while parsing Denominations from PHP request with Currency's Nid: " + currencyNid);
                    }


                    Elements options = denominationsPHPDocument.select("option");
                    for (Element element : options) {
                        String denNid = element.attributes().get("value");
                        String denFullName = element.text();

//                        UUID denUuid = N4JUtil.getInstance().numistaService.denominationService.findDenominationUuidByNid(denNid);
                        UUID denUuid = null;
                        if (denUuid == null) {
                            Denomination den = new Denomination();
                            den.setNid(denNid);
                            den.setFullName(denFullName);
                            String denName = denFullName.substring(0, denFullName.indexOf('(') - 1);
                            den.setName(denName);
                            String denNumericValueStr = denFullName.substring(denFullName.indexOf('(') + 1, denFullName.indexOf(')'));

                            Float denNumericValue = null;
                            try {
                                denNumericValue = Float.valueOf(denNumericValueStr);
                            } catch (NumberFormatException e) {
                                DebugUtil.showError(NumistaEditPageUtil.class, "Can't parse Denomination numericValue from " + denFullName + " while parsing Denominations from PHP request with Currency's Nid: " + currencyNid);
                            }
                            den.setNumericValue(denNumericValue);

                            denUuid = N4JUtil.getInstance().numistaService.denominationService.save(den).getUuid();
                            if (!N4JUtil.getInstance().numistaService.currencyService.setHasDenomination(currencyUuid, denUuid)) {  //Every Denomination mast to connect to Currency
                                DebugUtil.showError(NumistaEditPageUtil.class, "Some error while creating relationship between CURRENCY and DENOMINATION while parsing page with nid: " + nid);
                                return false;
                            }
                        } else { //need to check a connection to the Issuer
                            if (!N4JUtil.getInstance().numistaService.currencyService.hasSingleRelationshipToNode(currencyUuid, denUuid)) {
                                N4JUtil.getInstance().numistaService.currencyService.setHasDenomination(currencyUuid, denUuid);
                            }
                        }
                    }
                }

//                denominationUuid = N4JUtil.getInstance().numistaService.denominationService.findDenominationUuidByNid(denominationNid);
                denominationUuid = null;
            }

            if (denominationUuid == null) {
                DebugUtil.showError(NumistaEditPageUtil.class, "Find a Denomination's Nid but can't find Denomination in database or create new one while parsing page with nid: " + nid);
                return false;
            }

            return N4JUtil.getInstance().numistaService.nTypeService.setDenominatedIn(nTypeUuid, denominationUuid);

        }


        return false;
    }

    private static void loadNumericValue(Document page, NType nType) {
        String valeurChiffres = getAttribute(page.selectFirst("#valeur_chiffres"), "value");
        DebugUtil.printProperty("value numeric_value", valeurChiffres, true, true, false);
        if (valeurChiffres != null && !valeurChiffres.isEmpty()) {
            nType.setValueNumeric(valeurChiffres);
        }
    }

    private static UUID loadCurrency(Document page, UUID nTypeUuid, String nid, UUID issuerUuid) {
        HashMap<String, String> devise = getAttributeWithTextSingleOption(page.selectFirst("#devise"), "value");
        if (devise != null) {
            DebugUtil.printProperty("value currency id", devise.get("value"), true, true, false);
            DebugUtil.printProperty("value currency full_name", devise.get("text"), true, true, false);
        }

        if (devise != null && isValueAndTextNotNullAndNotEmpty(devise)) {

            String currencyNid = devise.get("value");
//            UUID currencyUuid = N4JUtil.getInstance().numistaService.currencyService.findCurrencyUuidByNid(currencyNid);
            UUID currencyUuid = null;

            if (currencyUuid == null) {

                String issuerCode = N4JUtil.getInstance().numistaService.issuerService.findIssuerCodeByEid(issuerUuid);

                if (issuerCode == null) {
                    DebugUtil.showError(NumistaEditPageUtil.class, "Can't find Issuer's Code by Issuer's Eid while loading Currency for Ntype with nid : " + nid);
                    return null;
                }

                Document currenciesPHPDocument = getNumistaCurrenciesByPHP(issuerCode);
                if (currenciesPHPDocument != null) {
                    Elements optgroups = currenciesPHPDocument.select("optgroup");

                    if (!optgroups.isEmpty()) {  //need to understand what to do with OPTGROUP in Currencies
                        DebugUtil.showError(NumistaEditPageUtil.class, "Find OPTGROUP while parsing Currencies from PHP request with Issuer's Code: " + issuerCode);
                    }


                    Elements options = currenciesPHPDocument.select("option");
                    for (Element element : options) {
                        String curNid = element.attributes().get("value");
                        String curFullName = element.text();

//                        UUID curUuid = N4JUtil.getInstance().numistaService.currencyService.findCurrencyUuidByNid(curNid);
                        UUID curUuid = null;
                        if (curUuid == null) {
                            Currency cur = new Currency();
                            cur.setNid(curNid);
                            cur.setFullName(curFullName);

//                            String curName = curFullName.substring(curFullName.indexOf(''), denFullName.indexOf('(') - 1);
//                            den.setName(denName);
//                            String denNumericValueStr = denFullName.substring(denFullName.indexOf('(') + 1, denFullName.indexOf(')'));
//
//                            Float denNumericValue = null;
//                            try {
//                                denNumericValue = Float.valueOf(denNumericValueStr);
//                            } catch (NumberFormatException e) {
//                                DebugUtil.showError(NumistaEditPageUtil.class, "Can't parse Denomination numericValue from " + denFullName + " while parsing Denominations from PHP request with Currency's Nid: " + currencyNid);
//                            }
//                            den.setNumericValue(denNumericValue);
//
//                            denUuid = N4JUtil.getInstance().numistaService.denominationService.save(den).getEid();
//                            if (!N4JUtil.getInstance().numistaService.currencyService.setHasDenomination(currencyUuid, denUuid)) {  //Every Denomination mast to connect to Currency
//                                DebugUtil.showError(NumistaEditPageUtil.class, "Some error while creating relationship between CURRENCY and DENOMINATION while parsing page with nid: " + nid);
//                                return false;
//                            }
                        } else { //need to check a connection to the Issuer
//                            if (!N4JUtil.getInstance().numistaService.currencyService.hasSingleRelationshipToNode(currencyUuid, denUuid)) {
//                                N4JUtil.getInstance().numistaService.currencyService.setHasDenomination(currencyUuid, denUuid);
//                            }
                        }
                    }
                }

//                denominationUuid = N4JUtil.getInstance().numistaService.denominationService.findUuidByNid(denominationNid);
            }

//            if(denominationUuid == null){
//                DebugUtil.showError(NumistaEditPageUtil.class, "Find a Denomination's Nid but can't find Denomination in database or create new one while parsing page with nid: " + nid);
//                return false;
//            }

//            return N4JUtil.getInstance().numistaService.nTypeService.setDenominatedIn(nTypeUuid, denominationUuid);


//            nType.setCurrency(N4JUtil.getInstance().numistaService.currencyService.update(nType.getCurrency(), devise.get("value"), devise.get("text")));
        }

        return null;
    }

    private static void loadType(Document page, NType nType) {

        if (nType.getCategory().getName().equals(Category.COIN) || nType.getCategory().getName().equals(Category.BANKNOTE)) {
            HashMap<String, String> coinType = getAttributeWithTextSelectedOption(page.selectFirst("#coin_type"));
            DebugUtil.printProperty("type value", coinType.get("value"), true, true, false);
            DebugUtil.printProperty("type text", coinType.get("text"), true, true, false);

            if (isValueAndTextNotNullAndNotEmpty(coinType)) {
                nType.setCollectibleType(N4JUtil.getInstance().numistaService.collectibleTypeService.update(nType.getCollectibleType(), coinType.get("value"), coinType.get("text"), null));
            }
        } else if (nType.getCategory().getName().equals(Category.EXONUMIA)) {
            Element exonumiaCoinType = page.selectFirst("#coin_type");
            if (exonumiaCoinType != null) {
                Elements optgroups = exonumiaCoinType.select("optgroup");

                for (Element optgroup : optgroups) {
                    HashMap<String, String> exonumiaType = getAttributeWithTextSelectedOption(optgroup);
                    if (exonumiaType != null) {
                        DebugUtil.printProperty("coin_type optgroup", optgroup.attributes().get("label"), false, false, true);
                        DebugUtil.printProperty("coin_type value", exonumiaType.get("value"), false, false, true);
                        DebugUtil.printProperty("coin_type text", exonumiaType.get("text"), false, false, true);

                        if (isValueAndTextNotNullAndNotEmpty(exonumiaType) && !optgroup.attributes().get("label").isEmpty()) {
                            CollectibleTypeGroup typeGroup = N4JUtil.getInstance().numistaService.typeGroupService.findByName(optgroup.attributes().get("label"));
//                            nType.setCollectibleType(N4JUtil.getInstance().numistaService.collectibleTypeService.update(nType.getCollectibleType(), exonumiaType.get("value"), exonumiaType.get("text"), typeGroup));
                        }
                        break;
                    }
                }
            }
        }
    }

    private static void loadCommemoratedEvent(Document page, NType nType) {
        String evenement = getAttribute(page.selectFirst("#evenement"), "value");
        DebugUtil.printProperty("commemorated_topic", evenement, true, false, true);
        if (evenement != null && !evenement.isEmpty()) {
            nType.setCommemoratedEvent(N4JUtil.getInstance().numistaService.commemoratedEventService.update(nType.getCommemoratedEvent(), evenement));
        }
    }

    private static void loadSeries(Document page, NType nType) {
        String series = getTagText(page.selectFirst("#series"));
        DebugUtil.printProperty("series", series, true, false, false);
        if (series != null && !series.isEmpty()) {
            nType.setSeries(N4JUtil.getInstance().numistaService.seriesService.update(nType.getSeries(), series));
        }
    }

    private static void loadDemonetized(Document page, NType nType) {
        Elements demonetisationElements = page.select("input[name=demonetisation]");
        for (Element demonetisationElement : demonetisationElements) {
            if (demonetisationElement.attributes().hasKey("checked")) {
                String demonetized = demonetisationElement.attributes().get("value");
                DebugUtil.printProperty("demonetization is_demonetized", demonetized, true, true, true);
                if (!demonetized.isEmpty()) {
//                    switch (demonetized) {
//                        case "oui":
//                            nType.setIsDemonetized(true);
//                            break;
//                        case "non":
//                            nType.setIsDemonetized(false);
//                            break;
//                        case "inconnu":
//                            nType.setIsDemonetized(null);
//                            break;
//                    }
                }

                if (demonetized.equals("oui")) {
                    //pattern="[0-9]+"
                    String demonetisationYear = getAttribute(page.selectFirst("#ad"), "value");
                    DebugUtil.printProperty("demonetization year", demonetisationYear, true, true, false);
                    if (demonetisationYear != null && !demonetisationYear.isEmpty()) {
                        nType.setDemonetizationYear(demonetisationYear);
                    }

                    //pattern="[0-9]{1,2}"
                    String demonetisationMonth = getAttribute(page.selectFirst("#md"), "value");
                    DebugUtil.printProperty("demonetization month", demonetisationMonth, true, true, false);
                    if (demonetisationMonth != null && !demonetisationMonth.isEmpty()) {
                        nType.setDemonetizationMonth(demonetisationMonth);
                    }

                    //pattern="[0-9]{1,2}"
                    String demonetisationDay = getAttribute(page.selectFirst("#jd"), "value");
                    DebugUtil.printProperty("demonetization day", demonetisationDay, true, true, false);
                    if (demonetisationDay != null && !demonetisationDay.isEmpty()) {
                        nType.setDemonetizationDay(demonetisationDay);
                    }
                }
            }
        }
    }


    private static void loadReferences(Document page, NType nType) {
        nType.getCatalogueReferences().clear();

        Elements referenceElements = page.select("div[class=reference_input]:not([style=display:none])");
        for (Element referenceElement : referenceElements) {
            HashMap<String, String> reference = getAttributeWithTextSingleOption(referenceElement, "value");
            DebugUtil.printProperty("references[] catalogue id", reference.get("value"), true, true, false);
            DebugUtil.printProperty("references[] catalogue code", reference.get("text"), true, true, false);

            String inputValue = getAttribute(referenceElement.selectFirst("input"), "value");
            DebugUtil.printProperty("references[] number", inputValue, true, true, false);

            if (inputValue != null && !inputValue.isEmpty()) {
                if (isValueAndTextNotNullAndNotEmpty(reference)) {
                    Catalogue catalogue = N4JUtil.getInstance().numistaService.catalogueService.findByNid(reference.get("value"), reference.get("text"));
                    if (catalogue != null) {
                        nType.getCatalogueReferences().add(N4JUtil.getInstance().numistaService.catalogueReferenceService.findByNumberAndCatalogueNid(inputValue, catalogue));
                    }
                }
            }
        }
    }

    private static void loadComposition(Document page, NType nType) {
        HashMap<String, String> metalType = getAttributeWithTextSelectedOption(page.selectFirst("#metal_type"));
        if (metalType != null) {
            DebugUtil.printProperty("composition type code", metalType.get("value"), true, false, true);
            DebugUtil.printProperty("composition type name", metalType.get("text"), true, false, true);

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

                DebugUtil.printProperty("composition metal code", metal.get("metalCode"), true, false, true);
                DebugUtil.printProperty("composition metal name", metal.get("metalName"), true, false, true);
                DebugUtil.printProperty("composition metal fineness", metal.get("fineness"), false, false, true);

                setMetal1(nType.getComposition(), metal, null);

            } else if (metalType.get("value").equals("plated")) {
                //Core
                HashMap<String, String> coreMetal = parseCompositionMetal("#metal1", "#fineness1", page);

                DebugUtil.printProperty("composition core metal code", coreMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition core metal name", coreMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition core metal fineness", coreMetal.get("fineness"), false, false, false);

                setMetal1(nType.getComposition(), coreMetal, CompositionMetalType.core);

                //Plating
                HashMap<String, String> platingMetal = parseCompositionMetal("#metal2", "#fineness2", page);

                DebugUtil.printProperty("composition plating metal code", platingMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition plating metal name", platingMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition plating metal fineness", platingMetal.get("fineness"), false, false, false);

                setMetal2(nType.getComposition(), platingMetal, CompositionMetalType.plating);

            } else if (metalType.get("value").equals("clad")) {

                //Core
                HashMap<String, String> coreMetal = parseCompositionMetal("#metal1", "#fineness1", page);

                DebugUtil.printProperty("composition core metal code", coreMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition core metal name", coreMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition core metal fineness", coreMetal.get("fineness"), false, false, false);

                setMetal1(nType.getComposition(), coreMetal, CompositionMetalType.core);

                //Clad
                HashMap<String, String> cladMetal = parseCompositionMetal("#metal2", "#fineness2", page);

                DebugUtil.printProperty("composition clad metal code", cladMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition clad metal name", cladMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition clad metal fineness", cladMetal.get("fineness"), false, false, false);

                setMetal2(nType.getComposition(), cladMetal, CompositionMetalType.clad);

            } else if (metalType.get("value").equals("bimetallic")) {

                //Center
                HashMap<String, String> centerMetal = parseCompositionMetal("#metal1", "#fineness1", page);

                DebugUtil.printProperty("composition center metal code", centerMetal.get("metalCode"), true, false, false);
                DebugUtil.printProperty("composition center metal name", centerMetal.get("metalName"), true, false, false);
                DebugUtil.printProperty("composition center metal fineness", centerMetal.get("fineness"), true, false, false);

                setMetal1(nType.getComposition(), centerMetal, CompositionMetalType.center);

                //Ring
                HashMap<String, String> ringMetal = parseCompositionMetal("#metal2", "#fineness2", page);

                DebugUtil.printProperty("composition ring metal code", ringMetal.get("metalCode"), true, false, false);
                DebugUtil.printProperty("composition ring metal name", ringMetal.get("metalName"), true, false, false);
                DebugUtil.printProperty("composition ring metal fineness", ringMetal.get("fineness"), false, false, false);

                setMetal2(nType.getComposition(), ringMetal, CompositionMetalType.ring);

            } else if (metalType.get("value").equals("bimetallic_plated")) {

                //Center Core
                HashMap<String, String> centerCoreMetal = parseCompositionMetal("#metal1", "#fineness1", page);

                DebugUtil.printProperty("composition center_core metal code", centerCoreMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition center_core metal name", centerCoreMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition center_core metal fineness", centerCoreMetal.get("fineness"), false, false, false);

                setMetal1(nType.getComposition(), centerCoreMetal, CompositionMetalType.center_core);

                //Center Plating
                HashMap<String, String> centerPlatingMetal = parseCompositionMetal("#metal2", "#fineness2", page);

                DebugUtil.printProperty("composition center plating metal code", centerPlatingMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition center plating metal name", centerPlatingMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition center plating metal fineness", centerPlatingMetal.get("fineness"), false, false, false);

                setMetal2(nType.getComposition(), centerPlatingMetal, CompositionMetalType.center_plating);

                //Ring
                HashMap<String, String> ringMetal = parseCompositionMetal("#metal3", "#fineness3", page);

                DebugUtil.printProperty("composition ring metal code", ringMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition ring metal name", ringMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition ring metal fineness", ringMetal.get("fineness"), false, false, false);

                setMetal3(nType.getComposition(), ringMetal, CompositionMetalType.ring);

            } else if (metalType.get("value").equals("bimetallic_plated_ring")) {

                //Center
                HashMap<String, String> centerMetal = parseCompositionMetal("#metal1", "#fineness1", page);

                DebugUtil.printProperty("composition center metal code", centerMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition center metal name", centerMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition center metal fineness", centerMetal.get("fineness"), false, false, false);

                setMetal1(nType.getComposition(), centerMetal, CompositionMetalType.center);

                //Ring core
                HashMap<String, String> ringCoreMetal = parseCompositionMetal("#metal2", "#fineness2", page);

                DebugUtil.printProperty("composition ring_core metal code", ringCoreMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition ring_core metal name", ringCoreMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition ring_core metal fineness", ringCoreMetal.get("fineness"), false, false, false);

                setMetal2(nType.getComposition(), ringCoreMetal, CompositionMetalType.ring_core);

                //Ring plating
                HashMap<String, String> ringPlatingMetal = parseCompositionMetal("#metal3", "#fineness3", page);

                DebugUtil.printProperty("composition ring plating metal code", ringPlatingMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition ring plating metal name", ringPlatingMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition ring plating metal fineness", ringPlatingMetal.get("fineness"), false, false, false);

                setMetal3(nType.getComposition(), ringPlatingMetal, CompositionMetalType.ring_plating);

            } else if (metalType.get("value").equals("bimetallic_plated_plated")) {

                //Center Core
                HashMap<String, String> centerCoreMetal = parseCompositionMetal("#metal1", "#fineness1", page);

                DebugUtil.printProperty("composition center_core metal code", centerCoreMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition center_core metal name", centerCoreMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition center_core metal fineness", centerCoreMetal.get("fineness"), false, false, false);

                setMetal1(nType.getComposition(), centerCoreMetal, CompositionMetalType.center_core);

                //Center Plating
                HashMap<String, String> centerPlatingMetal = parseCompositionMetal("#metal2", "#fineness2", page);

                DebugUtil.printProperty("composition center plating metal code", centerPlatingMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition center plating metal name", centerPlatingMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition center plating metal fineness", centerPlatingMetal.get("fineness"), false, false, false);

                setMetal2(nType.getComposition(), centerPlatingMetal, CompositionMetalType.center_plating);

                //Ring core
                HashMap<String, String> ringCoreMetal = parseCompositionMetal("#metal3", "#fineness3", page);

                DebugUtil.printProperty("composition ring_core metal code", ringCoreMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition ring_core metal name", ringCoreMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition ring_core metal fineness", ringCoreMetal.get("fineness"), false, false, false);

                setMetal3(nType.getComposition(), ringCoreMetal, CompositionMetalType.ring_core);

                //Ring plating
                HashMap<String, String> ringPlatingMetal = parseCompositionMetal("#metal4", "#fineness4", page);

                DebugUtil.printProperty("composition ring plating metal code", ringPlatingMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition ring plating metal name", ringPlatingMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition ring plating metal fineness", ringPlatingMetal.get("fineness"), false, false, false);

                setMetal4(nType.getComposition(), ringPlatingMetal, CompositionMetalType.ring_plating);

            } else if (metalType.get("value").equals("bimetallic_clad")) {
                //Center Core
                HashMap<String, String> centerCoreMetal = parseCompositionMetal("#metal1", "#fineness1", page);

                DebugUtil.printProperty("composition center_core metal code", centerCoreMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition center_core metal name", centerCoreMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition center_core metal fineness", centerCoreMetal.get("fineness"), false, false, false);

                setMetal1(nType.getComposition(), centerCoreMetal, CompositionMetalType.center_core);

                //Center Clad
                HashMap<String, String> centerCladMetal = parseCompositionMetal("#metal2", "#fineness2", page);

                DebugUtil.printProperty("composition center clad metal code", centerCladMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition center clad metal name", centerCladMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition center clad metal fineness", centerCladMetal.get("fineness"), false, false, false);

                setMetal2(nType.getComposition(), centerCladMetal, CompositionMetalType.center_clad);

                //Ring
                HashMap<String, String> ringMetal = parseCompositionMetal("#metal3", "#fineness3", page);

                DebugUtil.printProperty("composition ring metal code", ringMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition ring metal name", ringMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition ring metal fineness", ringMetal.get("fineness"), false, false, false);

                setMetal3(nType.getComposition(), ringMetal, CompositionMetalType.ring);

            } else if (metalType.get("value").equals("trimetallic")) {

                //Center
                HashMap<String, String> centerMetal = parseCompositionMetal("#metal1", "#fineness1", page);

                DebugUtil.printProperty("composition center code", centerMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition center name", centerMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition center fineness", centerMetal.get("fineness"), false, false, false);

                setMetal1(nType.getComposition(), centerMetal, CompositionMetalType.center);

                //Middle ring
                HashMap<String, String> middleRingMetal = parseCompositionMetal("#metal2", "#fineness2", page);

                DebugUtil.printProperty("composition middle_ring code", middleRingMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition middle_ring name", middleRingMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition middle_ring fineness", middleRingMetal.get("fineness"), false, false, false);

                setMetal2(nType.getComposition(), middleRingMetal, CompositionMetalType.middle_ring);

                //Outer ring
                HashMap<String, String> outerRingMetal = parseCompositionMetal("#metal2", "#fineness2", page);

                DebugUtil.printProperty("composition outer_ring code", outerRingMetal.get("metalCode"), false, false, false);
                DebugUtil.printProperty("composition outer_ring name", outerRingMetal.get("metalName"), false, false, false);
                DebugUtil.printProperty("composition outer_ring fineness", outerRingMetal.get("fineness"), false, false, false);

                setMetal3(nType.getComposition(), outerRingMetal, CompositionMetalType.outer_ring);
            }

            N4JUtil.getInstance().numistaService.compositionService.save(nType.getComposition());
        }

        //Metal Additional details
        String metalDetails = getAttribute(page.selectFirst("#metal_details"), "value");
        DebugUtil.printProperty("composition metal details", metalDetails, false, false, false);
        if (metalDetails != null && !metalDetails.isEmpty()) {
            nType.getComposition().setCompositionAdditionalDetails(metalDetails);
        }


        //Banknote Composition
        //<option value="68">Hybrid substrate</option>
        // <option value="73">Other</option>
        // <option value="66">Paper</option>
        // <option value="67">Polymer</option>
        // <option value="69">Silk</option>

        if (nType.getCategory().getName().equals("banknote")) {
            HashMap<String, String> compositionHashMap = getAttributeWithTextSelectedOption(page.selectFirst("#metal1"));
            if (compositionHashMap != null) {
                DebugUtil.printProperty("composition value", compositionHashMap.get("value"), false, true, false);
                DebugUtil.printProperty("composition name", compositionHashMap.get("text"), false, true, false);

                if (isValueAndTextNotNullAndNotEmpty(compositionHashMap)) {
                    if (nType.getComposition() == null) {
                        nType.setComposition(new Composition());
                    }
                    setMetal1(nType.getComposition(), compositionHashMap, null);
                    N4JUtil.getInstance().numistaService.compositionService.save(nType.getComposition());
                }
            }
        }
    }


    private static void loadShape(Document page, NType nType) {
        //coins
        //<option value="">Unknown</option> <option value="51">Annular sector</option> <option value="49">Cob</option> <option value="10">Decagonal (10-sided)</option> <option value="12">Dodecagonal (12-sided)</option> <option value="47">Equilateral curve heptagon (7-sided)</option> <option value="62">Half circle</option> <option value="53">Heart</option> <option value="11">Hendecagonal (11-sided)</option> <option value="7">Heptagonal (7-sided)</option> <option value="57">Hexadecagonal (16-sided)</option> <option value="6">Hexagonal (6-sided)</option> <option value="58">Icosagonal (20-sided)</option> <option value="59">Icosidigonal (22-sided)</option> <option value="66">Icosihenagonal (21-sided)</option> <option value="65">Icosipentagonal (25-sided)</option> <option value="56">Icositetragonal (24-sided)</option> <option value="45">Irregular</option> <option value="42">Klippe</option> <option value="72">Knife</option> <option value="9">Nonagonal (9-sided)</option> <option value="8">Octagonal (8-sided)</option> <option value="48">Octagonal (8-sided) with a hole</option> <option value="68">Octodecagonal (18-sided)</option> <option value="50">Other</option> <option value="36">Oval</option> <option value="37">Oval with a loop</option> <option value="5">Pentagonal (5-sided)</option> <option value="63">Quarter circle</option> <option value="4">Rectangular</option> <option value="46">Rectangular (irregular)</option> <option value="43">Reuleaux triangle</option> <option value="64">Rhombus</option> <option value="1" selected>Round</option> <option value="2">Round (irregular)</option> <option value="34">Round with 4 pinches</option> <option value="33">Round with a loop</option> <option value="31">Round with a round hole</option> <option value="32">Round with a square hole</option> <option value="38">Round with cutouts</option> <option value="39">Round with groove(s)</option> <option value="15">Scalloped</option> <option value="20">Scalloped (with 10 notches)</option> <option value="21">Scalloped (with 11 notches)</option> <option value="22">Scalloped (with 12 notches)</option> <option value="23">Scalloped (with 13 notches)</option> <option value="24">Scalloped (with 14 notches)</option> <option value="26">Scalloped (with 16 notches)</option> <option value="27">Scalloped (with 17 notches)</option> <option value="29">Scalloped (with 20 notches)</option> <option value="14">Scalloped (with 4 notches)</option> <option value="18">Scalloped (with 8 notches)</option> <option value="30">Scalloped with a hole</option> <option value="35">Scyphate</option> <option value="54">Spade</option> <option value="73">Spanish flower</option> <option value="44">Square</option> <option value="41">Square (irregular)</option> <option value="55">Square with angled corners</option> <option value="40">Square with rounded corners</option> <option value="71">Square with scalloped edges</option> <option value="74">Tetradecagonal (14-sided)</option> <option value="3">Triangular</option> <option value="13">Tridecagonal (13-sided)</option>

        //banknotes
        // <option value="">Unknown</option> <option value="150">Other</option> <option value="100" selected>Rectangular</option> <option value="102">Rectangular (hand cut)</option> <option value="101">Rectangular with undulating edge</option> <option value="103">Round</option> <option value="105">Square</option> <option value="104">Triangular</option>

        HashMap<String, String> shape = getAttributeWithTextSelectedOption(page.selectFirst("#shape"));
        if (shape != null) {
            DebugUtil.printProperty("shape code", shape.get("value"), true, true, true);
            DebugUtil.printProperty("shape name", shape.get("text"), true, true, true);
        }
        if (shape != null && isValueAndTextNotNullAndNotEmpty(shape)) {
            nType.setShape(N4JUtil.getInstance().numistaService.shapeService.update(nType.getShape(), shape.get("value"), shape.get("text")));
        }

        //Shape Additional details
        String shapeDetails = getAttribute(page.selectFirst("#shape_details"), "value");
        DebugUtil.printProperty("shape details", shapeDetails, false, false, false);
        if (shapeDetails != null && !shapeDetails.isEmpty()) {
            nType.setShapeAdditionalDetails(shapeDetails);
        }

    }


    private static void loadWeight(Document page, NType nType) {
        String poids = getAttribute(page.selectFirst("#poids"), "value");
        DebugUtil.printProperty("weight", poids, true, false, true);
        if (poids != null && !poids.isEmpty()) {
            nType.setWeight(poids);
        }
    }

    private static void loadSize(Document page, NType nType) {
        String dimension = getAttribute(page.selectFirst("#dimension"), "value");
        DebugUtil.printProperty("size", dimension, true, true, true);
        if (dimension != null && !dimension.isEmpty()) {
            nType.setSize(dimension);
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
                DebugUtil.printProperty("dimension2", dimension2, false, true, false);
                if (dimension2 != null && !dimension2.isEmpty()) {
                    nType.setSize2(dimension2);
                }
            }
        }
    }

    private static void loadThickness(Document page, NType nType) {
        String epaisseur = getAttribute(page.selectFirst("#epaisseur"), "value");
        DebugUtil.printProperty("thickness", epaisseur, true, false, true);
        if (epaisseur != null && !epaisseur.isEmpty()) {
            nType.setThickness(epaisseur);
        }
    }

    private static void loadTechniques(Document page, NType nType) {
        nType.getTechniques().clear();

        List<HashMap<String, String>> techniques = getAttributesWithTextSelectedOptions(page.selectFirst("#techniques"));
        if (techniques != null) {
            for (HashMap<String, String> technique : techniques) {
                DebugUtil.printProperty("technique[] code", technique.get("value"), true, false, true);
                DebugUtil.printProperty("technique[] name", technique.get("text"), true, false, true);
                if (isValueAndTextNotNullAndNotEmpty(technique)) {
                    nType.getTechniques().add(N4JUtil.getInstance().numistaService.techniqueService.findByNid(technique.get("value"), technique.get("text")));
                }
            }
        }

        // Technique Additional details
        String techniqueDetail = getAttribute(page.selectFirst("#technique_details"), "value");
        DebugUtil.printProperty("technique[] details", techniqueDetail, false, false, false);
        if (techniqueDetail != null && !techniqueDetail.isEmpty()) {
            nType.setTechniqueAdditionalDetails(techniqueDetail);
        }
    }

    private static void loadAlignment(Document page, NType nType) {
        String alignementCode = getAttribute(page.selectFirst("input[name=alignement][checked=checked]"), "value");
        DebugUtil.printProperty("orientation code", alignementCode, true, false, true);
        if (alignementCode != null && !alignementCode.isEmpty()) {
            nType.setAlignment(alignementCode);
        }
    }


    private static void loadObverseEngravers(Document page, NType nType) {
        List<String> graveursAvers = getTextsSelectedOptions(page.selectFirst("#graveur_avers"));
        if (graveursAvers != null) {
            for (String engraver : graveursAvers) {
                DebugUtil.printProperty("obverse engravers[]", engraver, true, true, false);
                if (engraver != null && !engraver.isEmpty() && !nType.getObverse().getEngravers().contains(engraver)) {
                    nType.getObverse().getEngravers().add(engraver);
                }
            }
        }
    }

    private static void loadObverseDesigners(Document page, NType nType) {
        List<String> designersAvers = getTextsSelectedOptions(page.selectFirst("#designer_avers"));
        if (designersAvers != null) {
            for (String designer : designersAvers) {
                DebugUtil.printProperty("obverse designers[]", designer, true, false, false);
                if (designer != null && !designer.isEmpty() && !nType.getObverse().getDesigners().contains(designer)) {
                    nType.getObverse().getDesigners().add(designer);
                }
            }
        }
    }


    private static void loadObverseDescription(Document page, NType nType) {
        String descriptionAvers = getTagText(page.selectFirst("#description_avers"));
        DebugUtil.printProperty("obverse description", descriptionAvers, true, true, true);
        if (descriptionAvers != null && !descriptionAvers.isEmpty()) {
            nType.getObverse().setDescription(descriptionAvers);
        }
    }

    private static void loadObverseLettering(Document page, NType nType) {
        String texteAvers = getTagText(page.selectFirst("#texte_avers"));
        DebugUtil.printProperty("obverse lettering", texteAvers, true, true, true);
        if (texteAvers != null && !texteAvers.isEmpty()) {
            nType.getObverse().setLettering(texteAvers);
        }
    }

    private static void loadObverseScripts(Document page, NType nType) {
        nType.getObverse().getLetteringScripts().clear();

        List<HashMap<String, String>> scriptsAvers = getAttributesWithTextSelectedOptions(page.selectFirst("#script_avers"));
        if (scriptsAvers != null) {
            for (HashMap<String, String> scriptAvers : scriptsAvers) {
                DebugUtil.printProperty("obverse lettering_scripts[] code", scriptAvers.get("value"), true, false, true);
                DebugUtil.printProperty("obverse lettering_scripts[] name", scriptAvers.get("text"), true, false, true);
                if (isValueAndTextNotNullAndNotEmpty(scriptAvers)) {
                    nType.getObverse().getLetteringScripts().add(N4JUtil.getInstance().numistaService.letteringScriptService.findByNid(scriptAvers.get("value"), scriptAvers.get("text")));
                }
            }
        }
    }

    private static void loadObverseUnabridgedLegend(Document page, NType nType) {
        String unabridgedAvers = getTagText(page.selectFirst("#unabridged_avers"));
        DebugUtil.printProperty("obverse unabridged_legend", unabridgedAvers, false, false, false);
        if (unabridgedAvers != null && !unabridgedAvers.isEmpty()) {
            nType.getObverse().setUnabridgedLegend(unabridgedAvers);
        }
    }

    private static void loadObverseLetteringTranslation(Document page, NType nType) {
        String traductionAvers = getTagText(page.selectFirst("#traduction_avers"));
        DebugUtil.printProperty("obverse lettering_translation", traductionAvers, true, true, false);
        if (traductionAvers != null && !traductionAvers.isEmpty()) {
            nType.getObverse().setLetteringTranslation(traductionAvers);
        }
    }

    private static void loadObversePicture(Document page, NType nType) {
        Element obverse = page.selectFirst("fieldset:contains(Obverse)");
        if (obverse != null) {
            String obversePhoto = getAttribute(obverse.selectFirst("a[target=_blank]"), "href");
            DebugUtil.printProperty("obverse picture", obversePhoto, true, true, true);
            if (obversePhoto != null && !obversePhoto.isEmpty()) {
                nType.getObverse().setPicture(obversePhoto);
            }
        }
    }

    private static void loadReverseEngravers(Document page, NType nType) {
        List<String> graveursRevers = getTextsSelectedOptions(page.selectFirst("#graveur_revers"));
        if (graveursRevers != null) {
            for (String engraver : graveursRevers) {
                DebugUtil.printProperty("reverse engravers[]", engraver, true, true, false);
                if (engraver != null && !engraver.isEmpty() && !nType.getReverse().getEngravers().contains(engraver)) {
                    nType.getReverse().getEngravers().add(engraver);
                }
            }
        }
    }

    private static void loadReverseDesigners(Document page, NType nType) {
        List<String> designersRevers = getTextsSelectedOptions(page.selectFirst("#designer_revers"));
        if (designersRevers != null) {
            for (String designer : designersRevers) {
                DebugUtil.printProperty("reverse designers[]", designer, true, false, false);
                if (designer != null && !designer.isEmpty() && !nType.getReverse().getDesigners().contains(designer)) {
                    nType.getReverse().getDesigners().add(designer);
                }
            }
        }
    }


    private static void loadReverseDescription(Document page, NType nType) {
        String descriptionRevers = getTagText(page.selectFirst("#description_revers"));
        DebugUtil.printProperty("reverse description", descriptionRevers, true, true, true);
        if (descriptionRevers != null && !descriptionRevers.isEmpty()) {
            nType.getReverse().setDescription(descriptionRevers);
        }
    }

    private static void loadReverseLettering(Document page, NType nType) {
        String texteRevers = getTagText(page.selectFirst("#texte_revers"));
        DebugUtil.printProperty("reverse lettering", texteRevers, true, true, true);
        if (texteRevers != null && !texteRevers.isEmpty()) {
            nType.getReverse().setLettering(texteRevers);
        }
    }

    private static void loadReverseScripts(Document page, NType nType) {
        nType.getReverse().getLetteringScripts().clear();

        List<HashMap<String, String>> scriptsRevers = getAttributesWithTextSelectedOptions(page.selectFirst("#script_revers"));
        if (scriptsRevers != null) {
            for (HashMap<String, String> scriptRevers : scriptsRevers) {
                DebugUtil.printProperty("reverse lettering_scripts[] code", scriptRevers.get("value"), true, false, true);
                DebugUtil.printProperty("reverse lettering_scripts[] name", scriptRevers.get("text"), true, false, true);

                if (isValueAndTextNotNullAndNotEmpty(scriptRevers)) {
                    nType.getReverse().getLetteringScripts().add(N4JUtil.getInstance().numistaService.letteringScriptService.findByNid(scriptRevers.get("value"), scriptRevers.get("text")));
                }
            }
        }
    }

    private static void loadReverseUnabridgedLegend(Document page, NType nType) {
        String unabridgedRevers = getTagText(page.selectFirst("#unabridged_revers"));
        DebugUtil.printProperty("reverse unabridged_legend", unabridgedRevers, false, false, false);
        if (unabridgedRevers != null && !unabridgedRevers.isEmpty()) {
            nType.getReverse().setUnabridgedLegend(unabridgedRevers);
        }
    }

    private static void loadReverseLetteringTranslation(Document page, NType nType) {
        String traductionRevers = getTagText(page.selectFirst("#traduction_revers"));
        DebugUtil.printProperty("reverse lettering_translation", traductionRevers, true, true, false);
        if (traductionRevers != null && !traductionRevers.isEmpty()) {
            nType.getReverse().setLetteringTranslation(traductionRevers);
        }
    }

    private static void loadReversePicture(Document page, NType nType) {
        Element reverse = page.selectFirst("fieldset:contains(Reverse (back))");
        if (reverse != null) {
            String reversePhoto = getAttribute(reverse.selectFirst("a[target=_blank]"), "href");
            DebugUtil.printProperty("reverse picture", reversePhoto, true, true, true);
            if (reversePhoto != null && !reversePhoto.isEmpty()) {
                nType.getReverse().setPicture(reversePhoto);
            }
        }
    }

    private static void loadEdgeDescription(Document page, NType nType) {
        String descriptionTranche = getTagText(page.selectFirst("#description_tranche"));
        DebugUtil.printProperty("edge description", descriptionTranche, true, false, true);
        if (descriptionTranche != null && !descriptionTranche.isEmpty()) {
            nType.getEdge().setDescription(descriptionTranche);
        }
    }

    private static void loadEdgeLettering(Document page, NType nType) {
        String texteTranche = getTagText(page.selectFirst("texte_tranche"));
        DebugUtil.printProperty("edge lettering", texteTranche, false, false, false);
        if (texteTranche != null && !texteTranche.isEmpty()) {
            nType.getEdge().setLettering(texteTranche);
        }
    }

    private static void loadEdgeScripts(Document page, NType nType) {
        nType.getEdge().getLetteringScripts().clear();

        List<HashMap<String, String>> scriptsTranche = getAttributesWithTextSelectedOptions(page.selectFirst("#script_tranche"));
        if (scriptsTranche != null) {
            for (HashMap<String, String> scriptTranche : scriptsTranche) {
                DebugUtil.printProperty("edge lettering_scripts[] code", scriptTranche.get("value"), false, false, false);
                DebugUtil.printProperty("edge lettering_scripts[] name", scriptTranche.get("text"), false, false, false);
                if (isValueAndTextNotNullAndNotEmpty(scriptTranche)) {
                    nType.getEdge().getLetteringScripts().add(N4JUtil.getInstance().numistaService.letteringScriptService.findByNid(scriptTranche.get("value"), scriptTranche.get("text")));
                }
            }
        }
    }

    private static void loadEdgeUnabridgedLegend(Document page, NType nType) {
        String unabridgedTranche = getTagText(page.selectFirst("#unabridged_tranche"));
        DebugUtil.printProperty("edge unabridged_legend", unabridgedTranche, false, false, false);
        if (unabridgedTranche != null && !unabridgedTranche.isEmpty()) {
            nType.getEdge().setUnabridgedLegend(unabridgedTranche);
        }
    }

    private static void loadEdgeLetteringTranslation(Document page, NType nType) {
        String traductionTranche = getTagText(page.selectFirst("#traduction_tranche"));
        DebugUtil.printProperty("edge lettering_translation", traductionTranche, false, false, false);
        if (traductionTranche != null && !traductionTranche.isEmpty()) {
            nType.getEdge().setLetteringTranslation(traductionTranche);
        }
    }

    private static void loadEdgePicture(Document page, NType nType) {
        Element legendEdge = page.selectFirst("fieldset>legend:containsOwn(Edge)");
        if (legendEdge != null) {
            String edgePhoto = getAttribute(legendEdge.parent().selectFirst("a[target=_blank]"), "href");
            DebugUtil.printProperty("edge photo", edgePhoto, true, false, false);
            if (edgePhoto != null && !edgePhoto.isEmpty()) {
                nType.getEdge().setPicture(edgePhoto);
            }
        }
    }

    private static void loadWatermarkDescription(Document page, NType nType) {
        String descriptionWatermark = getTagText(page.selectFirst("#description_watermark"));
        DebugUtil.printProperty("watermark description", descriptionWatermark, false, true, false);
        if (descriptionWatermark != null && !descriptionWatermark.isEmpty()) {
            nType.getWatermark().setDescription(descriptionWatermark);
        }
    }

    private static void loadWatermarkPicture(Document page, NType nType) {
        Element legendWatermarkElement = page.selectFirst("fieldset:contains(Watermark)");
        if (legendWatermarkElement != null) {
            String watermarkPhoto = getAttribute(legendWatermarkElement.selectFirst("a[target=_blank]"), "href");
            DebugUtil.printProperty("watermark photo", watermarkPhoto, false, true, false);
            if (watermarkPhoto != null && !watermarkPhoto.isEmpty()) {
                nType.getWatermark().setPicture(watermarkPhoto);
            }
        }
    }

    private static void loadMints(Document page, NType nType) {
        Element mints = page.selectFirst("fieldset:contains(Mint(s))");
        if (mints != null) {

            nType.getSpecifiedMints().clear();

            int i = 0;
            while (true) {
                Element mintIdentifierElement = mints.selectFirst("input[name=mint_identifier" + i + "]");
                Element mintElement = mints.selectFirst("select[name=mint" + i + "]");
                Element mintmarkElement = mints.selectFirst("select[name=mintmark" + i + "]");
                if (mintIdentifierElement != null && mintElement != null && mintmarkElement != null && mintElement.selectFirst("option") != null) {

                    Mint mint = null;

                    HashMap<String, String> mintCode = getAttributeWithTextSingleOption(mintElement, "value");
                    DebugUtil.printProperty("mints[] id", mintCode.get("value"), true, false, false);
                    DebugUtil.printProperty("mints[] name_full", mintCode.get("text"), true, false, false);
                    if (isValueAndTextNotNullAndNotEmpty(mintCode)) {
                        mint = N4JUtil.getInstance().numistaService.mintService.findByNid(mintCode.get("value"), mintCode.get("text"));
                    }

                    if (mint != null) {

                        String mintmarkIdentifier = getAttribute(mintIdentifierElement, "value");
                        DebugUtil.printProperty("mints[] mintmark identifier", mintmarkIdentifier, true, false, false);

                        if (mintmarkIdentifier == null) {
                            mintmarkIdentifier = "";
                        }

                        Mintmark mintmark = null;
                        HashMap<String, String> mintmarkHashMap = getAttributeWithTextSingleOption(mintmarkElement, "value");
                        DebugUtil.printProperty("mints[] mintmark code", mintmarkHashMap.get("value"), true, false, false);
                        DebugUtil.printProperty("mints[] mintmark picture", mintmarkHashMap.get("text"), true, false, false);

                        if (isValueAndTextNotNullAndNotEmpty(mintmarkHashMap)) {
                            mintmark = N4JUtil.getInstance().numistaService.mintmarkService.findByNid(mintmarkHashMap.get("value"));
                            if (mintmark != null && mintmark.getPicture() != null && !mintmark.getPicture().equals(mintmarkHashMap.get("text"))) {
                                mintmark.setPicture(mintmarkHashMap.get("text"));
                                N4JUtil.getInstance().numistaService.mintmarkService.save(mintmark);
                            }
                        }

                        SpecifiedMint specifiedMint = N4JUtil.getInstance().numistaService.specifiedMintService.findByIdentifierMintMintmark(mintmarkIdentifier, mint.getNid(), mintmark != null ? mintmark.getNid() : null);
                        if (specifiedMint == null) {
                            specifiedMint = new SpecifiedMint();
                            specifiedMint.setIdentifier(mintmarkIdentifier);
                            specifiedMint.setMint(mint);
                            specifiedMint.setMintmark(mintmark);
                            specifiedMint = N4JUtil.getInstance().numistaService.specifiedMintService.save(specifiedMint);
                        }
                        nType.getSpecifiedMints().add(specifiedMint);
                    }
                    i++;
                } else {
                    break;
                }
            }
        }
    }

    private static void loadPrinters(Document page, NType nType) {
        nType.getPrinters().clear();

        Element printerTableElement = page.selectFirst("#mint_table");
        if (printerTableElement != null) {
            Elements selectElements = printerTableElement.select("select[class=printer_select]");
            for (Element select : selectElements) {
                HashMap<String, String> mint = getAttributeWithTextSingleOption(select, "value");
                if (mint != null) {
                    DebugUtil.printProperty("printer mint value", mint.get("value"), false, true, false);
                    DebugUtil.printProperty("printer mint name", mint.get("text"), false, true, false);
                    if (isValueAndTextNotNullAndNotEmpty(mint)) {
                        nType.getPrinters().add(N4JUtil.getInstance().numistaService.printerService.findByNid(mint.get("value"), mint.get("text")));
                    }
                }
            }
        }
    }

    private static void loadComments(Document page, NType nType) {
        String commentaires = getTagText(page.selectFirst("#commentaires"));
        DebugUtil.printProperty("comments", commentaires, true, true, true);
        if (commentaires != null && !commentaires.isEmpty()) {
            nType.setComments(commentaires);
        }

    }

    private static void loadLinks(Document page, NType nType) {
        Element references = page.selectFirst("fieldset:contains(References)");
        if (references != null) {
            String links = getTagText(references.selectFirst("textarea[name=liens]"));
            DebugUtil.printProperty("links[]", links, true, true, false);
            if (links != null && !links.isEmpty()) {
                nType.setLinks(links);
            }
        }
    }

    private static void loadMintageCalendar(Document page, NType nType) {
        HashMap<String, String> calendar = getAttributeWithTextSelectedOption(page.selectFirst("#annees"));
        if (calendar == null) {
            nType.setCalendar(N4JUtil.getInstance().numistaService.calendarService.findByCode("inconnu", "Unknown"));
        } else {
            DebugUtil.printProperty("issues calendar code", calendar.get("value"), true, true, true);
            DebugUtil.printProperty("issues calendar name", calendar.get("text"), true, true, true);
            if (isValueAndTextNotNullAndNotEmpty(calendar)) {
                nType.setCalendar(N4JUtil.getInstance().numistaService.calendarService.findByCode(calendar.get("value"), calendar.get("text")));
            }
        }

    }

    private static void loadIssues(Document page, NType nType) {
        Element dateTableElement = page.selectFirst("#date_table");
        if (dateTableElement != null) {
            Elements trsElements = dateTableElement.select("tr");

            nType.getVariants().clear();

            for (Element tr : trsElements) {
                if (tr.attributes().hasKey("id")) { //to avoid catch a tr with min-max dates, when previous tr with date_check is checked (no date)
                    continue;
                }
                Elements tdElements = tr.select("td");
                Variant variant = null;
                for (Element td : tdElements) {

                    if (td.attributes().get("class").equals("date_check")) {
                        Element checkboxElement = td.selectFirst("input");
                        if (checkboxElement != null) {
                            if (checkboxElement.attributes().get("name").startsWith("nouveau")) continue;

                            if (checkboxElement.attributes().hasKey("checked")) {

                                String id = checkboxElement.attributes().get("name").replace("nd", "");
                                DebugUtil.printProperty("issues[] id", id, true, false, false);
                                if (!id.isEmpty()) {
                                    variant = N4JUtil.getInstance().numistaService.variantService.findByNid(id);
                                } else {
                                    continue;
                                }
                                DebugUtil.printProperty("issues[] is_dated", "false", true, true, true);
                                variant.setDated(false);

                                Element datesElement = page.selectFirst("#dates" + id);
                                if (datesElement != null) {
                                    Element datedElement = datesElement.selectFirst("input[name=dated" + id + "]");
                                    if (datedElement != null) {
                                        String min_year = datedElement.attributes().get("value");

                                        DebugUtil.printProperty("issues[] min_year", min_year, true, false, false);
                                        if (!min_year.isEmpty()) {
                                            variant.setMinYear(Integer.parseInt(min_year));
                                        } else {
                                            variant.setMinYear(Integer.MIN_VALUE);
                                        }
                                    }

                                    Element datefElement = datesElement.selectFirst("input[name=datef" + id + "]");
                                    if (datefElement != null) {
                                        String max_year = datefElement.attributes().get("value");
                                        DebugUtil.printProperty("issues[] max_year", max_year, true, false, false);
                                        if (!max_year.isEmpty()) {
                                            variant.setMaxYear(Integer.parseInt(max_year));
                                        } else {
                                            variant.setMaxYear(Integer.MAX_VALUE);
                                        }
                                    }
                                }

                            } else {
                                DebugUtil.printProperty("issues[] is_dated", "true", true, true, true);
                            }

                        }
                    } else if (td.attributes().get("class").equals("date_year")) {
                        Element dateYearElement = td.selectFirst("input");
                        if (dateYearElement != null) {
                            if (!dateYearElement.attributes().hasKey("disabled")) {
                                if (dateYearElement.attributes().get("id").contains("nouveau")) { // to avoid empty rows
                                    continue;
                                }
                                String id = dateYearElement.attributes().get("id").replace("millesime", "");
                                DebugUtil.printProperty("issues[] id", id, true, true, true);
                                if (!id.isEmpty()) {
                                    variant = N4JUtil.getInstance().numistaService.variantService.findByNid(id);
                                }

//                                if (variant != null && is_dated) {
//                                    check for null value for is_dated
//                                    variant.setDated(true);
//                                }

                                //pattern="[?]|-?[0-9]+"
                                String year = dateYearElement.attributes().get("value");
                                DebugUtil.printProperty("issues[] year", year, true, true, true);

                                if (variant != null && !Objects.equals(variant.getDateYear(), Integer.valueOf(year))) {
                                    DebugUtil.showServiceMessage(NumistaEditPageUtil.class, "New value for Year of Variant with nid=" + variant.getNid() + " will be set (" + variant.getDateYear() + " -> " + Integer.valueOf(year), DebugUtil.MESSAGE_LEVEL.WARNING);
                                    variant.setDateYear(Integer.parseInt(year));
                                    if (nType.getCalendar() != null && nType.getCalendar().getToGregorianShift() != null) {
                                        variant.setGregorianYear(Integer.parseInt(year) + nType.getCalendar().getToGregorianShift());
                                    }

                                }
                            }
                        }
                    } else if (td.attributes().get("class").equals("date_month")) {
                        Element dateMonthElement = td.selectFirst("input");
                        if (dateMonthElement != null) {
                            if (!dateMonthElement.attributes().hasKey("disabled")) {
                                if (dateMonthElement.attributes().get("id").contains("nouveau")) { // to avoid empty rows
                                    continue;
                                }
                                //pattern="[0-9]{1,2}"
                                if (!dateMonthElement.attributes().get("value").isEmpty()) {
                                    String month = dateMonthElement.attributes().get("value");
                                    DebugUtil.printProperty("issues[] month", month, false, false, false);

                                    if (variant != null && variant.getDateMonth() != null && !Objects.equals(variant.getDateMonth(), Integer.valueOf(month))) {
                                        DebugUtil.showServiceMessage(NumistaEditPageUtil.class, "New value for Month of Variant with nid=" + variant.getNid() + " will be set (" + variant.getDateMonth() + " -> " + Integer.valueOf(month), DebugUtil.MESSAGE_LEVEL.WARNING);
                                        variant.setDateMonth(Integer.parseInt(month));
                                    }
                                }
                            }
                        }
                    } else if (td.attributes().get("class").equals("date_day")) {
                        Element dateDayElement = td.selectFirst("input");
                        if (dateDayElement != null) {
                            if (!dateDayElement.attributes().hasKey("disabled")) {
                                if (dateDayElement.attributes().get("id").contains("nouveau")) { // to avoid empty rows
                                    continue;
                                }
                                //pattern="[0-9]{1,2}"
                                if (!dateDayElement.attributes().get("value").isEmpty()) {
                                    String day = dateDayElement.attributes().get("value");
                                    DebugUtil.printProperty("issues[] day", day, false, false, false);

                                    if (variant != null && variant.getDateDay() != null && !Objects.equals(variant.getDateDay(), Integer.valueOf(day))) {
                                        DebugUtil.showServiceMessage(NumistaEditPageUtil.class, "New value for Day of Variant with nid=" + variant.getNid() + " will be set (" + variant.getDateDay() + " -> " + Integer.valueOf(day), DebugUtil.MESSAGE_LEVEL.WARNING);
                                        variant.setDateDay(Integer.parseInt(day));
                                    }
                                }
                            }
                        }
                    } else if (td.attributes().get("class").equals("date_mint")) {
                        Element dateMintElement = td.selectFirst("input");
                        if (dateMintElement != null) {
                            if (!dateMintElement.attributes().get("value").isEmpty()) {
                                String mint = dateMintElement.attributes().get("value");
                                DebugUtil.printProperty("issues[] mint", mint, true, false, false);
                                if (variant != null && !mint.isEmpty()) {
                                    variant.setSpecifiedMint(nType.getSpecifiedMints().stream().filter(specifiedMint -> {
                                        if (specifiedMint.getIdentifier().contains(mint)) {
                                            return true;
                                        }
                                        return false;
                                    }).findFirst().orElse(null));
                                }
                            }
                        }
                    } else if (td.attributes().get("class").equals("date_mark")) {
                        Elements marksElements = td.select("option");
                        for (Element markElement : marksElements) {
                            String markCode = markElement.attributes().get("value");
                            DebugUtil.printProperty("issues[] marks[] id", markCode, true, false, false);
                            if (!markCode.isEmpty()) {
                                Mark mark = N4JUtil.getInstance().numistaService.markService.findByNid(markCode);
                                if (variant != null && !variant.getMarks().contains(mark)) {
                                    variant.getMarks().add(mark);
                                }
                            }

                        }
                    } else if (td.attributes().get("class").equals("date_mintage")) {
                        Element mintageElement = td.selectFirst("input");
                        if (mintageElement != null) {
                            if (!mintageElement.attributes().get("value").isEmpty()) {
                                String mintage = mintageElement.attributes().get("value");
                                DebugUtil.printProperty("issues[] mintage", mintage, true, false, false);
                                if (variant != null && !mintage.isEmpty()) {
                                    variant.setMintage(Integer.parseInt(mintage.replace(" ", "")));
                                }
                            }
                        }
                    } else if (td.attributes().get("class").equals("")) {
                        Element commentaireElement = td.selectFirst("input");
                        if (commentaireElement != null && commentaireElement.attributes().get("name").startsWith("commentaire")) {
                            if (!commentaireElement.attributes().get("value").isEmpty()) {
                                String comment = commentaireElement.attributes().get("value");
                                DebugUtil.printProperty("issues[] comment", comment, true, true, true);
                                if (variant != null && !comment.isEmpty()) {
                                    variant.setComment(comment);
                                }
                            }
                        }
                    }
                }
                if (variant != null) {
                    variant = N4JUtil.getInstance().numistaService.variantService.save(variant);
                    nType.getVariants().add(variant);
                }
            }
        }

    }


    private static void loadTagReferences(Document page, NType nType) {

        nType.getNTags().clear();

        //Tags (an hierarchy of tags (1st and 2nd levels))
        // <option value="34">Agriculture</option> <option value="12" disabled>Animals</option> <option value="133" data-level="2">Amphibian</option> <option value="151" data-level="2">Arachnid</option> <option value="68" selected data-level="2">Bear</option> <option value="13" data-level="2">Bird</option> <option value="82" data-level="2">Camel or camelid</option> <option value="16" data-level="2">Cat or feline</option> <option value="84" data-level="2">Cow or bovine</option> <option value="152" data-level="2">Crustacean</option> <option value="146" data-level="2">Deer or cervid</option> <option value="17" data-level="2">Dog or canid</option> <option value="64" data-level="2">Eagle</option> <option value="67" data-level="2">Elephant</option> <option value="65" data-level="2">Fantasy animal</option> <option value="14" data-level="2">Fish</option> <option value="153" data-level="2">Goat or caprine</option> <option value="15" data-level="2">Horse or equine</option> <option value="66" data-level="2">Insect</option> <option value="89" data-level="2">Marine invertebrate</option> <option value="69" data-level="2">Marine mammal</option> <option value="127" data-level="2">Marsupial</option> <option value="147" data-level="2">Other animal</option> <option value="126" data-level="2">Pig or porcine</option> <option value="40" data-level="2">Prehistoric animal</option> <option value="124" data-level="2">Primate</option> <option value="136" data-level="2">Rabbit</option> <option value="88" data-level="2">Reptile</option> <option value="135" data-level="2">Rodent</option> <option value="83" data-level="2">Sheep or ovine</option> <option value="70" data-level="2">Shell</option> <option value="63" data-level="2">Turtle or tortoise</option> <option value="185">Armour</option> <option value="24" disabled>Art</option> <option value="114" data-level="2">Cinema</option> <option value="27" data-level="2">Dance</option> <option value="26" data-level="2">Literature</option> <option value="25" data-level="2">Music</option> <option value="28" data-level="2">Painting</option> <option value="62" data-level="2">Sculpture</option> <option value="113" data-level="2">Theatre</option> <option value="30" disabled>Astrology</option> <option value="31" data-level="2">Chinese calendar</option> <option value="32" data-level="2">Western astrological sign</option> <option value="48" disabled>Buildings</option> <option value="90" data-level="2">Bridge</option> <option value="50" data-level="2">Castle or fortification</option> <option value="188" data-level="2">Government building</option> <option value="111" data-level="2">Lighthouse</option> <option value="187" data-level="2">Memorial</option> <option value="123" data-level="2">Museum</option> <option value="148" data-level="2">Other building</option> <option value="110" data-level="2">Palace</option> <option value="49" data-level="2">Religious building</option> <option value="149" data-level="2">Sports venue</option> <option value="76" data-level="2">Tower</option> <option value="168" data-level="2">Tunnel</option> <option value="176" disabled>Coins, banknotes, stamps</option> <option value="177" data-level="2">Banknote depiction</option> <option value="6" data-level="2">Coin depiction</option> <option value="178" data-level="2">Stamp depiction</option> <option value="121">Education</option> <option value="77" disabled>Events</option> <option value="80" data-level="2">Birth anniversary</option> <option value="154" data-level="2">Carnival or festival</option> <option value="78" data-level="2">Coronation</option> <option value="81" data-level="2">Death anniversary</option> <option value="118" data-level="2">Fair or exhibition</option> <option value="128" data-level="2">Independence</option> <option value="47" data-level="2">Marriage</option> <option value="166" data-level="2">National day</option> <option value="183" data-level="2">Religious holiday</option> <option value="167" data-level="2">Revolution or civil war</option> <option value="112" data-level="2">Treaty</option> <option value="91" disabled>Famous people</option> <option value="97" data-level="2">Artist</option> <option value="95" data-level="2">Explorer</option> <option value="179" data-level="2">Fictional character</option> <option value="180" data-level="2">Military leader</option> <option value="92" data-level="2">Monarch</option> <option value="96" data-level="2">Philosopher</option> <option value="93" data-level="2">Politician</option> <option value="143" data-level="2">Religious figure</option> <option value="94" data-level="2">Scientist</option> <option value="181" data-level="2">Sportsperson</option> <option value="122">Firearm or handheld weapon</option> <option value="5">Flag</option> <option value="37" disabled>Games and sports events</option> <option value="51" data-level="2">Asian Games</option> <option value="39" data-level="2">Commonwealth Games</option> <option value="175" data-level="2">FIFA World Cup</option> <option value="38" data-level="2">Summer Olympic Games</option> <option value="45" data-level="2">Winter Olympic Games</option> <option value="150">Hand</option> <option value="184">Handicraft</option> <option value="41">Hat</option> <option value="119">Health</option> <option value="35">Industry</option> <option value="71" disabled>Landscapes</option> <option value="75" data-level="2">Desert</option> <option value="73" data-level="2">Lake</option> <option value="72" data-level="2">Mountain</option> <option value="140" data-level="2">River</option> <option value="74" data-level="2">Sea</option> <option value="7">Map</option> <option value="18" disabled>Means of transport</option> <option value="170" data-level="2">Bike or motorcycle</option> <option value="20" data-level="2">Boat or watercraft</option> <option value="21" data-level="2">Car</option> <option value="22" data-level="2">Plane or aircraft</option> <option value="19" data-level="2">Train</option> <option value="169" data-level="2">Truck or tractor</option> <option value="36">Millennium</option> <option value="42">Mustache</option> <option value="29">Mythology</option> <option value="163">Natural phenomenon</option> <option value="134" disabled>Objects</option> <option value="137" data-level="2">Anchor</option> <option value="172" data-level="2">Armillary sphere</option> <option value="144" data-level="2">Container or tableware</option> <option value="138" data-level="2">Crown</option> <option value="164" data-level="2">Globe</option> <option value="139" data-level="2">Scale</option> <option value="155" data-level="2">Toy or game</option> <option value="157" disabled>Organizations</option> <option value="1" data-level="2">FAO</option> <option value="165" data-level="2">Red Cross</option> <option value="44" data-level="2">United Nations</option> <option value="120">Peace</option> <option value="85" disabled>Places</option> <option value="115" data-level="2">Beach</option> <option value="87" data-level="2">Park</option> <option value="116" data-level="2">Region</option> <option value="86" data-level="2">Town</option> <option value="9" disabled>Plants</option> <option value="11" data-level="2">Flower</option> <option value="131" data-level="2">Fruit</option> <option value="10" data-level="2">Tree</option> <option value="145">Puzzle coin</option> <option value="46">Science</option> <option value="159" disabled>Social history</option> <option value="160" data-level="2">Feminism</option> <option value="161" data-level="2">Human rights</option> <option value="162" data-level="2">Slavery</option> <option value="23">Space</option> <option value="2" disabled>Sports</option> <option value="54" data-level="2">Athletics</option> <option value="107" data-level="2">Baseball</option> <option value="52" data-level="2">Basketball</option> <option value="129" data-level="2">Boxing or wrestling</option> <option value="79" data-level="2">Cricket</option> <option value="108" data-level="2">Cycling</option> <option value="132" data-level="2">Golf</option> <option value="59" data-level="2">Gymnastics</option> <option value="53" data-level="2">Hockey</option> <option value="109" data-level="2">Martial arts</option> <option value="182" data-level="2">Other sport</option> <option value="56" data-level="2">Rugby</option> <option value="55" data-level="2">Ski</option> <option value="3" data-level="2">Soccer</option> <option value="61" data-level="2">Swimming</option> <option value="60" data-level="2">Table tennis</option> <option value="57" data-level="2">Tennis</option> <option value="58" data-level="2">Volleyball</option> <option value="158">Sustainability</option> <option value="98" disabled>Symbols</option> <option value="43" data-level="2">Allegory</option> <option value="100" data-level="2">Caduceus</option> <option value="156" data-level="2">Christogram</option> <option value="4" data-level="2">Coat of Arms</option> <option value="102" data-level="2">Cornucopia</option> <option value="105" data-level="2">Crescent</option> <option value="101" data-level="2">Cross</option> <option value="171" data-level="2">Double-headed eagle</option> <option value="117" data-level="2">Fasces</option> <option value="99" data-level="2">Fleur-de-lis</option> <option value="173" data-level="2">Globus cruciger</option> <option value="104" data-level="2">Hammer and sickle</option> <option value="141" data-level="2">Monogram</option> <option value="103" data-level="2">Phrygian cap</option> <option value="106" data-level="2">Star of David</option> <option value="142" data-level="2">Triskelion</option> <option value="186" data-level="2">Trophy of arms</option> <option value="130" data-level="2">Wreath</option> <option value="174">Technology</option> <option value="33">Trade</option> <option value="8">War</option>
        Element tagsElement = page.selectFirst("#tags");
        if (tagsElement != null) {
            Elements options = tagsElement.select("option");
            Element firstLevel = null;
            for (Element option : options) {
                if (option.attributes().hasKey("disabled")) {
                    firstLevel = option;
                    continue;
                }
                if (option.attributes().hasKey("selected")) {
                    if (option.attributes().hasKey("data-level")) {
                        String tagSecondLevelCode = option.attributes().get("value");
                        String tagSecondLevelName = option.text();

                        String tagFirstLevelCode = "";
                        String tagFirstLevelName = "";
                        if (firstLevel != null) {
                            tagFirstLevelCode = firstLevel.attributes().get("value");
                            tagFirstLevelName = firstLevel.text();

                            DebugUtil.printProperty("tags[] disabled_first_level code", tagFirstLevelCode, true, true, true);
                            DebugUtil.printProperty("tags[] disabled_first_level name", tagFirstLevelName, true, true, true);
                        }

                        DebugUtil.printProperty("tags[] second_level code", tagSecondLevelCode, true, true, true);
                        DebugUtil.printProperty("tags[] second_level name", tagSecondLevelName, true, true, true);

                        if (!tagFirstLevelCode.equals("") && !tagFirstLevelName.equals("") && !tagSecondLevelCode.equals("") && !tagSecondLevelName.equals("")) {
                            NTag nTagFirstLevel = N4JUtil.getInstance().numistaService.nTagService.findByNid(tagFirstLevelCode, tagFirstLevelName);
                            NTag nTagSecondLevel = N4JUtil.getInstance().numistaService.nTagService.findByNid(tagSecondLevelCode, tagSecondLevelName);

                            nTagSecondLevel.setParent(nTagFirstLevel);
                            N4JUtil.getInstance().numistaService.nTagService.save(nTagSecondLevel);
                            nType.getNTags().add(nTagSecondLevel);

                        }

                    } else {
                        String tagFirstLevelCode = option.attributes().get("value");
                        String tagFirstLevelName = option.text();

                        DebugUtil.printProperty("tags[] first_level code", tagFirstLevelCode, true, true, true);
                        DebugUtil.printProperty("tags[] first_level name", tagFirstLevelName, true, true, true);

                        NTag nTagFirstLevel = N4JUtil.getInstance().numistaService.nTagService.findByNid(tagFirstLevelCode, tagFirstLevelName);
                        nType.getNTags().add(nTagFirstLevel);

                    }
                }
            }
        }

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


    private static Document getNumistaPage(String numistaNumber) {
        Document document;
        try {

            URL url = new URL("https://en.numista.com/catalogue/contributions/modifier.php?id=" + numistaNumber);

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Set the cookie value to send
            con.setRequestProperty("Cookie", "_pk_id.10.0242=98c7c0d8feae54a1.1669823762.; _pk_ref.10.0242=%5B%22%22%2C%22%22%2C1679756455%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; _pk_ses.10.0242=1; _pk_testcookie=1; tb=y; tc=y; tn=y; tp=n; tt=n; PHPSESSID=k0ot7rr75am7q6r72prfvva4bj; carte=piece; tbb=y; tbc=y; tbl=y; tbt=y; _ga=GA1.2.1130692859.1674417278; ph_phc_Tbfg4EiRsr5iefFoth2Y1Hi3sttTeLQ5RV5TLg4hL1W_posthog=%7B%22distinct_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24device_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24session_recording_enabled_server_side%22%3Afalse%2C%22%24initial_referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24initial_referring_domain%22%3A%22en.numista.com%22%2C%22%24referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24referring_domain%22%3A%22en.numista.com%22%2C%22%24sesid%22%3A%5B1677792463026%2C%22186a43948b3efc-076d29a9c96b9d-3d626b4b-29b188-186a43948b4159f%22%2C1677792463026%5D%7D; pass=44e1894842815ca70e0e4727b600765f; pseudo=kbobryakov; _gcl_au=1.1.1579430192.1674417278; pieces_par_page=200");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (responseCode == 404) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append(System.getProperty("line.separator"));
            }

            in.close();

            // Send the request to the server
            document = Jsoup.parse(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }

    private static Document getNumistaIssuingEntitiesByPHP(String issuerCode) {
        Document document;
        try {

            URL url = URI.create("https://en.numista.com/catalogue/get_issuing_entities.php?country=" + issuerCode + "&prefill=").toURL();

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Set the cookie value to send
            con.setRequestProperty("Cookie", "_pk_id.10.0242=98c7c0d8feae54a1.1669823762.; _pk_ref.10.0242=%5B%22%22%2C%22%22%2C1679756455%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; _pk_ses.10.0242=1; _pk_testcookie=1; tb=y; tc=y; tn=y; tp=n; tt=n; PHPSESSID=k0ot7rr75am7q6r72prfvva4bj; carte=piece; tbb=y; tbc=y; tbl=y; tbt=y; _ga=GA1.2.1130692859.1674417278; ph_phc_Tbfg4EiRsr5iefFoth2Y1Hi3sttTeLQ5RV5TLg4hL1W_posthog=%7B%22distinct_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24device_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24session_recording_enabled_server_side%22%3Afalse%2C%22%24initial_referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24initial_referring_domain%22%3A%22en.numista.com%22%2C%22%24referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24referring_domain%22%3A%22en.numista.com%22%2C%22%24sesid%22%3A%5B1677792463026%2C%22186a43948b3efc-076d29a9c96b9d-3d626b4b-29b188-186a43948b4159f%22%2C1677792463026%5D%7D; pass=44e1894842815ca70e0e4727b600765f; pseudo=kbobryakov; _gcl_au=1.1.1579430192.1674417278; pieces_par_page=200");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (responseCode == 404) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append(System.getProperty("line.separator"));
            }

            in.close();

            // Send the request to the server
            document = Jsoup.parse(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }

    /**
     * Load Denominations from Numista PHP GET request with countryNid input value
     *
     * @param currencyNid
     * @return
     */
    private static Document getNumistaDenominationsByPHP(String currencyNid) {
        Document document;
        try {

            URL url = URI.create("/catalogue/get_denominations.php?currency=" + currencyNid + "&prefill=").toURL();

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Set the cookie value to send
            con.setRequestProperty("Cookie", "_pk_id.10.0242=98c7c0d8feae54a1.1669823762.; _pk_ref.10.0242=%5B%22%22%2C%22%22%2C1679756455%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; _pk_ses.10.0242=1; _pk_testcookie=1; tb=y; tc=y; tn=y; tp=n; tt=n; PHPSESSID=k0ot7rr75am7q6r72prfvva4bj; carte=piece; tbb=y; tbc=y; tbl=y; tbt=y; _ga=GA1.2.1130692859.1674417278; ph_phc_Tbfg4EiRsr5iefFoth2Y1Hi3sttTeLQ5RV5TLg4hL1W_posthog=%7B%22distinct_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24device_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24session_recording_enabled_server_side%22%3Afalse%2C%22%24initial_referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24initial_referring_domain%22%3A%22en.numista.com%22%2C%22%24referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24referring_domain%22%3A%22en.numista.com%22%2C%22%24sesid%22%3A%5B1677792463026%2C%22186a43948b3efc-076d29a9c96b9d-3d626b4b-29b188-186a43948b4159f%22%2C1677792463026%5D%7D; pass=44e1894842815ca70e0e4727b600765f; pseudo=kbobryakov; _gcl_au=1.1.1579430192.1674417278; pieces_par_page=200");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (responseCode == 404) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append(System.getProperty("line.separator"));
            }

            in.close();

            // Send the request to the server
            document = Jsoup.parse(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }

    private static Document getNumistaCurrenciesByPHP(String issuerCode) {
        Document document;
        try {

            URL url = URI.create("https://en.numista.com/catalogue/get_currencies.php?country=" + issuerCode).toURL();

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Set the cookie value to send
            con.setRequestProperty("Cookie", "_pk_id.10.0242=98c7c0d8feae54a1.1669823762.; _pk_ref.10.0242=%5B%22%22%2C%22%22%2C1679756455%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; _pk_ses.10.0242=1; _pk_testcookie=1; tb=y; tc=y; tn=y; tp=n; tt=n; PHPSESSID=k0ot7rr75am7q6r72prfvva4bj; carte=piece; tbb=y; tbc=y; tbl=y; tbt=y; _ga=GA1.2.1130692859.1674417278; ph_phc_Tbfg4EiRsr5iefFoth2Y1Hi3sttTeLQ5RV5TLg4hL1W_posthog=%7B%22distinct_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24device_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24session_recording_enabled_server_side%22%3Afalse%2C%22%24initial_referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24initial_referring_domain%22%3A%22en.numista.com%22%2C%22%24referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24referring_domain%22%3A%22en.numista.com%22%2C%22%24sesid%22%3A%5B1677792463026%2C%22186a43948b3efc-076d29a9c96b9d-3d626b4b-29b188-186a43948b4159f%22%2C1677792463026%5D%7D; pass=44e1894842815ca70e0e4727b600765f; pseudo=kbobryakov; _gcl_au=1.1.1579430192.1674417278; pieces_par_page=200");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (responseCode == 404) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append(System.getProperty("line.separator"));
            }

            in.close();

            // Send the request to the server
            document = Jsoup.parse(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }

    private Document getNumistaBanks(String country) {
        Document document;
        try {

            URL url = new URL("https://en.numista.com/catalogue/get_banks.php?country=" + country);

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Set the cookie value to send
            con.setRequestProperty("Cookie", "_pk_id.10.0242=98c7c0d8feae54a1.1669823762.; _pk_ref.10.0242=%5B%22%22%2C%22%22%2C1679756455%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; _pk_ses.10.0242=1; _pk_testcookie=1; tb=y; tc=y; tn=y; tp=n; tt=n; PHPSESSID=k0ot7rr75am7q6r72prfvva4bj; carte=piece; tbb=y; tbc=y; tbl=y; tbt=y; _ga=GA1.2.1130692859.1674417278; ph_phc_Tbfg4EiRsr5iefFoth2Y1Hi3sttTeLQ5RV5TLg4hL1W_posthog=%7B%22distinct_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24device_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24session_recording_enabled_server_side%22%3Afalse%2C%22%24initial_referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24initial_referring_domain%22%3A%22en.numista.com%22%2C%22%24referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24referring_domain%22%3A%22en.numista.com%22%2C%22%24sesid%22%3A%5B1677792463026%2C%22186a43948b3efc-076d29a9c96b9d-3d626b4b-29b188-186a43948b4159f%22%2C1677792463026%5D%7D; pass=44e1894842815ca70e0e4727b600765f; pseudo=kbobryakov; _gcl_au=1.1.1579430192.1674417278; pieces_par_page=200");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (responseCode == 404) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append(System.getProperty("line.separator"));
            }

            in.close();

            // Send the request to the server
            document = Jsoup.parse(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }


    private Document getNumistaCurrencies(String country) {
        Document document;
        try {

            URL url = new URL("https://en.numista.com/catalogue/get_currencies.php?country=" + country);

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Set the cookie value to send
            con.setRequestProperty("Cookie", "_pk_id.10.0242=98c7c0d8feae54a1.1669823762.; _pk_ref.10.0242=%5B%22%22%2C%22%22%2C1679756455%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; _pk_ses.10.0242=1; _pk_testcookie=1; tb=y; tc=y; tn=y; tp=n; tt=n; PHPSESSID=k0ot7rr75am7q6r72prfvva4bj; carte=piece; tbb=y; tbc=y; tbl=y; tbt=y; _ga=GA1.2.1130692859.1674417278; ph_phc_Tbfg4EiRsr5iefFoth2Y1Hi3sttTeLQ5RV5TLg4hL1W_posthog=%7B%22distinct_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24device_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24session_recording_enabled_server_side%22%3Afalse%2C%22%24initial_referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24initial_referring_domain%22%3A%22en.numista.com%22%2C%22%24referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24referring_domain%22%3A%22en.numista.com%22%2C%22%24sesid%22%3A%5B1677792463026%2C%22186a43948b3efc-076d29a9c96b9d-3d626b4b-29b188-186a43948b4159f%22%2C1677792463026%5D%7D; pass=44e1894842815ca70e0e4727b600765f; pseudo=kbobryakov; _gcl_au=1.1.1579430192.1674417278; pieces_par_page=200");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (responseCode == 404) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append(System.getProperty("line.separator"));
            }

            in.close();

            // Send the request to the server
            document = Jsoup.parse(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }


    private Document getNumistaRulers(String country) {
        Document document;
        try {

            URL url = new URL("https://en.numista.com/catalogue/get_rulers.php?country=" + country);

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Set the cookie value to send
            con.setRequestProperty("Cookie", "_pk_id.10.0242=98c7c0d8feae54a1.1669823762.; _pk_ref.10.0242=%5B%22%22%2C%22%22%2C1679756455%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; _pk_ses.10.0242=1; _pk_testcookie=1; tb=y; tc=y; tn=y; tp=n; tt=n; PHPSESSID=k0ot7rr75am7q6r72prfvva4bj; carte=piece; tbb=y; tbc=y; tbl=y; tbt=y; _ga=GA1.2.1130692859.1674417278; ph_phc_Tbfg4EiRsr5iefFoth2Y1Hi3sttTeLQ5RV5TLg4hL1W_posthog=%7B%22distinct_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24device_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24session_recording_enabled_server_side%22%3Afalse%2C%22%24initial_referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24initial_referring_domain%22%3A%22en.numista.com%22%2C%22%24referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24referring_domain%22%3A%22en.numista.com%22%2C%22%24sesid%22%3A%5B1677792463026%2C%22186a43948b3efc-076d29a9c96b9d-3d626b4b-29b188-186a43948b4159f%22%2C1677792463026%5D%7D; pass=44e1894842815ca70e0e4727b600765f; pseudo=kbobryakov; _gcl_au=1.1.1579430192.1674417278; pieces_par_page=200");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (responseCode == 404) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append(System.getProperty("line.separator"));
            }

            in.close();

            // Send the request to the server
            document = Jsoup.parse(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }


    public Document getNumistaAllCoinsFronFile() {
        Document document;
        try {

            // Send the request to the server
            document = Jsoup.parse(new File("/Users/kirillbobryakov/Coins/Numista/Numista_coin_list.html"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }

    public Document getNumistaAllCoins() {
        Document document;
        try {

            URL url = new URL("https://en.numista.com/catalogue/toutes_pieces.php?ct=coin");

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Set the cookie value to send
//            con.setRequestProperty("Cookie", "_pk_id.10.0242=98c7c0d8feae54a1.1669823762.; _pk_ref.10.0242=%5B%22%22%2C%22%22%2C1679756455%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; _pk_ses.10.0242=1; _pk_testcookie=1; tb=y; tc=y; tn=y; tp=n; tt=n; PHPSESSID=k0ot7rr75am7q6r72prfvva4bj; carte=piece; tbb=y; tbc=y; tbl=y; tbt=y; _ga=GA1.2.1130692859.1674417278; ph_phc_Tbfg4EiRsr5iefFoth2Y1Hi3sttTeLQ5RV5TLg4hL1W_posthog=%7B%22distinct_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24device_id%22%3A%22185db0c0ab81ff-058fac83e2a881-3c626b4b-29b188-185db0c0ab91eea%22%2C%22%24session_recording_enabled_server_side%22%3Afalse%2C%22%24initial_referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24initial_referring_domain%22%3A%22en.numista.com%22%2C%22%24referrer%22%3A%22https%3A%2F%2Fen.numista.com%2F%22%2C%22%24referring_domain%22%3A%22en.numista.com%22%2C%22%24sesid%22%3A%5B1677792463026%2C%22186a43948b3efc-076d29a9c96b9d-3d626b4b-29b188-186a43948b4159f%22%2C1677792463026%5D%7D; pass=44e1894842815ca70e0e4727b600765f; pseudo=kbobryakov; _gcl_au=1.1.1579430192.1674417278; pieces_par_page=200");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (responseCode == 404) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append(System.getProperty("line.separator"));
            }

            in.close();

            // Send the request to the server
            document = Jsoup.parse(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }


}

