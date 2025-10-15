package bkv.colligendis.utils.numista.parser;

import java.util.UUID;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.NumistaPartParser;

public class NumistaParser {

    private static final Logger logger = LogManager.getLogger(NumistaParser.class);

    private String nid;
    private UUID nTypeUuid;
    private boolean withMetrics;

    private Document editPage;

    public NumistaParser(String nid, boolean withMetrics) {
        this.nid = nid;
        this.withMetrics = withMetrics;

        loadNumistaPage();

        long start = System.currentTimeMillis();

        this.nTypeUuid = N4JUtil.getInstance().numistaService.nTypeService.findNTypeUuidByNid(nid);
        if (nTypeUuid == null) {
            this.nTypeUuid = N4JUtil.getInstance().numistaService.nTypeService.save(new NType(nid)).getUuid();
        }
        // TitleParsing.parse(editPage, nTypeUuid);
        // CollectibleTypeParsing.parse(editPage, nTypeUuid);
        // IssuerParsing.parse(editPage, nTypeUuid);
        // RulerParsing.parse(editPage, nTypeUuid);
        // IssuingEntityParsing.parse(editPage, nTypeUuid);
        // CurrencyParsing.parse(editPage, nTypeUuid);
        // DenominationParsing.parse(editPage, nTypeUuid);
        // CommemoratedEventParsing.parse(editPage, nTypeUuid);
        // SeriesParsing.parse(editPage, nTypeUuid);
        // DemonetizedAndIssueDateParsing.parse(editPage, nTypeUuid);
        // ReferenceNumberParsing.parse(editPage, nTypeUuid);
        // MintageParsing.parse(editPage, nTypeUuid);
        // TechnicalDataParsing.parse(editPage, nTypeUuid);
        System.out.println("Time execution: " + " takes " + (System.currentTimeMillis() - start) / 1000
                + " sec "
                + (System.currentTimeMillis() - start) % 1000 + " millis");
    }

    public void loadNumistaPage() {
        this.editPage = NumistaPartParser.loadPageByURL(NumistaPartParser.TYPE_PAGE_PREFIX + nid, true);
    }

    public static <T, R> Function<T, R> withMetrics(Function<T, R> function) {
        return input -> {
            long start = System.currentTimeMillis();
            R result = function.apply(input);
            System.out.println("Time execution: " + " takes " + (System.currentTimeMillis() - start) / 1000
                    + " sec "
                    + (System.currentTimeMillis() - start) % 1000 + " millis");
            return result;
        };
    }

}
