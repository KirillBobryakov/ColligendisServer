package bkv.colligendis.utils.numista.item;


import bkv.colligendis.utils.numista.EditPageParser;
import bkv.colligendis.utils.numista.NumistaPartParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.stream.Streams;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class NumistaAllItemsParser {
    private static final String BASE_URL = "https://en.numista.com/catalogue/index.php";


    public NumistaAllItemsParser() {

    }

    public void fetchAndProcessCatalog(String issuerNumistaCode, ItemType itemType) {


        int page = 1;
        List<String> nids = new ArrayList<>();
        boolean stop = true;
        do{
            System.out.println("Reading page: " + page);
            String url = String.format("%s?e=%s&r=&st=%s&cat=y&p=%d", BASE_URL, issuerNumistaCode, itemType.getCode(), page);
       
    
            Document document = NumistaPartParser.loadPageByURL(url, false);
    
            List<Element> elements = document.select("div.description_piece");
    
            for(Element element : elements){
                String nid = element.selectFirst("a[href^=/catalogue/pieces]").attr("href").replace("/catalogue/pieces", "").replace(".html", "");
                if(nids.contains(nid)){
                    stop = false;
                    break;
                }
                nids.add(nid);
            }
            System.out.println("Found " + nids.size() + " items");
            page++;
            stop = false;
        } while(stop);

        // Streams.of("45015")
        nids.stream().map(nid -> EditPageParser
                       .create
                       .andThen(EditPageParser.loadNumistaPage)
                       .apply(nid))
               .filter(EditPageParser.isEditPageLoaded)
               .map(editPageParser -> EditPageParser
                       .loadNType
                       .andThen(EditPageParser.showMetrics)
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

        System.out.println(nids.size());
        System.out.println("end");
    }


} 
