package bkv.colligendis.utils;

import bkv.colligendis.database.entity.piece.ItemSide;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NumistaUtil {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    public static final String COMMEMORATIVE_ISSUE = "Commemorative issue";
    public static final String OBVERSE = "Obverse";
    public static final String REVERSE = "Reverse";
    public static final String EDGE = "Edge";
    public static final String COMMENTS = "Comments";
    public static final String MINT = "Mint";
    public static final String MINTS = "Mints";

    public static Document getNumistaPage(String numistaNumber) {
        Document document;
        try {
            document = Jsoup.connect("https://en.numista.com/catalogue/pieces" + numistaNumber + ".html").get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }


    public static ItemSide loadPieceSide(Document numistaPage, String sideType) {

        ItemSide pieceSide = new ItemSide(sideType);

        Element fiche_photo = numistaPage.selectFirst("#fiche_photo");
        if (fiche_photo != null) {
            Elements elements = fiche_photo.select("a[href]");
            if (pieceSide.getSideType().equals(ItemSide.OBVERSE) && elements.get(0) != null) {
                pieceSide.setPhotoLink(elements.get(0).attr("href"));
            } else if (pieceSide.getSideType().equals(ItemSide.REVERSE) && elements.get(1) != null) {
                pieceSide.setPhotoLink(elements.get(1).attr("href"));
            }
        }

        //Description
        Element ficheDescriptions = numistaPage.selectFirst("#fiche_descriptions");
        if (ficheDescriptions == null) {
            System.out.println(ANSI_RED + "Piece without Description" + ANSI_RESET);
            return null;
        }

        ArrayList<Element> elements = new ArrayList<>();


        String currentTitle = "";


        boolean catchPart = false;

        for (Element element : ficheDescriptions.children()) {
            if (element.tag().equals(Tag.valueOf("h3"))) {
                if (catchPart) {
                    break;
                }
                if (element.text().equals(pieceSide.getSideType())) {
                    catchPart = true;
                }
            } else {
                if (catchPart) elements.add(element);
            }
        }

        // Description
        applyPieceSideDescription(elements, pieceSide);

        return pieceSide;
    }

    private static void applyPieceSideDescription(List<Element> elements, ItemSide pieceSide) {
        for (Element element : elements) {
            setCoinPartProperty(pieceSide, element);
        }
    }

    private static void setCoinPartProperty(ItemSide pieceSide, Element element) {
        Element strong = element.getElementsByTag("strong").first();
        if (strong != null) {
            if (strong.text().contains("Script")) {
                pieceSide.setScript(element.text().replace("Script:", "").trim());
            } else if (strong.text().contains("Lettering")) {
                pieceSide.setLettering(element.text().replace("Lettering:", "").trim());
            } else if (strong.text().contains("Translation") && element.children().last() != null) {
                pieceSide.setTranslation(element.text().replace("Translation:", "").trim());
            } else if (strong.text().contains("Engraver") && element.children().last() != null) {
                pieceSide.setEngraver(element.text().replace("Engraver:", "").trim());
            } else if (strong.text().contains("Designer") && element.children().last() != null) {
                pieceSide.setDesigner(element.text().replace("Designer:", "").trim());
            } else if (strong.text().contains("Unabridged legend") && element.children().last() != null) {
                pieceSide.setUnabridgedLegend(element.text().replace("Unabridged legend:", "").trim());
            } else {
                System.out.println(ANSI_RED + "Find new property for CoinSide: " + element.text() + ANSI_RESET);
            }
        } else if (element.tag().equals(Tag.valueOf("a"))) {
            pieceSide.setPhotoLink(element.attr("href"));
        } else if (element.className().equals("mentions") ||
                element.className().equals("tooltip") ||
                element.text().isEmpty() || element.text().equals(" ")) {
            return;
        } else {
            pieceSide.setDescription(element.text());
        }
    }
}
