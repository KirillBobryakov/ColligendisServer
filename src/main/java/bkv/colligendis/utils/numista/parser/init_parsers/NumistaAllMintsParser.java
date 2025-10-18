package bkv.colligendis.utils.numista.parser.init_parsers;

import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.parser.PartParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class NumistaAllMintsParser {

    private static final Logger logger = LogManager.getLogger(NumistaAllMintsParser.class);

    private static final String BASE_URL = "https://en.numista.com/catalogue/mints.php";

    public NumistaAllMintsParser() {
    }

    /**
     * Parses the Numista mints page and saves all mints to the database.
     * The page contains a list of mints with:
     * - id in <a> tag (nid)
     * - fullName in <strong> tag
     * - coordinates in <a href="map_canvas"> with map.flyTo([lat, lon])
     */
    public void parseAndSaveAllMints() {
        logger.info("Starting to parse mints from: " + BASE_URL);

        // Load the mints page
        Document page = PartParser.loadPageByURL(BASE_URL, true);

        if (page == null) {
            logger.error("Failed to load mints page from: " + BASE_URL);
            return;
        }

        // Find all mint rows in the table
        // The structure is: <tr> contains <td> with <a> tag for mint link and <strong>
        // for name
        Elements mintRows = page.select("li");

        logger.info("Found " + mintRows.size() + " total rows");

        int processedCount = 0;
        for (Element row : mintRows) {
            try {
                // Find the link to the mint page (format: /catalogue/mint.php?id=XXX)
                Element mintLink = row.selectFirst("a[href*=/catalogue/mint.php]");

                if (mintLink == null) {
                    continue;
                }

                // Extract mint ID (nid) from URL
                String href = mintLink.attr("href");
                String nid = extractMintId(href);

                if (nid == null || nid.isEmpty()) {
                    logger.warn("Skipping row - no valid mint ID found in href: " + href);
                    continue;
                }

                // Extract fullName from <strong> tag in the same row
                Element strongElement = row.selectFirst("strong");
                String fullName = null;
                if (strongElement != null) {
                    fullName = strongElement.text().trim();
                }

                if (fullName == null || fullName.isEmpty()) {
                    logger.warn("Skipping mint with nid=" + nid + " - no fullName found");
                    continue;
                }

                // Extract coordinates from <a href="#map_canvas"> with onclick containing
                // map.flyTo([lat, lon])
                String latitude = null;
                String longitude = null;

                Element mapLink = row.selectFirst("a[href=#map_canvas]");
                if (mapLink != null) {
                    String onclick = mapLink.attr("onclick");
                    if (onclick != null && !onclick.isEmpty()) {
                        // Extract coordinates from map.flyTo([55.755833,37.617778], ...)
                        Pattern pattern = Pattern.compile("map\\.flyTo\\(\\[([0-9.-]+),([0-9.-]+)\\]");
                        Matcher matcher = pattern.matcher(onclick);

                        if (matcher.find()) {
                            latitude = matcher.group(1);
                            longitude = matcher.group(2);
                        }
                    }
                }

                // Check if mint already exists in database

                UUID mintUuid = N4JUtil.getInstance().numistaService.mintService.findUuidByNid(nid);
                if (mintUuid == null) {
                    mintUuid = N4JUtil.getInstance().numistaService.mintService
                            .save(new Mint(nid, fullName, latitude, longitude)).getUuid();
                    logger.info("Created new mint: " + fullName + " (nid: " + nid + ")");
                } else {
                    if (!N4JUtil.getInstance().numistaService.mintService.compareFullName(mintUuid, fullName)) {
                        N4JUtil.getInstance().numistaService.mintService.setFullName(mintUuid, fullName);
                        logger.info("Updated mint: " + fullName + " (nid: " + nid + ")");
                    }
                    if (latitude != null && longitude != null) {
                        if (!N4JUtil.getInstance().numistaService.mintService.compareLatitude(mintUuid, latitude)) {
                            N4JUtil.getInstance().numistaService.mintService.setLatitude(mintUuid, latitude);
                            logger.info("Updated latitude for mint: " + fullName + " (nid: " + nid + ")");
                        }
                        if (!N4JUtil.getInstance().numistaService.mintService.compareLongitude(mintUuid, longitude)) {
                            N4JUtil.getInstance().numistaService.mintService.setLongitude(mintUuid, longitude);
                            logger.info("Updated longitude for mint: " + fullName + " (nid: " + nid + ")");
                        }
                    }
                }

                processedCount++;

            } catch (Exception e) {
                logger.error("Error processing mint row: " + e.getMessage());
                e.printStackTrace();
            }
        }

        logger.info("Successfully processed " + processedCount + " mints");
    }

    /**
     * Extracts mint ID from URL format: /catalogue/mint.php?id=XXX
     */
    private String extractMintId(String href) {
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
