//package bkv.colligendis.utils;
//
//import bkv.colligendis.database.entity.piece.PieceSide;
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
//public class NumistaLoader {
//
//
//    public final ServiceUtils serviceUtils;
//    private static NumistaLoader instance;
//
//    public NumistaLoader(ServiceUtils serviceUtils) {
//        this.serviceUtils = serviceUtils;
//    }
//
//    public static synchronized NumistaLoader getInstance(ServiceUtils serviceUtils) {
//        if(instance == null){
//            instance = new NumistaLoader(serviceUtils);
//        }
//        return instance;
//    }
//
//    public static final String COMMEMORATIVE_ISSUE = "Commemorative issue";
//    public static final String OBVERSE = "Obverse";
//    public static final String REVERSE = "Reverse";
//    public static final String EDGE = "Edge";
//    public static final String COMMENTS = "Comments";
//    public static final String MINT = "Mint";
//    public static final String MINTS = "Mints";
//
//
//
//    private boolean applyName(Element main_title, PieceInformation pieceInformation){
//        if(main_title != null){
//            Element h1 = main_title.selectFirst("h1");
//            if(h1 != null) pieceInformation.setName(h1.text());
//            return true;
//        } else {
//            System.out.println("#main_title was not found");
//            return false;
//        }
//    }
//
//    public static final String COINS = "Coins";
//    public static final String BANKNOTES = "Banknotes";
//    public static final String EXONUMIA = "Exonumia";
//
//    private boolean checkPieceType(Element main_breadcrumb, String pieceType){
//        if(main_breadcrumb != null){
//            ArrayList<String> mainBreadcrumbArray = new ArrayList<>();
//            for(Element span : main_breadcrumb.children()){
//                Element item = span.selectFirst("span[itemprop=\"name\"]");
//                if(item != null){
//                    mainBreadcrumbArray.add(item.text());
//                }
//            }
//            if(mainBreadcrumbArray.size() > 1 && mainBreadcrumbArray.get(1) != null){
//                return mainBreadcrumbArray.get(1).equals(pieceType);
//            }
//        }
//        return false;
//    }
//
//    private boolean applyCharacteristic(Element fiche_caracteristiques, PieceInformation pieceInformation){
//        //Features
//        HashMap<String, String> features = new HashMap<>();
//        if(fiche_caracteristiques != null){
//            Elements rows = fiche_caracteristiques.select("tr");
//
//            for (Element row : rows){
//                String feature = row.select("th").text();
//                //Number
//                if(feature.equals("Number")) continue;
//                if(feature.equals("References")){
//                    String value = "";
//                    for(String part : row.select("td").text().split(" ")){
//                        if(part.contains("#")){
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
//            System.out.println("#fiche_caracteristiques was not found");
//            return false;
//        }
//
//        for(String key : features.keySet()){
//            String value = features.get(key);
//            if(key.equals("Issuer")){
//                if(pieceInformation.getIssuer() == null || !pieceInformation.getIssuer().getName().equals(value)){
//                    Issuer issuer = serviceUtils.issuerService.findByName(value);
//                    pieceInformation.setIssuer(issuer != null ? issuer : new Issuer(value));
//                }
//            } else if(key.equals("Period")){
//                if(pieceInformation.getPeriod() == null || !pieceInformation.getPeriod().getName().equals(value)){
//                    Period period = serviceUtils.periodService.findByName(value);
//                    pieceInformation.setPeriod(period != null ? period : new Period(value));
//                }
//            } else if(key.equals("Type")){
//                if(pieceInformation instanceof CoinInformation){
//                    if(value.equals("Standard circulation coin")){
//                        ((CoinInformation) pieceInformation).setCoinType(COIN_TYPE.Standard_circulation_coin);
//                    } else if(value.equals("Non-circulating coin")){
//                        ((CoinInformation) pieceInformation).setCoinType(COIN_TYPE.Non_circulating_coin);
//                    } else if(value.equals("Circulating commemorative coin")){
//                        ((CoinInformation) pieceInformation).setCoinType(COIN_TYPE.Circulating_commemorative_coin);
//                    } else if(value.equals("Pattern")){
//                        ((CoinInformation) pieceInformation).setCoinType(COIN_TYPE.Pattern);
//                    } else {
//                        System.out.println("Find some unknown coin type: " + value);
//                    }
//                }
//            } else if(key.equals("Year")){
//                if(value.contains("(")){
//                    pieceInformation.setStartYear(Integer.parseInt(value.substring(0, value.indexOf(" "))));
//                    pieceInformation.setStartYearGregorian(Integer.parseInt(value.substring(value.indexOf("(") + 1, value.indexOf(")"))));
//                } else {
//                    pieceInformation.setStartYear(Integer.parseInt(value));
//                    pieceInformation.setStartYearGregorian(Integer.parseInt(value));
//                }
//                pieceInformation.setEndYear(-1);
//                pieceInformation.setEndYearGregorian(-1);
//            } else if(key.equals("Years")){
//                if(value.contains("(")){
//                    String startYear = value.substring(0, value.indexOf("-"));
//                    String endYear = value.substring(value.indexOf("-") + 1, value.indexOf(" "));
//                    String startYearGreg = value.substring(value.indexOf("(") + 1, value.indexOf("-", value.indexOf("(")));
//                    String endYearGreg = value.substring(value.indexOf("-", value.indexOf("(")) + 1, value.indexOf(")"));
//                    pieceInformation.setStartYear(Integer.parseInt(startYear));
//                    pieceInformation.setEndYear(Integer.parseInt(endYear));
//                    pieceInformation.setStartYearGregorian(Integer.parseInt(startYearGreg));
//                    pieceInformation.setEndYearGregorian(Integer.parseInt(endYearGreg));
//                } else {
//                    String startYear = value.substring(0, value.indexOf("-"));
//                    String endYear = value.substring(value.indexOf("-") + 1);
//                    pieceInformation.setStartYear(Integer.parseInt(startYear));
//                    pieceInformation.setEndYear(Integer.parseInt(endYear));
//                    pieceInformation.setStartYearGregorian(Integer.parseInt(startYear));
//                    pieceInformation.setEndYearGregorian(Integer.parseInt(endYear));
//                }
//            } else if(key.equals("Value")){
//                if(pieceInformation.getValue() == null || !pieceInformation.getValue().getName().equals(value)){
//                    Value val = serviceUtils.valueService.findByName(value);
//                    pieceInformation.setValue(val != null ? val : new Value(value));
//                }
//            } else if(key.equals("Calendar")){
//                pieceInformation.setCalendar(value);
//            } else if(key.equals("Currency")){
//                if(pieceInformation.getCurrency() == null || !pieceInformation.getCurrency().getName().equals(value)){
//                    Currency currency = serviceUtils.currencyService.findByName(value);
//                    pieceInformation.setCurrency(currency != null ? currency : new Currency(value));
//                }
//            } else if(key.equals("Composition")){
//                pieceInformation.setComposition(value);
//            } else if(key.equals("Shape")){
//                pieceInformation.setShape(value);
//            } else if(key.equals("Technique")){
//                pieceInformation.setTechnique(value);
//            } else if(key.equals("Orientation")){
//                pieceInformation.setOrientation(value);
//            } else if(key.equals("Demonetized")){
//                pieceInformation.setDemonetized(value);
//            } else if(key.equals("Weight")){
//                pieceInformation.setWeight(value);
//            } else if(key.equals("Diameter")){
//                pieceInformation.setDiameter(value);
//            } else if(key.equals("Thickness")){
//                pieceInformation.setThickness(value);
//            } else if(key.equals("References")){
//                pieceInformation.setReferences(value);
//            } else {
//                System.out.println("Find new coin feature: " + key + " | " + value);
//                return false;
//            }
//        }
//        return true;
//    }
//
//
//    private boolean applyTerritory(Element main_breadcrumb, PieceInformation pieceInformation){
//        if(main_breadcrumb != null){
//            ArrayList<String> mainBreadcrumbArray = new ArrayList<>();
//            for(Element span : main_breadcrumb.children()){
//                Element item = span.selectFirst("span[itemprop=\"name\"]");
//                if(item != null){
//                    mainBreadcrumbArray.add(item.text());
//                }
//            }
//            Territory lastTerritory = null;
//            for(int i = 2; i < mainBreadcrumbArray.size(); i++){
//                Territory territory = serviceUtils.territoryService.findByName(mainBreadcrumbArray.get(i));
//                if(territory == null) {
//                    territory = new Territory(mainBreadcrumbArray.get(i));
//                    if(lastTerritory != null){
//                        territory.setParentTerritory(lastTerritory);
//                    }
//                    territory = serviceUtils.territoryService.save(territory);
//                }
//                lastTerritory = territory;
//            }
//
//            if(lastTerritory == null) {
//                System.out.println("Territory if coin is null: " + pieceInformation.getNumistaNumber());
//                return false;
//            }
//            pieceInformation.setTerritory(lastTerritory);
//            return true;
//        }
//        System.out.println("#main_breadcrumb was nod found");
//        return false;
//    }
//
//    private void applyPieceSideDescription(List<Element> elements, PieceSide pieceSide){
//        for(Element element : elements){
//            setCoinPartProperty(pieceSide, element);
//        }
//    }
//
//    private void applyMintDescription(List<Element> elements, PieceInformation pieceInformation){
//        for(Element element : elements){
//            if(element.tag().equals(Tag.valueOf("table")) && element.id().equals("fiche_mint")){
//                Elements tr = element.select("tr");
//                for(Element elem : tr){
//                    setMint(pieceInformation.getMints(), elem);
//                }
//            } else {
//                setMint(pieceInformation.getMints(), element);
//                return;
//            }
//        }
//    }
//
//
//    private void applyCommemorativeDescription(List<Element> commIssueElementList, PieceInformation pieceInformation){
//        for(Element element : commIssueElementList){
//            if(element.tag().equals(Tag.valueOf("p"))){
//                if(element.text().contains("Series")) {
//                    Element href = element.selectFirst("a[href]");
//                    if(href != null){
//                        CommIssueSeries commIssueSeries = serviceUtils.commIssueSeriesService.findByName(href.text());
//                        if(commIssueSeries == null){
//                            commIssueSeries = new CommIssueSeries(href.text());
//                            commIssueSeries.setNumistaURL(href.attr("href"));
//                        }
//                        pieceInformation.setCommIssueSeries(commIssueSeries);
//                    }
//                } else {
//                    String commIssueName = element.text();
//                    if(!commIssueName.isEmpty()) pieceInformation.setCommIssueName(commIssueName);
//                }
//            }
//        }
//    }
//
//    private boolean applyCollections(Elements elements, PieceInformation pieceInformation, boolean cleanExists){
//
//        if(cleanExists){
//            if(pieceInformation instanceof CoinInformation){
//                serviceUtils.coinInformationService.deleteExistCoinVariants(((CoinInformation) pieceInformation));
//            }
//        }
//
//        for (Element tbody : elements){
//            if(tbody.attr("style").contains("display:none")){
//                continue;
//            }
//
//            Element tr = tbody.selectFirst("tr.date_row");
//            if(tr != null){
//                Elements tds = tr.select("td");
//                String date = "";
//                String tirage = "";
//                String comment = "";
//                for(Element td : tds){
//                    if(td.className().contains("date")){
//                        date = td.text();
//                    } else if(td.className().contains("tirage")){
//                        tirage = td.text();
//                    } else if(td.className().contains("comment")){
//                        comment = td.text();
//                    }
//                }
//
//                CoinVariant coinVariant = new CoinVariant();
//                coinVariant.setDate(date);
//                coinVariant.setTirage(tirage);
//                coinVariant.setComment(comment);
//                pieceInformation.getCoinVariants().add(coinVariant);
//            }
//        }
//        return true;
//    }
//
//
//
//    public static PieceSide loadPieceSide(int numistaNumber, String sideType){
//        PieceSide pieceSide = new PieceSide(sideType);
//
//
//        return pieceSide;
//    }
//
//
//    public CoinInformation loadNumistaPiece(String numistaNumber){
//
//        Document document = null;
//        try {
//            document = Jsoup.connect("https://en.numista.com/catalogue/pieces" + numistaNumber + ".html").get();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        //Type
//        Element main_breadcrumb = document.selectFirst("#main_breadcrumb");
//        CoinInformation coinInformation = null;
//
//        if(checkPieceType(main_breadcrumb, COINS)){
//            System.out.println("Numista piece with number : " + numistaNumber + " is COIN");
//            coinInformation = loadCoinInformation(document, numistaNumber);
//            if(coinInformation != null) serviceUtils.coinInformationService.save(coinInformation);
//        } else if(checkPieceType(main_breadcrumb, BANKNOTES)){
//            System.out.println("Numista piece with number : " + numistaNumber + " is BANKNOTE");
//        } else if(checkPieceType(main_breadcrumb, EXONUMIA)){
//            System.out.println("Numista piece with number : " + numistaNumber + " is EXONUMIA");
//        }
//
//        return coinInformation;
//    }
//
//    private CoinInformation loadCoinInformation(Document document, String numistaNumber){
//
//        CoinInformation coinInformation = serviceUtils.coinInformationService.findByNumistaNumber(numistaNumber);
//        if(coinInformation == null) {
//            coinInformation = new CoinInformation();
//            coinInformation.setNumistaNumber(numistaNumber);
//        }
//
//        //Name
//        if(!applyName(document.selectFirst("#main_title"), coinInformation)){
//            return null;
//        }
//
//
//        //Features
//        Element fiche_caracteristiques = document.selectFirst("#fiche_caracteristiques");
//        if(!applyCharacteristic(fiche_caracteristiques, coinInformation)){
//            return null;
//        }
//
//        //Type
//        Element main_breadcrumb = document.selectFirst("#main_breadcrumb");
//
//        // set Territories
//        if(!applyTerritory(main_breadcrumb, coinInformation)){
//            return null;
//        }
//
//        PieceSide obverse = coinInformation.getObverse() != null ? coinInformation.getObverse() : new PieceSide(SIDE_TYPE.Obverse);
//        PieceSide reverse = coinInformation.getReverse() != null ? coinInformation.getReverse() : new PieceSide(SIDE_TYPE.Reverse);
//        PieceSide edge = coinInformation.getEdge() != null ? coinInformation.getEdge() : new PieceSide(SIDE_TYPE.Edge);
//        coinInformation.setObverse(obverse);
//        coinInformation.setReverse(reverse);
//        coinInformation.setEdge(edge);
//
//
//
//        //Reverse and Obverse photos
//        Element fiche_photo = document.selectFirst("#fiche_photo");
//
//        if(fiche_photo != null){
//            Elements elements = fiche_photo.select("a[href]");
//            if(elements.get(0) != null){
//                obverse.setPhotoLink(elements.get(0).attr("href"));
//            }
//            if(elements.get(1) != null){
//                reverse.setPhotoLink(elements.get(1).attr("href"));
//            }
//        }
//
//        //Description
//        Element ficheDescriptions = document.selectFirst("#fiche_descriptions");
//        if(ficheDescriptions == null){
//            System.out.println("Piece with number: " + numistaNumber + " without Description");
//            return null;
//        }

//        ArrayList<Element> commIssueElements = new ArrayList<>();
//        ArrayList<Element> obverseElements = new ArrayList<>();
//        ArrayList<Element> reverseElements = new ArrayList<>();
//        ArrayList<Element> edgeElements = new ArrayList<>();
//        ArrayList<Element> mintElements = new ArrayList<>();
//        ArrayList<Element> commentsElements = new ArrayList<>();
//
//        String currentDescr = "";
//        for(Element element : ficheDescriptions.children()){
//            if(element.tag().equals(Tag.valueOf("h3"))){
//                switch (element.text()) {
//                    case COMMEMORATIVE_ISSUE, OBVERSE, REVERSE, EDGE, MINT, MINTS, COMMENTS, "See also" -> currentDescr = element.text();
//                    default -> {
//                        System.out.println("Piece with number: " + numistaNumber + " find new Part of Description: " + element.text());
//                        return null;
//                    }
//                }
//            } else if(currentDescr.equals(COMMEMORATIVE_ISSUE)){
//                commIssueElements.add(element);
//            } else if(currentDescr.equals(OBVERSE)){
//                obverseElements.add(element);
//            } else if(currentDescr.equals(REVERSE)){
//                reverseElements.add(element);
//            } else if(currentDescr.equals(EDGE)){
//                edgeElements.add(element);
//            } else if(currentDescr.equals(MINT) || currentDescr.equals(MINTS)){
//                mintElements.add(element);
//            }
//        }
//
//        // Commemorative Description
//        applyCommemorativeDescription(commIssueElements, coinInformation);
//        // Obverse Description
//        applyPieceSideDescription(obverseElements, coinInformation.getObverse());
//        // Reverse Description
//        applyPieceSideDescription(reverseElements, coinInformation.getReverse());
//        // Edge Description
//        applyPieceSideDescription(edgeElements, coinInformation.getEdge());
//
//        // Mint Description
//        applyMintDescription(mintElements, coinInformation);
//
//        //Comments
//        Element fiche_comments = document.selectFirst("#fiche_comments");
//        coinInformation.setHtmlComments(fiche_comments != null ? "<p id=\"fiche_comments\">" + fiche_comments.html() + "</p>" : null);
//
//        //Collection
//        Elements tbodies = document.select("table.collection").select("tbody");
//        applyCollections(tbodies, coinInformation, true);
//
//        return coinInformation;
//    }
//
//
//    public void setCoinPartProperty(PieceSide pieceSide, Element element){
//        Element strong = element.getElementsByTag("strong").first();
//        if(strong != null) {
//            if (strong.text().contains("Script")) {
//                pieceSide.setScript(element.text().replace("Script:", "").trim());
//            } else if (strong.text().contains("Lettering")) {
//                pieceSide.setLettering(element.text().replace("Lettering:", "").trim());
//            } else if (strong.text().contains("Translation") && element.children().last() != null) {
//                pieceSide.setTranslation(element.text().replace("Translation:", "").trim());
//            } else if (strong.text().contains("Engraver") && element.children().last() != null) {
//                pieceSide.setEngraver(element.text().replace("Engraver:", "").trim());
//            } else if (strong.text().contains("Designer") && element.children().last() != null) {
//                pieceSide.setDesigner(element.text().replace("Designer:", "").trim());
//            } else if (strong.text().contains("Unabridged legend") && element.children().last() != null) {
//                pieceSide.setUnabridgedLegend(element.text().replace("Unabridged legend:", "").trim());
//            } else {
//                System.out.println("Find new property for CoinSide: " + element.text());
//            }
//        } else if(element.tag().equals(Tag.valueOf("a"))){
//            pieceSide.setPhotoLink(element.attr("href"));
//        } else if(element.className().equals("mentions") ||
//                element.className().equals("tooltip") ||
//                element.text().isEmpty() || element.text().equals(" ")){
//            return;
//        } else {
//            pieceSide.setDescription(element.text());
//        }
//    }
//
//    public void setMint(Set<Mint> mints, Element element){
//        String mintName = element.text();
//        if(mints.stream().filter(mint -> mint.getName().equals(mintName)).findFirst().orElse(null) == null){
//            Element numistaURL = element.select("[href]").first();
//            if(numistaURL != null){
//                String numURL = numistaURL.attr("href");
//                if(!numURL.isEmpty()){
//                    Mint mint = serviceUtils.mintService.findByNumistaURL(numURL);
//                    if(mint == null || !mints.contains(mint)){
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
//
//
//}
