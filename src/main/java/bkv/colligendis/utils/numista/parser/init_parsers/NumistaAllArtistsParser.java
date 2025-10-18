package bkv.colligendis.utils.numista.parser.init_parsers;

import bkv.colligendis.database.entity.numista.Artist;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.parser.PartParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class NumistaAllArtistsParser {
    private static final Logger logger = LogManager.getLogger(NumistaAllArtistsParser.class);

    private static final String BASE_URL = "https://en.numista.com/catalogue/artists.php";

    public NumistaAllArtistsParser() {
    }

    /**
     * Parses the Numista artists page and saves all artists to the database.
     * The page contains a list of artists with links like
     * /catalogue/index.php?r=artist%2Fview&id=XXXX
     */
    public void parseAndSaveAllArtists() {

        logger.info("Starting to parse artists from: " + BASE_URL);

        // Load the artists page
        Document page = PartParser.loadPageByURL(BASE_URL, true);

        if (page == null) {
            logger.error("Failed to load artists page from: " + BASE_URL);
            return;
        }

        List<Artist> artists = new ArrayList<>();

        // Find all artist links - they are in the format: /catalogue/artist.php?id=XXX
        Elements artistLinks = page.select("a[href^=/catalogue/artist.php]");

        logger.info("Found " + artistLinks.size() + " artist links");

        for (Element link : artistLinks) {
            String href = link.attr("href");
            String name = link.text().trim();

            // Skip empty names or invalid links
            if (name.isEmpty() || href.isEmpty()) {
                continue;
            }

            // Extract artist ID (nid) from URL
            // Format: /catalogue/artist.php?id=XXX
            String nid = extractArtistId(href);

            if (nid != null && !nid.isEmpty()) {
                // Check if artist already exists in database
                Artist existingArtist = N4JUtil.getInstance().numistaService.artistService.findByNid(nid);

                if (existingArtist == null) {
                    // Create new artist
                    Artist artist = new Artist();
                    artist.setNid(nid);
                    artist.setName(name);
                    artists.add(artist);

                    logger.info("Found new artist: " + name + " (nid: " + nid + ")");
                } else {
                    // Update existing artist name if different
                    if (!existingArtist.getName().equals(name)) {
                        existingArtist.setName(name);
                        N4JUtil.getInstance().numistaService.artistService.save(existingArtist);

                        logger.info("Updated artist: " + name + " (nid: " + nid + ")");
                    }
                }
            }
        }

        // Save all new artists to database
        if (!artists.isEmpty()) {
            logger.info("Saving " + artists.size() + " new artists to database");
            for (Artist artist : artists) {
                N4JUtil.getInstance().numistaService.artistService.save(artist);
            }
            logger.info("Successfully saved all artists");
        } else {
            logger.info("No new artists to save");
        }
    }

    /**
     * Extracts artist ID from URL format: /catalogue/artist.php?id=XXX
     */
    private String extractArtistId(String href) {
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
