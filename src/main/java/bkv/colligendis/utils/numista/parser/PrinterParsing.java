package bkv.colligendis.utils.numista.parser;

import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PrinterParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(PrinterParsing.class);

    public PrinterParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            Element printerListElement = pageParser.getNumistaPage().selectFirst("#printer_list");

            if (printerListElement == null) {
                return ParsingResult.NOT_CHANGED;
            }

            Elements printerElements = printerListElement.select("li");

            Elements selectElements = printerElements.select("select[class=printer_select]");
            for (Element select : selectElements) {
                HashMap<String, String> printer = getAttributeWithTextSingleOption(select,
                        "value");
                if (printer == null) {
                    continue;
                }

                if (!isValueAndTextNotNullAndNotEmpty(printer)) {
                    continue;
                }

                String printerNid = printer.get("value");
                String printerName = printer.get("text");

                UUID printerUuid = printerService.findUuidByNid(printerNid);
                if (printerUuid == null) {
                    logger.error("Can't find printer: " + printerName + " (nid: " + printerNid + ")");
                    continue;
                } else {
                    if (!printerService.compareName(printerUuid, printerName)) {
                        printerService.setName(printerUuid, printerName);
                        logger.warn("Updated printer: " + printerName + " (nid: " + printerNid + ")");
                        result = ParsingResult.CHANGED;
                    }
                }

                if (!nTypeService.hasRelationshipToPrinter(pageParser.getNTypeUuid(), printerUuid)) {
                    nTypeService.addPrinter(pageParser.getNTypeUuid(), printerUuid);
                    result = ParsingResult.CHANGED;
                }
            }

            return result;

        });
    }

}
