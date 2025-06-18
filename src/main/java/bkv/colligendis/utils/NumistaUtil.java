//package bkv.colligendis.utils;
//
//import bkv.colligendis.database.entity.features.*;
//import bkv.colligendis.database.entity.features.Value;
//import bkv.colligendis.database.entity.item.*;
//import bkv.colligendis.database.entity.numista.*;
//import bkv.colligendis.services.ServiceUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.parser.Tag;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//
//public class NumistaUtil {
//
//    public static final String ANSI_RESET = "\u001B[0m";
//
//    public static final String ANSI_BLACK = "\u001B[30m";
//    public static final String ANSI_RED = "\u001B[31m";
//    public static final String ANSI_GREEN = "\u001B[32m";
//    public static final String ANSI_YELLOW = "\u001B[33m";
//    public static final String ANSI_BLUE = "\u001B[34m";
//    public static final String ANSI_PURPLE = "\u001B[35m";
//    public static final String ANSI_CYAN = "\u001B[36m";
//    public static final String ANSI_WHITE = "\u001B[37m";
//
//    public static final String COINS = "Coins";
//    public static final String BANKNOTES = "Banknotes";
//    public static final String EXONUMIA = "Exonumia";
//
//
//    public static final String COMMEMORATIVE_ISSUE = "Commemorative issue";
//    public static final String OBVERSE = "Obverse";
//    public static final String REVERSE = "Reverse";
//    public static final String EDGE = "Edge";
//    public static final String COMMENTS = "Comments";
//    public static final String MINT = "Mint";
//    public static final String MINTS = "Mints";
//    public static final String MARK = "Mark";
//
//
//    private final ServiceUtils serviceUtils;
//    private static NumistaUtil instance;
//
//
//    private NumistaUtil(ServiceUtils serviceUtils) {
//        this.serviceUtils = serviceUtils;
//
//
//    }
//
//    public static void InitInstance(ServiceUtils serviceUtils) {
//        instance = new NumistaUtil(serviceUtils);
//    }
//
//    public static synchronized NumistaUtil getInstance() {
//        if (instance == null) {
//            try {
//                throw new Exception("NumistaUtil's Instance was forgotten to be initialized");
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return instance;
//    }
//
//
//    public Item loadItem(String numistaNumber) {
//
//        Document document = getNumistaPage(numistaNumber);
//
//        //Type
//        assert document != null;
//        Element main_breadcrumb = document.selectFirst("#main_breadcrumb");
//        Item item;
//
//        if (checkPieceType(main_breadcrumb, COINS)) {
//            System.out.println("Numista piece with number : " + numistaNumber + " is COIN");
//        } else if (checkPieceType(main_breadcrumb, BANKNOTES)) {
//            System.out.println("Numista piece with number : " + numistaNumber + " is BANKNOTE");
//        } else if (checkPieceType(main_breadcrumb, EXONUMIA)) {
//            System.out.println("Numista piece with number : " + numistaNumber + " is EXONUMIA");
//        }
//
//        item = loadItem(document, numistaNumber);
//        if (item != null) {
//            System.out.println(ANSI_GREEN + " ...LOADED" + ANSI_RESET);
//            return serviceUtils.itemService.save(item);
//        }
//
//        System.out.println(ANSI_RED + " ...ERROR" + ANSI_RESET);
//
//
//        return null;
//    }
//
//
//    private Document getNumistaPage(String numistaNumber) {
//        Document document;
//        try {
//            document = Jsoup.connect("https://en.numista.com/catalogue/pieces" + numistaNumber + ".html").get();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return document;
//    }
//
//
//    private boolean checkPieceType(Element main_breadcrumb, String pieceType) {
//        if (main_breadcrumb != null) {
//            ArrayList<String> mainBreadcrumbArray = new ArrayList<>();
//            for (Element span : main_breadcrumb.children()) {
//                Element item = span.selectFirst("span[itemprop=\"name\"]");
//                if (item != null) {
//                    mainBreadcrumbArray.add(item.text());
//                }
//            }
//            if (mainBreadcrumbArray.size() > 1 && mainBreadcrumbArray.get(1) != null) {
//                return mainBreadcrumbArray.get(1).equals(pieceType);
//            }
//        }
//        return false;
//    }
//
//
//    private Item loadItem(Document document, String numistaNumber) {
//
//        Item item = serviceUtils.itemService.findByNumistaNumber(numistaNumber);
//        if (item == null) {
//            item = new Item();
//            item.setNumistaNumber(numistaNumber);
//        } else {
//            System.out.println(ANSI_YELLOW + " ... EXISTS ... " + ANSI_RESET);
//        }
//
//        //Name
//        if (!applyName(document.selectFirst("#main_title"), item)) {
//            return null;
//        }
//
//
//        //Features
//        Element fiche_caracteristiques = document.selectFirst("#fiche_caracteristiques");
//        if (!applyCharacteristic(fiche_caracteristiques, item)) {
//            return null;
//        }
//
//        //Type
//        Element main_breadcrumb = document.selectFirst("#main_breadcrumb");
//
//        // set Territories
//        if (!applyTerritory(main_breadcrumb, item)) {
//            return null;
//        }
//
//        ItemSide obverse = item.getObverse() != null ? item.getObverse() : new ItemSide(ItemSide.OBVERSE);
//        ItemSide reverse = item.getReverse() != null ? item.getReverse() : new ItemSide(ItemSide.REVERSE);
//        item.setObverse(obverse);
//        item.setReverse(reverse);
//
//
//        //Reverse and Obverse photos
//        Element fiche_photo = document.selectFirst("#fiche_photo");
//
//        if (fiche_photo != null) {
//            Elements elements = fiche_photo.select("a[href]");
//            if (elements.get(0) != null) {
//                String link = elements.get(0).attr("href");
//                //todo
//                if(item.getTerritory() != null && item.getName() != null){
//                    String path = "";
//                    path += item.getTerritory().getName() + "/";
//                    path += item.getName() + "_" + numistaNumber + "/" + item.getName() + "_" + numistaNumber + "_obverse.jpg";
//                    ImageUtil.loadAndSaveImageToRoot(link, path);
//                    obverse.setPhotoLink(path);
//                }
//
//                obverse.setPhotoLink(link);
//            }
//            if (elements.get(1) != null) {
//                reverse.setPhotoLink(elements.get(1).attr("href"));
//            }
//        }
//
//        //Description
//        Element ficheDescriptions = document.selectFirst("#fiche_descriptions");
//        if (ficheDescriptions == null) {
//            System.out.println(ANSI_YELLOW + "Piece with number: " + numistaNumber + " without Description" + ANSI_RESET);
//            return null;
//        }
//
//        ArrayList<Element> commIssueElements = new ArrayList<>();
//        ArrayList<Element> obverseElements = new ArrayList<>();
//        ArrayList<Element> reverseElements = new ArrayList<>();
//        ArrayList<Element> edgeElements = new ArrayList<>();
//        ArrayList<Element> mintElements = new ArrayList<>();
//        ArrayList<Element> marksElements = new ArrayList<>();
//        ArrayList<Element> commentsElements = new ArrayList<>();
//
//        String currentDescr = "";
//        for (Element element : ficheDescriptions.children()) {
//            if (element.tag().equals(Tag.valueOf("h3"))) {
//                switch (element.text()) {
//                    case COMMEMORATIVE_ISSUE, OBVERSE, REVERSE, EDGE, MINT, MINTS, COMMENTS, MARK, "See also" ->
//                            currentDescr = element.text();
//                    default -> {
//                        System.out.println(ANSI_RED + "Piece with number: " + numistaNumber + " find new Part of Description: " + element.text() + ANSI_RESET);
//                        return null;
//                    }
//                }
//            } else if (currentDescr.equals(COMMEMORATIVE_ISSUE)) {
//                commIssueElements.add(element);
//            } else if (currentDescr.equals(OBVERSE)) {
//                obverseElements.add(element);
//            } else if (currentDescr.equals(REVERSE)) {
//                reverseElements.add(element);
//            } else if (currentDescr.equals(EDGE)) {
//                edgeElements.add(element);
//            } else if (currentDescr.equals(MINT) || currentDescr.equals(MINTS)) {
//                mintElements.add(element);
//            } else if (currentDescr.equals(MARK)) {
//                marksElements.add(element);
//            }
//        }
//
//        // Commemorative Description
//        applyCommemorativeDescription(commIssueElements, item);
//        // Obverse Description
//        applyPieceSideDescription(obverseElements, item.getObverse());
//        // Reverse Description
//        applyPieceSideDescription(reverseElements, item.getReverse());
//        // Edge Description
//
//        if (!edgeElements.isEmpty()) {
//            ItemSide edge = item.getEdge() != null ? item.getEdge() : new ItemSide(ItemSide.EDGE);
//            item.setEdge(edge);
//            applyPieceSideDescription(edgeElements, item.getEdge());
//        }
//
//        // Mint Description
//        applyMintDescription(mintElements, item);
//
//        // Mint Description
//        applyMarkDescription(marksElements, item);
//
//        //Comments
//        Element fiche_comments = document.selectFirst("#fiche_comments");
//        item.setHtmlComments(fiche_comments != null ? "<p id=\"fiche_comments\">" + fiche_comments.html() + "</p>" : null);
//
//        //Collection
//        Elements tbodies = document.select("table.collection").select("tbody");
//        applyCollections(tbodies, item, true);
//
//
//        return item;
//    }
//
//    private boolean applyName(Element main_title, Item item) {
//        if (main_title != null) {
//            Element h1 = main_title.selectFirst("h1");
//            if (h1 != null) {
//                if (item.getName() != null && !item.getName().isEmpty() && !item.getName().equals(h1.text())) {
//                    println("item.name", item.getName(), h1.text());
//                }
//                item.setName(h1.text());
//            }
//            return true;
//        } else {
//            System.out.println(ANSI_RED + "#main_title was not found" + ANSI_RESET);
//            return false;
//        }
//    }
//
//
//    private boolean applyCharacteristic(Element fiche_caracteristiques, Item item) {
//        //Features
//        HashMap<String, String> features = new HashMap<>();
//
//        if (fiche_caracteristiques != null) {
//            Elements rows = fiche_caracteristiques.select("tr");
//
//            for (Element row : rows) {
//                String feature = row.select("th").text();
//                //Number
//                if (feature.equals("Number")) continue;
//                if (feature.equals("References")) {
//                    String value = "";
//                    for (String part : row.select("td").text().split(" ")) {
//                        if (part.contains("#")) {
//                            value += part.replace(",", "") + "|";
//                        }
//                    }
//                    features.put(feature, value);
//                    continue;
//                }
//                String value = row.select("td").text();
//                value = value.replace("&nbsp", " ");
//                features.put(feature, value);
//            }
//        } else {
//            System.out.println(ANSI_RED + "#fiche_caracteristiques was not found" + ANSI_RESET);
//            return false;
//        }
//
//        for (String key : features.keySet()) {
//            String value = features.get(key);
//            if (key.equals("Issuer")) {
//
//                if(item.getIssuer() == null || !item.getIssuer().getName().equals(value)){
//                    Issuer issuer = serviceUtils.issuerService.findByName(value);
//                    if(issuer == null){
//                        issuer = new Issuer(value);
//                        System.out.println("New Issuer with name '" + issuer.getName() + "'");
//                    }
//                    item.setIssuer(issuer);
//                }
//
//            } else if (key.equals("Period")) {
//                if(item.getPeriod() == null || !item.getPeriod().getName().equals(value)){
//                    Period period = serviceUtils.periodService.findByName(value);
//                    if(period == null){
//                        period = new Period(value);
//                        System.out.println("New Period with name '" + period.getName() + "'");
//                    }
//                    item.setPeriod(period);
//                }
//            } else if (key.equals("Type")) {
//                if (value.equals("Standard circulation coin")) {
//                    item.setItemType(ITEM_TYPE.Standard_circulation_coin);
//                } else if (value.equals("Non-circulating coin")) {
//                    item.setItemType(ITEM_TYPE.Non_circulating_coin);
//                } else if (value.equals("Circulating commemorative coin")) {
//                    item.setItemType(ITEM_TYPE.Circulating_commemorative_coin);
//                } else if (value.equals("Pattern")) {
//                    item.setItemType(ITEM_TYPE.Pattern);
//                } else if (value.equals("Token")) {
//                    item.setItemType(ITEM_TYPE.Token);
//                } else {
//                    System.out.println(ANSI_RED + "Find some unknown item type: " + value + ANSI_RESET);
//                    return false;
//                }
//            } else if (key.equals("Year")) {
//                if (value.contains("(")) {
//                    item.setStartYear(Integer.parseInt(value.substring(0, value.indexOf(" "))));
//                    item.setStartYearGregorian(Integer.parseInt(value.substring(value.indexOf("(") + 1, value.indexOf(")"))));
//                } else {
//                    item.setStartYear(Integer.parseInt(value));
//                    item.setStartYearGregorian(Integer.parseInt(value));
//                }
//                item.setEndYear(-1);
//                item.setEndYearGregorian(-1);
//            } else if (key.equals("Years")) {
//                if (value.contains("(")) {
//                    String startYear = value.substring(0, value.indexOf("-"));
//                    String endYear = value.substring(value.indexOf("-") + 1, value.indexOf(" "));
//                    String startYearGreg = value.substring(value.indexOf("(") + 1, value.indexOf("-", value.indexOf("(")));
//                    String endYearGreg = value.substring(value.indexOf("-", value.indexOf("(")) + 1, value.indexOf(")"));
//                    item.setStartYear(Integer.parseInt(startYear));
//                    item.setEndYear(Integer.parseInt(endYear));
//                    item.setStartYearGregorian(Integer.parseInt(startYearGreg));
//                    item.setEndYearGregorian(Integer.parseInt(endYearGreg));
//                } else {
//                    String startYear = value.substring(0, value.indexOf("-"));
//                    String endYear = value.substring(value.indexOf("-") + 1);
//                    item.setStartYear(Integer.parseInt(startYear));
//                    item.setEndYear(Integer.parseInt(endYear));
//                    item.setStartYearGregorian(Integer.parseInt(startYear));
//                    item.setEndYearGregorian(Integer.parseInt(endYear));
//                }
//            } else if (key.equals("Value")) {
//                if(item.getValue() == null || !item.getValue().getName().equals(value)){
//                    Value val = serviceUtils.valueService.findByName(value);
//                    if(val == null){
//                        val = new Value(value);
//                        System.out.println("New Value with name '" + val.getName() + "'");
//                    }
//                    item.setValue(val);
//                }
//            } else if (key.equals("Calendar")) {
//                item.setCalendar(value);
//            } else if (key.equals("Currency")) {
//                if(item.getCurrency() == null || !item.getCurrency().getName().equals(value)){
//                    Currency currency = serviceUtils.currencyService.findByName(value);
//                    if(currency == null){
//                        currency = new Currency(value);
//                        System.out.println("New Currency with name '" + currency.getName() + "'");
//                    }
//                    item.setCurrency(currency);
//                }
//            } else if (key.equals("Composition")) {
//                item.setComposition(value);
//            } else if (key.equals("Shape")) {
//                item.setShape(value);
//            } else if (key.equals("Technique")) {
//                item.setTechnique(value);
//            } else if (key.equals("Orientation")) {
//                item.setOrientation(value);
//            } else if (key.equals("Demonetized")) {
//                item.setDemonetized(value);
//            } else if (key.equals("Weight")) {
//                item.setWeight(value);
//            } else if (key.equals("Diameter")) {
//                item.setDiameter(value);
//            } else if (key.equals("Thickness")) {
//                item.setThickness(value);
//            } else if (key.equals("References")) {
//                item.setReferences(value);
//            } else {
//                System.out.println(ANSI_RED + "Find new coin feature: " + key + " | " + value + ANSI_RESET);
//                return false;
//            }
//        }
//        return true;
//    }
//
//
//    private boolean applyTerritory(Element main_breadcrumb, Item item) {
//        if (main_breadcrumb != null) {
//            ArrayList<String> mainBreadcrumbArray = new ArrayList<>();
//            for (Element span : main_breadcrumb.children()) {
//                Element element = span.selectFirst("span[itemprop=\"name\"]");
//                if (element != null) {
//                    mainBreadcrumbArray.add(element.text());
//                }
//            }
//            Territory lastTerritory = null;
//            for (int i = 2; i < mainBreadcrumbArray.size(); i++) {
//                Territory territory = serviceUtils.territoryService.findByName(mainBreadcrumbArray.get(i));
//                if (territory == null) {
//                    territory = new Territory(mainBreadcrumbArray.get(i));
//                    if (lastTerritory != null) {
//                        territory.setParentTerritory(lastTerritory);
//                    }
//                    territory = serviceUtils.territoryService.save(territory);
//                }
//                lastTerritory = territory;
//            }
//
//            if (lastTerritory == null) {
//                System.out.println(ANSI_RED + "Territory if coin is null: " + item.getNumistaNumber() + ANSI_RESET);
//                return false;
//            }
//            item.setTerritory(lastTerritory);
//            return true;
//        }
//
//        System.out.println(ANSI_RED + "#main_breadcrumb was nod found" + ANSI_RESET);
//        return false;
//    }
//
//
//    private void applyCommemorativeDescription(List<Element> commIssueElementList, Item item) {
//        for (Element element : commIssueElementList) {
//            if (element.tag().equals(Tag.valueOf("p"))) {
//                if (element.text().contains("Series")) {
//                    Element href = element.selectFirst("a[href]");
//                    if (href != null) {
//                        CommIssueSeries commIssueSeries = serviceUtils.commIssueSeriesService.findByName(href.text());
//                        if (commIssueSeries == null) {
//                            commIssueSeries = new CommIssueSeries(href.text());
//                            commIssueSeries.setNumistaURL(href.attr("href"));
//                        }
//                        item.setCommIssueSeries(commIssueSeries);
//                    }
//                } else {
//                    String commIssueName = element.text();
//                    if (!commIssueName.isEmpty()) item.setCommIssueName(commIssueName);
//                }
//            }
//        }
//    }
//
//
//    private void applyItemSideDescription(List<Element> elements, ItemSide itemSide) {
//        for (Element element : elements) {
//            setItemPartProperty(itemSide, element);
//        }
//    }
//
//    private void setItemPartProperty(ItemSide itemSide, Element element) {
//        Element strong = element.getElementsByTag("strong").first();
//        if (strong != null) {
//            if (strong.text().contains("Script")) {
//                itemSide.setScript(element.text().replace("Script:", "").trim());
//            } else if (strong.text().contains("Lettering")) {
//                itemSide.setLettering(element.text().replace("Lettering:", "").trim());
//            } else if (strong.text().contains("Translation") && element.children().last() != null) {
//                itemSide.setTranslation(element.text().replace("Translation:", "").trim());
//            } else if (strong.text().contains("Engraver") && element.children().last() != null) {
//                itemSide.setEngraver(element.text().replace("Engraver:", "").trim());
//            } else if (strong.text().contains("Designer") && element.children().last() != null) {
//                itemSide.setDesigner(element.text().replace("Designer:", "").trim());
//            } else if (strong.text().contains("Unabridged legend") && element.children().last() != null) {
//                itemSide.setUnabridgedLegend(element.text().replace("Unabridged legend:", "").trim());
//            } else {
//                System.out.println(ANSI_RED + "Find new property for CoinSide: " + element.text() + ANSI_RESET);
//            }
//        } else if (element.tag().equals(Tag.valueOf("a"))) {
//            itemSide.setPhotoLink(element.attr("href"));
//        } else if (element.className().equals("mentions") ||
//                element.className().equals("tooltip") ||
//                element.text().isEmpty() || element.text().equals(" ")) {
//            return;
//        } else {
//            itemSide.setDescription(element.text());
//        }
//    }
//
//
//    private void applyMintDescription(List<Element> elements, Item item) {
//        for (Element element : elements) {
//            if (element.tag().equals(Tag.valueOf("table")) && element.id().equals("fiche_mint")) {
//                Elements tr = element.select("tr");
//                for (Element elem : tr) {
//                    setMint(item.getMints(), elem);
//                }
//            } else {
//                setMint(item.getMints(), element);
//                return;
//            }
//        }
//    }
//
//    public void setMint(Set<Mint> mints, Element element) {
//        String mintName = element.text();
//        if (mints.stream().filter(mint -> mint.getName().equals(mintName)).findFirst().orElse(null) == null) {
//
//            Element numistaURL = element.select("[href]").first();
//            if (numistaURL != null) {
//                String numURL = numistaURL.attr("href");
//                if (!numURL.isEmpty()) {
////                    Mint mint = serviceUtils.mintService.findByNumistaURL(numURL);
//                    Mint mint = serviceUtils.mintService.findByName(mintName);
//                    if (mint == null || !mints.contains(mint)) {
//                        mints.add(mint != null ? mint : new Mint(mintName, numURL));
//                    }
//                }
//            } else {
//                System.out.println("Find coin mint without url: " + mintName);
//            }
//        }
//    }
//
//
//    private void applyMarkDescription(List<Element> elements, Item item) {
//        for (Element element : elements) {
//            if (element.tag().equals(Tag.valueOf("table")) && element.id().equals("fiche_marks")) {
//                Elements tr = element.select("tr");
//                for (Element elem : tr) {
//                    setMark(item.getMarks(), elem);
//                }
//            } else {
//                setMark(item.getMarks(), element);
//                return;
//            }
//        }
//    }
//
//
//    public void setMark(Set<SpecifiedMint> mintmarks, Element element) {
//        String markName = element.text();
//        if (mintmarks.stream().filter(mint -> mint.getName().equals(markName)).findFirst().orElse(null) == null) {
//            Element numistaURL = element.select("[href]").first();
//            if (numistaURL != null) {
//                String numURL = numistaURL.attr("href");
//                if (!numURL.isEmpty()) {
////                    Mark mark = serviceUtils.mintService.findByNumistaURL(numURL);
//                    SpecifiedMint mintmark = null;
//                    if (mintmark == null || !mintmarks.contains(mintmark)) {
//                        mintmarks.add(mintmark != null ? mintmark : new SpecifiedMint(markName));
//                    }
//                }
//            } else {
//                System.out.println(ANSI_YELLOW + "Find coin mint without url: " + markName + ANSI_RESET);
//            }
//        }
//    }
//
//    private void applyPieceSideDescription(List<Element> elements, ItemSide itemSide) {
//        for (Element element : elements) {
//            setItemPartProperty(itemSide, element);
//        }
//    }
//
//
//    private boolean applyCollections(Elements elements, Item item, boolean cleanExists) {
//
//        if (cleanExists) {
//            serviceUtils.itemService.deleteExistingVariants(item);
//        }
//
//        for (Element tbody : elements) {
//            if (tbody.attr("style").contains("display:none")) {
//                continue;
//            }
//
//            Element tr = tbody.selectFirst("tr.date_row");
//            if (tr != null) {
//                Elements tds = tr.select("td");
//                String date = "";
//                String tirage = "";
//                String comment = "";
//                for (Element td : tds) {
//                    if (td.className().contains("date")) {
//                        date = td.text();
//                    } else if (td.className().contains("tirage")) {
//                        tirage = td.text();
//                    } else if (td.className().contains("comment")) {
//                        comment = td.text();
//                    }
//                }
//
//                Variant variant = new Variant();
//                variant.setDate(date);
////                variant.setTirage(tirage);
//                variant.setComment(comment);
//                item.getVariants().add(variant);
//            }
//        }
//        return true;
//    }
//
//
//    /**
//     * @param document
//     * @param numistaNumber
//     * @param descriptionPart - one of the COMMEMORATIVE_ISSUE, OBVERSE, REVERSE, EDGE, MINT, MINTS, COMMENTS
//     * @return
//     */
//    public ArrayList<Element> findDescriptionPart(String numistaNumber, Document document, String descriptionPart) {
//
//        //Description
//        Element ficheDescriptions = document.selectFirst("#fiche_descriptions");
//        if (ficheDescriptions == null) {
//            System.out.println(ANSI_RED + "Piece with number: " + numistaNumber + " without Description" + ANSI_RESET);
//            return null;
//        }
//
//        ArrayList<Element> elements = new ArrayList<>();
//
//        boolean catchPart = false;
//
//        for (Element element : ficheDescriptions.children()) {
//            if (element.tag().equals(Tag.valueOf("h3"))) {
//                if (catchPart) {
//                    break;
//                }
//                if (element.text().equals(descriptionPart)) {
//                    catchPart = true;
//                }
//            } else {
//                if (catchPart) elements.add(element);
//            }
//        }
//
//        return elements;
//    }
//
//
//    public ItemSide loadItemSide(String numistaNumber, String sideType) {
//
//        Document numistaPage = getNumistaPage(numistaNumber);
//
//        ItemSide itemSide = new ItemSide(sideType);
//
//        Element fiche_photo = numistaPage.selectFirst("#fiche_photo");
//        if (fiche_photo != null) {
//            Elements elements = fiche_photo.select("a[href]");
//            if (itemSide.getSideType().equals(ItemSide.OBVERSE) && elements.get(0) != null) {
//                itemSide.setPhotoLink(elements.get(0).attr("href"));
//            } else if (itemSide.getSideType().equals(ItemSide.REVERSE) && elements.get(1) != null) {
//                itemSide.setPhotoLink(elements.get(1).attr("href"));
//            }
//        }
//
//        //Description
//        ArrayList<Element> elements = findDescriptionPart(numistaNumber, numistaPage, itemSide.getSideType());
//        if (elements != null) {
//            applyItemSideDescription(elements, itemSide);
//        }
//
//        return itemSide;
//    }
//
//    public Item loadItemProperties(String numistaNumber, Item item) {
//
//        Document numistaPage = getNumistaPage(numistaNumber);
//
//        //fiche_descriptions
//        assert numistaPage != null;
//        ArrayList<Element> elements = findDescriptionPart(numistaNumber, numistaPage, COMMEMORATIVE_ISSUE);
//        applyCommemorativeDescription(elements, item);
//
//        // fiche_caracteristiques
//
//        Element fiche_caracteristiques = numistaPage.selectFirst("#fiche_caracteristiques");
//        if (!applyCharacteristic(fiche_caracteristiques, item)) {
//            return null;
//        }
//
//        Element main_breadcrumb = numistaPage.selectFirst("#main_breadcrumb");
//        // set Territories
//        if (!applyTerritory(main_breadcrumb, item)) {
//            return null;
//        }
//
//
//        return item;
//    }
//
//
//    private void println(String fieldName, String oldValue, String newValue) {
//        System.out.println("Change " + fieldName + " from '" + oldValue + "' to '" + newValue + "'");
//    }
//
//}
