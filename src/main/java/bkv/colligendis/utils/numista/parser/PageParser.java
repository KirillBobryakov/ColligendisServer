package bkv.colligendis.utils.numista.parser;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.service.numista.NTypeService;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.NumistaPartParser;
import lombok.Data;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Data
public class PageParser {

    private static final Logger logger = LogManager.getLogger(PageParser.class);

    private ParsingResult currentParsingStatus = ParsingResult.NOT_CHANGED;

    private String nid;
    private Document numistaPage;

    private UUID nTypeUuid;
    private UUID collectibleTypeUuid;
    private UUID issuerUuid;
    private UUID currencyUuid;
    private UUID denominationUuid;

    private boolean changed = false;

    private final long startParsingTime = System.currentTimeMillis();
    private boolean isShowMetrics = false;

    public PageParser(String nid) {
        this.nid = nid;
    }

    public static Consumer<Stream<String>> parse = nids -> {
        nids.map(nid -> PageParser.create.andThen(PageParser.loadNumistaPage).apply(nid))
                .filter(PageParser.isEditPageLoaded)
                .map(PageParser -> PageParser.loadNType
                        .andThen(PageParser.showMetrics)
                        .andThen(PageParser.titleParser)
                        .andThen(PageParser.collectibleTypeParser)
                        .andThen(PageParser.issuerParser)
                        .andThen(PageParser.rulerParser)
                        .andThen(PageParser.issuingEntityParser)
                        .andThen(PageParser.currencyParser)
                        .andThen(PageParser.denominationParser)
                        .andThen(PageParser.commemoratedEventParser)
                        .andThen(PageParser.seriesParser)
                        .andThen(PageParser.demonetizedAndIssueDateParser)
                        .andThen(PageParser.referenceNumberParser)
                        .andThen(PageParser.mintageParser)
                        // .andThen(PageParser.technicalDataParser)
                        // .andThen(PageParser.obverseParser)
                        // .andThen(PageParser.reverseParser)
                        // .andThen(PageParser.edgeParser)
                        // .andThen(PageParser.watermarkParser)
                        // .andThen(PageParser.mintsParser)
                        // .andThen(PageParser.saveNType)
                        .apply(PageParser))
                .forEach(PageParser.finalyInfo);
    };

    public static Function<String, PageParser> create = PageParser::new;

    public static UnaryOperator<PageParser> loadNumistaPage = pageParser -> {
        logger.debug("Loading Numista page nid: {}", pageParser.nid);

        long time = System.currentTimeMillis();
        pageParser.numistaPage = NumistaPartParser
                .loadPageByURL(NumistaPartParser.TYPE_PAGE_PREFIX + pageParser.nid, true);

        if (pageParser.numistaPage == null) {
            logger.error("Loading Numista page nid: {} editPage is null", pageParser.nid);
            return null;
        }
        logger.info("Loading Numista page takes: {}", calcAndShowSpentTimeInfo(time));
        return pageParser;
    };

    public static Predicate<PageParser> isEditPageLoaded = pageParser -> {
        if (pageParser.numistaPage == null)
            return false;

        Element mainTitle = pageParser.numistaPage.selectFirst("#main_title");
        return mainTitle != null && !mainTitle.text().equals("Page not found");
    };

    public UnaryOperator<PageParser> loadNType = pageParser -> {

        final NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;
        final String nid = pageParser.getNid();
        final Document page = pageParser.getNumistaPage();
        boolean exists = nTypeService.existsByNid(nid);

        if (exists) { // Only informs that NType is in Graph
            pageParser.setNTypeUuid(nTypeService.findNTypeUuidByNid(getNid()));
            logger.debug("Parsing existing Numista Type with nid: {} nTypeUuid: {}", nid, pageParser.getNTypeUuid());
        } else { // Create new NType with nid and title - unique fields
            String title = NumistaPartParser.getAttribute(page.selectFirst("#designation"), "value");
            pageParser.setNTypeUuid(nTypeService.save(new NType(nid, title)).getUuid());
            logger.warn("Parsing new Numista Type with nid: {} title: {}", nid, title);
        }

        return pageParser;
    };

    private PageParser parse(PageParser pageParser, PartParser partParser) {
        if (pageParser.currentParsingStatus == ParsingResult.ERROR) {
            return pageParser;
        }

        switch (pageParser.isShowMetrics ? partParser.parseWithMetric(pageParser)
                : partParser.parse(pageParser)) {
            case ERROR -> {
                logger.error("PageParser {} parsing error", partParser.partName);
                pageParser.currentParsingStatus = ParsingResult.ERROR;
            }
            case CHANGED -> {
                pageParser.setChanged(true);
                logger.warn("{} was changed", partParser.partName);
            }
            case NOT_CHANGED -> {
            }
        }

        return pageParser;
    }

    public UnaryOperator<PageParser> showMetrics = PageParser -> {
        PageParser.setShowMetrics(true);
        return PageParser;
    };
    public static UnaryOperator<PageParser> hideMetrics = PageParser -> {
        PageParser.setShowMetrics(false);
        return PageParser;
    };

    public UnaryOperator<PageParser> titleParser = PageParser -> PageParser.parse(PageParser, new TitleParsing());

    public UnaryOperator<PageParser> collectibleTypeParser = PageParser -> PageParser.parse(PageParser,
            new CollectibleTypeParsing());

    public UnaryOperator<PageParser> issuerParser = PageParser -> PageParser.parse(PageParser, new IssuerParsing());

    public UnaryOperator<PageParser> rulerParser = PageParser -> PageParser.parse(PageParser, new RulerParsing());

    public UnaryOperator<PageParser> issuingEntityParser = PageParser -> PageParser.parse(PageParser,
            new IssuingEntityParsing());
    public UnaryOperator<PageParser> currencyParser = PageParser -> PageParser.parse(PageParser, new CurrencyParsing());

    public UnaryOperator<PageParser> denominationParser = PageParser -> PageParser.parse(PageParser,
            new DenominationParsing());

    public UnaryOperator<PageParser> commemoratedEventParser = PageParser -> PageParser.parse(PageParser,
            new CommemoratedEventParsing());

    public UnaryOperator<PageParser> seriesParser = PageParser -> PageParser.parse(PageParser, new SeriesParsing());

    public UnaryOperator<PageParser> demonetizedAndIssueDateParser = PageParser -> PageParser.parse(PageParser,
            new DemonetizedAndIssueDateParsing());

    public UnaryOperator<PageParser> referenceNumberParser = PageParser -> PageParser.parse(PageParser,
            new ReferenceNumberParsing());

    public UnaryOperator<PageParser> mintageParser = PageParser -> PageParser.parse(PageParser,
            new MintageParsing());

    public UnaryOperator<PageParser> technicalDataParser = PageParser -> PageParser
            .parse(PageParser, new TechnicalDataParsing());

    // public UnaryOperator<PageParser> obverseParser = PageParser ->
    // PageParser.parse(PageParser,
    // new ObverseParser());

    // public UnaryOperator<PageParser> reverseParser = PageParser ->
    // PageParser.parse(PageParser,
    // new ReverseParser());

    // public UnaryOperator<PageParser> edgeParser = PageParser ->
    // PageParser.parse(PageParser,
    // new EdgeParser());

    // public UnaryOperator<PageParser> watermarkParser = PageParser ->
    // PageParser.parse(PageParser,
    // new WatermarkParser());

    // public UnaryOperator<PageParser> mintsParser = PageParser ->
    // PageParser.parse(PageParser,
    // new MintsParser());

    public UnaryOperator<PageParser> saveNType = PageParser -> {
        // if (PageParser.isChanged()) {
        // long time = System.currentTimeMillis();
        // final NTypeService nTypeService =
        // N4JUtil.getInstance().numistaService.nTypeService;
        // PageParser.setNType(nTypeService.save(PageParser.getNType()));
        // if (PageParser.isShowMetrics) {
        // DebugUtil.showInfo(PageParser.class, calcAndShowSpentTimeInfo(
        // "nid: " + PageParser.getNType().getNid() + " Saving NType takes: ", time));
        // }
        // } else {
        // DebugUtil.showInfo(PageParser.class,
        // "nid: " + PageParser.getNType().getNid() + "NType without saving.");
        // }

        return PageParser;
    };

    public static Consumer<PageParser> finalyInfo = PageParser -> {
        if (PageParser.isShowMetrics) {
            logger.warn("nid: {} Parsing totally takes: {}", PageParser.getNid(),
                    calcAndShowSpentTimeInfo(PageParser.startParsingTime));
        }
    };

    private static String calcAndShowSpentTimeInfo(long startTime) {
        return (System.currentTimeMillis() - startTime) / 1000 + " sec "
                + (System.currentTimeMillis() - startTime) % 1000 + " millis";
    }

}
