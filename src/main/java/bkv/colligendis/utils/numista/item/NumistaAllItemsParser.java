package bkv.colligendis.utils.numista.item;

import bkv.colligendis.utils.ImageUtil;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.parser.PART_TYPE;
import bkv.colligendis.utils.numista.parser.PageParser;
import bkv.colligendis.utils.numista.parser.PartParser;

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

            Document document = PartParser.loadPageByURL(url, false);

            List<Element> elements = document.select("div.description_piece");

            if (elements.isEmpty()) {
                stop = false;
                break;
            }

            for (Element element : elements) {

                Element strong = element.selectFirst("strong");
                if (strong == null)
                    continue;

                Element a = strong.selectFirst("a[href^=/]");
                if (a == null)
                    continue;

                String nid = a.attr("href");
                if (nid.contains("pieces")) {
                    nid = nid.replace("/catalogue/pieces", "").replace(".html", "");
                } else if (nid.contains("exonumia")) {
                    nid = nid.replace("/catalogue/exonumia", "").replace(".html", "");
                } else if (nid.contains("note")) {
                    nid = nid.replace("/catalogue/note", "").replace(".html", "");
                } else if (nid.contains("/")) {
                    nid = nid.replace("/", "");
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

        PageParser.parse.accept(typesForParsing.stream().map(nType -> nType.nid));

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