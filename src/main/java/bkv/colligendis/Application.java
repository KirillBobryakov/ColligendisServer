package bkv.colligendis;

import bkv.colligendis.services.NumistaServices;
import bkv.colligendis.utils.N4JUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class Application {

    public final NumistaServices numistaServices;


    public Application(NumistaServices numistaServices) {
        this.numistaServices = numistaServices;
        N4JUtil.InitInstance(numistaServices);




        // System.setProperty("https.protocols", "TLSv1.3");


//        try {
//            Issuer issue = N4JUtil.getInstance().numistaService.issuerService.findIssuerByCodeOrName("essen_notgeld", "");
//            RulerParser.parseRulersByIssuerCodeFromPHPRequest(issue);
//        } catch (UniqueEntityException e) {
//            throw new RuntimeException(e);
//        }


        //MATCH (n:NTYPE {nid:'1004'}) CALL apoc.path.subgraphNodes(n, {minLevel:1}) YIELD node RETURN node
        
        
        
        // Streams.of("225462", "1004", "23", "51857", "398555", "202201")
        // N4JUtil.getInstance().numistaService.nTypeService.findAllNidsOfNTypes().stream()
        //        .map(nid -> EditPageParser
        //                .create
        //                .andThen(EditPageParser.loadNumistaPage)
        //                .apply(nid))
        //        .filter(EditPageParser.isEditPageLoaded)
        //        .map(editPageParser -> EditPageParser
        //                .loadNType
        //                .andThen(EditPageParser.showMetrics)
        //                .andThen(EditPageParser.titleParser)
        //                .andThen(EditPageParser.issuerParser)
        //                .andThen(EditPageParser.rulerParser)
        //                .andThen(EditPageParser.issuingEntityParser)
        //                .andThen(EditPageParser.currencyParser)
        //                .andThen(EditPageParser.denominationParser)
        //                .andThen(EditPageParser.collectibleTypeParser)
        //                .andThen(EditPageParser.commemoratedEventParser)
        //                .andThen(EditPageParser.seriesParser)
        //                .andThen(EditPageParser.demonetizedParser)
        //                .andThen(EditPageParser.referenceNumberParser)
        //                .andThen(EditPageParser.mintageParser)
        //                .andThen(EditPageParser.technicalDataParser)
        //                .andThen(EditPageParser.obverseParser)
        //                .andThen(EditPageParser.reverseParser)
        //                .andThen(EditPageParser.edgeParser)
        //                .andThen(EditPageParser.watermarkParser)
        //                .andThen(EditPageParser.mintsParser)
        //                .andThen(EditPageParser.saveNType)
        //                .apply(editPageParser))
        //        .forEach(EditPageParser.finalyInfo);



        // NumistaIssuersResponse numistaIssuer = NumistaPartParser.fetchAndParseJson("https://en.numista.com/catalogue/search_issuers.php?ct=coin&q=German%20Democratic%20Republic", true, NumistaIssuersResponse.class);


        // NumistaAllIssuersParser numistaAllIssuersParser = new NumistaAllIssuersParser();
        // numistaAllIssuersParser.processIssuersJsons2();


        // NumistaAllItemsParser numistaAllItemsParser = new NumistaAllItemsParser();
        // numistaAllItemsParser.fetchAndProcessCatalog("zambie", ItemType.Coin);
        // System.out.println(numistaIssuer.toString());


        // N4JUtil.getInstance().numistaService.nTypeService.findAll().forEach(nType -> {
        //     ImageUtil.saveNumistaImage(nType, PART_TYPE.OBVERSE);
        //     ImageUtil.saveNumistaImage(nType, PART_TYPE.REVERSE);
        //     ImageUtil.saveNumistaImage(nType, PART_TYPE.EDGE);
        //     ImageUtil.saveNumistaImage(nType, PART_TYPE.WATERMARK);
        // });

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
