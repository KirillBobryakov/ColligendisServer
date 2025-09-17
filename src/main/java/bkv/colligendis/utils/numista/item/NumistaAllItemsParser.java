package bkv.colligendis.utils.numista.item;

import bkv.colligendis.utils.ImageUtil;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.EditPageParser;
import bkv.colligendis.utils.numista.NumistaPartParser;
import bkv.colligendis.utils.numista.PART_TYPE;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class NumistaAllItemsParser {
    private static final String BASE_URL = "https://en.numista.com/catalogue/index.php";

    public NumistaAllItemsParser() {

    }

    public void fetchAndProcessCatalog(String issuerNumistaCode, String collectibleType) {

        int page = 1;
        List<TypeForParsing> typesForParsing = new ArrayList<>();
        boolean stop = true;
        int number = 0;
        do {
            System.out.println("Reading page: " + page);
            String url = String.format("%s?e=%s&r=&st=%s&cat=y&p=%d", BASE_URL, issuerNumistaCode,
                    collectibleType,
                    page);

            System.err.println(url);

            Document document = NumistaPartParser.loadPageByURL(url, false);

            List<Element> elements = document.select("div.description_piece");

            if (elements.isEmpty()) {
                stop = false;
                break;
            }

            for (Element element : elements) {
                Element a = element.selectFirst("a[href^=/catalogue/]");
                if (a == null)
                    continue;

                String nid = a.attr("href");
                if (nid.contains("pieces")) {
                    nid = nid.replace("/catalogue/pieces", "").replace(".html", "");
                } else if (nid.contains("exonumia")) {
                    nid = nid.replace("/catalogue/exonumia", "").replace(".html", "");
                } else if (nid.contains("note")) {
                    nid = nid.replace("/catalogue/note", "").replace(".html", "");
                }

                final String finalNid = nid;

                if (typesForParsing.stream().anyMatch(t -> t.nid.equals(finalNid))) {
                    stop = false;
                    break;
                }
                typesForParsing.add(new TypeForParsing(nid, page, number));
                number++;
            }
            System.out.println("Found " + typesForParsing.size() + " items");
            page++;
        } while (stop);

        System.out.println("Total items for parsing: " + typesForParsing.size());

        typesForParsing.stream()
                .map(t -> {
                    System.out.println("Parsing " + t.number + " from page: " + t.page + " nid: " + t.nid);
                    return EditPageParser.create
                            .andThen(EditPageParser.loadNumistaPage)
                            .apply(t.nid);
                })
                .filter(EditPageParser.isEditPageLoaded)
                .map(editPageParser -> EditPageParser.loadNType
                        .andThen(EditPageParser.hideMetrics)
                        .andThen(EditPageParser.titleParser)
                        .andThen(EditPageParser.collectibleTypeParser)
                        .andThen(EditPageParser.issuerParser)
                        .andThen(EditPageParser.rulerParser)
                        .andThen(EditPageParser.issuingEntityParser)
                        .andThen(EditPageParser.currencyParser)
                        .andThen(EditPageParser.denominationParser)
                        .andThen(EditPageParser.commemoratedEventParser)
                        .andThen(EditPageParser.seriesParser)
                        .andThen(EditPageParser.demonetizedParser)
                        .andThen(EditPageParser.referenceNumberParser)
                        .andThen(EditPageParser.mintageParser)
                        .andThen(EditPageParser.technicalDataParser)
                        .andThen(EditPageParser.obverseParser)
                        .andThen(EditPageParser.reverseParser)
                        .andThen(EditPageParser.edgeParser)
                        .andThen(EditPageParser.watermarkParser)
                        .andThen(EditPageParser.mintsParser)
                        .andThen(EditPageParser.saveNType)
                        .apply(editPageParser))
                .forEach(EditPageParser.finalyInfo);

        typesForParsing.forEach(nType -> {
            ImageUtil.saveNumistaImage(N4JUtil.getInstance().numistaService.nTypeService.findByNid(nType.nid),
                    PART_TYPE.OBVERSE);
            ImageUtil.saveNumistaImage(N4JUtil.getInstance().numistaService.nTypeService.findByNid(nType.nid),
                    PART_TYPE.REVERSE);
            ImageUtil.saveNumistaImage(N4JUtil.getInstance().numistaService.nTypeService.findByNid(nType.nid),
                    PART_TYPE.EDGE);
            ImageUtil.saveNumistaImage(N4JUtil.getInstance().numistaService.nTypeService.findByNid(nType.nid),
                    PART_TYPE.WATERMARK);
        });
    }

}

class TypeForParsing {

    String nid;
    int page;
    int number;

    public TypeForParsing(String nid, int page, int number) {
        this.nid = nid;
        this.page = page;
        this.number = number;
    }

}