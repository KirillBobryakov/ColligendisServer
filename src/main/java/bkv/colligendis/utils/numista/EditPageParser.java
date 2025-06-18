package bkv.colligendis.utils.numista;


import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.service.numista.NTypeService;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import lombok.Data;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Data
public class EditPageParser {

    private String nid;
    private Document editPage;

    private NType nType = null;
    private boolean changed = false;


    private final long startParsingTime = System.currentTimeMillis();
    private boolean isShowMetrics = false;

    public EditPageParser(String nid) {
        this.nid = nid;
    }

    public static Function<String, EditPageParser> create = EditPageParser::new;

    public static UnaryOperator<EditPageParser> loadNumistaPage = editPageParser -> {
        long startParsingTime = System.currentTimeMillis();
        editPageParser.editPage = NumistaPartParser.loadPageByURL(NumistaPartParser.TYPE_PAGE_PREFIX + editPageParser.nid, true);
        DebugUtil.showInfo(EditPageParser.class, calcAndShowSpentTimeInfo("Page loading", startParsingTime));
        return editPageParser;
    };

    public static Predicate<EditPageParser> isEditPageLoaded = editPageParser -> {
        if (editPageParser.editPage == null) return false;

        Element mainTitle = editPageParser.editPage.selectFirst("#main_title");
        return mainTitle != null && !mainTitle.text().equals("Page not found");
    };

    public static UnaryOperator<EditPageParser> loadNType = editPageParser -> {
        long startParsingTime = System.currentTimeMillis();

        final NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;
        final String nid = editPageParser.getNid();
        final Document page = editPageParser.getEditPage();
        boolean exists = nTypeService.existsByNid(nid);

        if (exists) { // Only informs that NType is in Graph
            editPageParser.setNType(nTypeService.findByNid(nid));
            DebugUtil.showWarning(EditPageParser.class, "Parsing existing Numista Type with nid=" + nid);
        } else { //Create new NType with nid and title - unique fields
            DebugUtil.showError(EditPageParser.class, "Parsing new Numista Type with nid=" + nid);
            String title = NumistaPartParser.getAttribute(page.selectFirst("#designation"), "value");
            editPageParser.setNType(nTypeService.save(new NType(nid, title)));
        }

        DebugUtil.showInfo(EditPageParser.class, calcAndShowSpentTimeInfo("NType loading from database", startParsingTime));
        return editPageParser;
    };

    private static EditPageParser parse(EditPageParser editPageParser, NumistaPartParser partParser) {
        final Document page = editPageParser.getEditPage();
        final NType nType = editPageParser.getNType();

        switch (editPageParser.isShowMetrics ? partParser.parseWithMetric(page, nType) : partParser.parse(page, nType)) {
            case ERROR -> {
                return null;
            }
            case CHANGED -> {
                editPageParser.setChanged(true);
                DebugUtil.showInfo(EditPageParser.class, partParser.partName + " was changed");
            }
        }

        return editPageParser;
    }


    public static UnaryOperator<EditPageParser> showMetrics = editPageParser -> {
        editPageParser.setShowMetrics(true);
        return editPageParser;
    };
    public static UnaryOperator<EditPageParser> hideMetrics = editPageParser -> {
        editPageParser.setShowMetrics(false);
        return editPageParser;
    };

    public static UnaryOperator<EditPageParser> titleParser = editPageParser -> EditPageParser.parse(editPageParser, new TitleParser());
    public static UnaryOperator<EditPageParser> issuerParser = editPageParser -> EditPageParser.parse(editPageParser, new IssuerParser());
    public static UnaryOperator<EditPageParser> rulerParser = editPageParser -> EditPageParser.parse(editPageParser, new RulerParser());
    public static UnaryOperator<EditPageParser> issuingEntityParser = editPageParser -> EditPageParser.parse(editPageParser, new IssuingEntityParser());
    public static UnaryOperator<EditPageParser> currencyParser = editPageParser -> EditPageParser.parse(editPageParser, new CurrencyParser());
    public static UnaryOperator<EditPageParser> denominationParser = editPageParser -> EditPageParser.parse(editPageParser, new DenominationParser());
    public static UnaryOperator<EditPageParser> collectibleTypeParser = editPageParser -> EditPageParser.parse(editPageParser, new CollectibleTypeParser());
    public static UnaryOperator<EditPageParser> commemoratedEventParser = editPageParser -> EditPageParser.parse(editPageParser, new CommemoratedEventParser());
    public static UnaryOperator<EditPageParser> seriesParser = editPageParser -> EditPageParser.parse(editPageParser, new SeriesParser());
    public static UnaryOperator<EditPageParser> demonetizedParser = editPageParser -> EditPageParser.parse(editPageParser, new DemonetizedParser());
    public static UnaryOperator<EditPageParser> referenceNumberParser = editPageParser -> EditPageParser.parse(editPageParser, new ReferenceNumberParser());
    public static UnaryOperator<EditPageParser> mintageParser = editPageParser -> EditPageParser.parse(editPageParser, new MintageParser());

    public static UnaryOperator<EditPageParser> technicalDataParser = editPageParser -> EditPageParser.parse(editPageParser, new TechnicalDataParser());

    public static UnaryOperator<EditPageParser> obverseParser = editPageParser -> EditPageParser.parse(editPageParser, new ObverseParser());

    public static UnaryOperator<EditPageParser> reverseParser = editPageParser -> EditPageParser.parse(editPageParser, new ReverseParser());

    public static UnaryOperator<EditPageParser> edgeParser = editPageParser -> EditPageParser.parse(editPageParser, new EdgeParser());

    public static UnaryOperator<EditPageParser> watermarkParser = editPageParser -> EditPageParser.parse(editPageParser, new WatermarkParser());

    public static UnaryOperator<EditPageParser> mintsParser = editPageParser -> EditPageParser.parse(editPageParser, new MintsParser());



    public static UnaryOperator<EditPageParser> saveNType = editPageParser -> {
        if (editPageParser.isChanged()) {
            long time = System.currentTimeMillis();
            final NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;
            editPageParser.setNType(nTypeService.save(editPageParser.getNType()));
            if (editPageParser.isShowMetrics) {
                DebugUtil.showInfo(EditPageParser.class, calcAndShowSpentTimeInfo("nid: " + editPageParser.getNType().getNid() + " Saving NType takes: ", time));
            }
        } else {
            DebugUtil.showInfo(EditPageParser.class, "nid: " + editPageParser.getNType().getNid() + "NType without saving.");
        }

        return editPageParser;
    };

    public static Consumer<EditPageParser> finalyInfo = editPageParser -> {
        if (editPageParser.isShowMetrics) {
            DebugUtil.showInfo(EditPageParser.class, calcAndShowSpentTimeInfo("nid: " + editPageParser.nType.getNid() + " Parsing totally takes: ", editPageParser.startParsingTime));
        }
        DebugUtil.showInfo(EditPageParser.class, "nid: " + editPageParser.getNType().getNid() + " NType finished parsing.");
    };

    private static String calcAndShowSpentTimeInfo(String message, long startTime) {
        return message + " takes " + (System.currentTimeMillis() - startTime) / 1000 + " sec " + (System.currentTimeMillis() - startTime) % 1000 + " millis";
    }

}
