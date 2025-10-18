package bkv.colligendis.utils.numista.parser;

import java.util.Objects;

import org.jsoup.nodes.Document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TitleParsing extends PartParser {

    private static final Logger logger = LogManager.getLogger(TitleParsing.class);

    public TitleParsing() {
        super((pageParser) -> {
            Document page = pageParser.getNumistaPage();
            String designation = PartParser.getAttribute(page.selectFirst("#designation"), "value");
            if (designation == null) {
                logger.error("The Title of NType with nid = {} can't be found. {}",
                        pageParser.getNTypeUuid(),
                        Objects.requireNonNull(page.selectFirst("p[class=info_box]")).text());

                return ParsingResult.ERROR;
            }
            if (!nTypeService.compareTitle(pageParser.getNTypeUuid(), designation)) {
                nTypeService.setTitle(pageParser.getNTypeUuid(), designation);
                return ParsingResult.CHANGED;
            }

            return ParsingResult.NOT_CHANGED;
        });

        this.partName = "Title";
    }
}
