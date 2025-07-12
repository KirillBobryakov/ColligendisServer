package bkv.colligendis;

import bkv.colligendis.services.NumistaServices;
import bkv.colligendis.utils.N4JUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableTransactionManagement
public class Application {

    public final NumistaServices numistaServices;

    public Application(NumistaServices numistaServices) {
        this.numistaServices = numistaServices;
        N4JUtil.InitInstance(numistaServices);

        // Stream.of("336784").map(nid -> EditPageParser.create
        // .andThen(EditPageParser.loadNumistaPage)
        // .apply(nid))
        // .filter(EditPageParser.isEditPageLoaded)
        // .map(editPageParser -> EditPageParser.loadNType
        // .andThen(EditPageParser.hideMetrics)
        // .andThen(EditPageParser.titleParser)
        // .andThen(EditPageParser.collectibleTypeParser)
        // .andThen(EditPageParser.issuerParser)
        // .andThen(EditPageParser.rulerParser)
        // .andThen(EditPageParser.issuingEntityParser)
        // .andThen(EditPageParser.currencyParser)
        // .andThen(EditPageParser.denominationParser)
        // .andThen(EditPageParser.commemoratedEventParser)
        // .andThen(EditPageParser.seriesParser)
        // .andThen(EditPageParser.demonetizedParser)
        // .andThen(EditPageParser.referenceNumberParser)
        // .andThen(EditPageParser.mintageParser)
        // .andThen(EditPageParser.technicalDataParser)
        // .andThen(EditPageParser.obverseParser)
        // .andThen(EditPageParser.reverseParser)
        // .andThen(EditPageParser.edgeParser)
        // .andThen(EditPageParser.watermarkParser)
        // .andThen(EditPageParser.mintsParser)
        // .andThen(EditPageParser.saveNType)
        // .apply(editPageParser))
        // .forEach(EditPageParser.finalyInfo);

        // NumistaIssuersResponse numistaIssuer = NumistaPartParser.fetchAndParseJson(
        // "https://en.numista.com/catalogue/search_issuers.php?ct=coin&q=German%20Democratic%20Republic",
        // true, NumistaIssuersResponse.class);

        // NumistaAllIssuersParser numistaAllIssuersParser = new
        // NumistaAllIssuersParser();
        // numistaAllIssuersParser.processIssuersJsons2();

        // NumistaAllItemsParser numistaAllItemsParser = new NumistaAllItemsParser();
        // numistaAllItemsParser.fetchAndProcessCatalog("zambie", ItemType.Coin);

        // N4JUtil.getInstance().numistaService.nTypeService.findAll().forEach(nType ->
        // {
        // ImageUtil.saveNumistaImage(nType, PART_TYPE.OBVERSE);
        // ImageUtil.saveNumistaImage(nType, PART_TYPE.REVERSE);
        // ImageUtil.saveNumistaImage(nType, PART_TYPE.EDGE);
        // ImageUtil.saveNumistaImage(nType, PART_TYPE.WATERMARK);
        // });

        // https://en.numista.com/catalogue/index.php?e=germany&r=&st=148&cat=y&im1=&im2=&ru=&ie=&ca=3&no=&v=&a=&dg=&i=&b=&m=&f=&t=&t2=&w=&mt=&u=&g=&c=&wi=&sw=
        // https://en.numista.com/catalogue/index.php?e=germany&r=&st=148&cat=y&im1=&im2=&ru=&ie=&ca=3&no=&v=&a=&dg=&i=&b=&m=&f=&t=&t2=&w=&mt=&u=&g=&c=&wi=&sw=&q=200
        // https://en.numista.com/catalogue/index.php?e=germany&r=&st=147&cat=y&im1=&im2=&ru=&ie=&ca=3&no=&v=&a=&dg=&i=&b=&m=&f=&t=&t2=&w=&mt=&u=&g=&c=&wi=&sw=&p=2
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
