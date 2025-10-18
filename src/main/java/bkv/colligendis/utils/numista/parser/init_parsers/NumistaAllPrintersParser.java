package bkv.colligendis.utils.numista.parser.init_parsers;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bkv.colligendis.database.entity.numista.Printer;
import bkv.colligendis.database.service.numista.PrinterService;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.parser.PartParser;

public class NumistaAllPrintersParser {

    private static final Logger logger = LogManager.getLogger(NumistaAllPrintersParser.class);

    private static final String BASE_URL = "https://en.numista.com/catalogue/printers.php";

    public NumistaAllPrintersParser() {
    }

    public void parseAndSaveAllPrinters() {
        logger.info("Starting to parse printers from: " + BASE_URL);

        // Load the printers page
        Document page = PartParser.loadPageByURL(BASE_URL, true);

        if (page == null) {
            logger.error("Failed to load printers page from: " + BASE_URL);
            return;
        }

        // Find all printer rows in the table
        // The structure is: <tr> contains <td> with <a> tag for printer link and
        // <strong>
        // for name
        Elements printerRows = page.select("li");

        logger.info("Found " + printerRows.size() + " total rows");

        int processedCount = 0;
        for (Element row : printerRows) {
            try {
                // Find the link to the printer page (format:
                // /catalogue/printer.php?id=NID_NUMBER)
                Element printerLink = row.selectFirst("a[href*=/catalogue/printer.php]");

                if (printerLink == null) {
                    continue;
                }

                // Extract printer ID (nid) from URL
                String href = printerLink.attr("href");
                String nid = extractPrinterId(href);

                if (nid == null || nid.isEmpty()) {
                    logger.warn("Skipping row - no valid printer ID found in href: " + href);
                    continue;
                }

                // Extract fullName from <strong> tag in the same row
                Element strongElement = row.selectFirst("strong");
                String name = null;
                if (strongElement != null) {
                    name = strongElement.text().trim();
                }

                if (name == null || name.isEmpty()) {
                    logger.warn("Skipping printer with nid=" + nid + " - no name found");
                    continue;
                }

                // Check if printer already exists in database
                PrinterService printerService = N4JUtil.getInstance().numistaService.printerService;

                UUID printerUuid = printerService.findUuidByNid(nid);
                if (printerUuid == null) {
                    printerUuid = printerService.save(new Printer(nid, name)).getUuid();
                    logger.info("Created new printer: " + name + " (nid: " + nid + ")");
                } else {
                    if (!printerService.compareName(printerUuid, name)) {
                        printerService.setName(printerUuid, name);
                        logger.info("Updated printer: " + name + " (nid: " + nid + ")");
                    }
                }

                processedCount++;

            } catch (Exception e) {
                logger.error("Error processing printer row: " + e.getMessage());
                e.printStackTrace();
            }
        }

        logger.info("Successfully processed " + processedCount + " printers");
    }

    /**
     * Extracts printer ID from URL format: /catalogue/printer.php?id=NID_NUMBER
     */
    private String extractPrinterId(String href) {
        if (href.contains("id=")) {
            // Extract ID parameter from URL
            String[] parts = href.split("id=");
            if (parts.length > 1) {
                String idPart = parts[1];
                // Get only the ID part (before any & or # characters)
                int endIndex = idPart.indexOf('&');
                if (endIndex > 0) {
                    idPart = idPart.substring(0, endIndex);
                }
                endIndex = idPart.indexOf('#');
                if (endIndex > 0) {
                    idPart = idPart.substring(0, endIndex);
                }
                return idPart.trim();
            }
        }
        return null;
    }
}
