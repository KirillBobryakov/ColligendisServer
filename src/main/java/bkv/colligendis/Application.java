package bkv.colligendis;

import bkv.colligendis.services.MeshokServices;
import bkv.colligendis.services.NumistaServices;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.parser.PageParser;

import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
// @EnableTransactionManagement
public class Application {

    public final NumistaServices numistaServices;
    public final MeshokServices meshokServices;

    public Application(NumistaServices numistaServices, MeshokServices meshokServices) {

        this.numistaServices = numistaServices;
        this.meshokServices = meshokServices;
        N4JUtil.InitInstance(numistaServices, meshokServices);

        // PageParser.parse.accept(Stream.of("209129"));

        // EditPageParser.parse.accept(Stream.of("209129"));

        // Stream.of("209129").map(nid -> EditPageParser.create
        // .andThen(EditPageParser.loadNumistaPage)
        // .apply(nid))
        // .filter(EditPageParser.isEditPageLoaded)
        // .map(editPageParser -> EditPageParser.loadNType
        // .andThen(EditPageParser.showMetrics)
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

        PageParser.parse.accept(Stream.of("209130", "20930"));
        // PageParser.parse.accept(Stream.of("20930"));
        // NumistaParser numistaParser1 = new NumistaParser("209130", true);
        // NumistaParser numistaParser2 = new NumistaParser("268884", true);
        // NumistaParser numistaParser3 = new NumistaParser("14640", true);
        // NumistaParser numistaParser4 = new NumistaParser("210635", true);

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

        // List<MeshokLot> lots =
        // N4JUtil.getInstance().meshokService.meshokLotService.findAllLimitedWithCategory(100);
        // for (MeshokLot lot : lots) {
        // ImageUtil.saveMeshokImage(lot);
        // System.out.println();
        // }

    }

    public static void main(String[] args) {
        // Clear console on application start
        System.out.print("\033[H\033[2J");
        System.out.flush();

        SpringApplication.run(Application.class, args);
    }

}
