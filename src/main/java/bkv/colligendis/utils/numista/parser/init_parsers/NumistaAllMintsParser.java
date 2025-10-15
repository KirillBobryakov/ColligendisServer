package bkv.colligendis.utils.numista.parser.init_parsers;

import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.NumistaPartParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NumistaAllMintsParser {

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
        DebugUtil.showInfo(this, "Starting to parse mints from: " + BASE_URL);

        // Load the mints page
        Document page = NumistaPartParser.loadPageByURL(BASE_URL, true);

        if (page == null) {
            DebugUtil.showError(this, "Failed to load mints page from: " + BASE_URL);
            return;
        }

        // Find all mint rows in the table
        // The structure is: <tr> contains <td> with <a> tag for mint link and <strong>
        // for name
        Elements mintRows = page.select("tr");

        DebugUtil.showInfo(this, "Found " + mintRows.size() + " total rows");

        int processedCount = 0;
        for (Element row : mintRows) {
            try {
                // Find the link to the mint page (format: /catalogue/mint.php?id=XXX)
                Element mintLink = row.selectFirst("a[href^=/catalogue/mint.php]");

                if (mintLink == null) {
                    continue;
                }

                // Extract mint ID (nid) from URL
                String href = mintLink.attr("href");
                String nid = extractMintId(href);

                if (nid == null || nid.isEmpty()) {
                    DebugUtil.showWarning(this, "Skipping row - no valid mint ID found in href: " + href);
                    continue;
                }

                // Extract fullName from <strong> tag in the same row
                Element strongElement = row.selectFirst("strong");
                String fullName = null;
                if (strongElement != null) {
                    fullName = strongElement.text().trim();
                }

                if (fullName == null || fullName.isEmpty()) {
                    DebugUtil.showWarning(this, "Skipping mint with nid=" + nid + " - no fullName found");
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
                Mint existingMint = N4JUtil.getInstance().numistaService.mintService.findByNid(nid, fullName);

                boolean needsUpdate = false;

                if (existingMint != null) {
                    // Update coordinates if they were found and are different
                    if (latitude != null && longitude != null) {
                        if (!latitude.equals(existingMint.getLatitude()) ||
                                !longitude.equals(existingMint.getLongitude())) {
                            existingMint.setLatitude(latitude);
                            existingMint.setLongitude(longitude);
                            needsUpdate = true;
                            DebugUtil.showInfo(this, "Updated coordinates for mint: " + fullName +
                                    " (nid: " + nid + ") - lat: " + latitude + ", lon: " + longitude);
                        }
                    }

                    if (needsUpdate) {
                        N4JUtil.getInstance().numistaService.mintService.save(existingMint);
                    }
                } else {
                    // Create new mint (already created by findByNid)
                    // Update with coordinates if available
                    Mint mint = N4JUtil.getInstance().numistaService.mintService.findByNid(nid, fullName);
                    if (mint != null && latitude != null && longitude != null) {
                        mint.setLatitude(latitude);
                        mint.setLongitude(longitude);
                        N4JUtil.getInstance().numistaService.mintService.save(mint);
                        DebugUtil.showInfo(this, "Created new mint: " + fullName +
                                " (nid: " + nid + ") - lat: " + latitude + ", lon: " + longitude);
                    }
                }

                processedCount++;

            } catch (Exception e) {
                DebugUtil.showError(this, "Error processing mint row: " + e.getMessage());
                e.printStackTrace();
            }
        }

        DebugUtil.showInfo(this, "Successfully processed " + processedCount + " mints");
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
