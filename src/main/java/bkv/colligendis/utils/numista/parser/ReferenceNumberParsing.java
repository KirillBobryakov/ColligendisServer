package bkv.colligendis.utils.numista.parser;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import bkv.colligendis.database.entity.numista.Catalogue;
import bkv.colligendis.database.entity.numista.CatalogueReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReferenceNumberParsing extends PartParser {

    private static final Logger logger = LogManager.getLogger(ReferenceNumberParsing.class);

    public ReferenceNumberParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            List<ReferenceToCatalogue> references = new ArrayList<>();

            Elements referenceInputs = pageParser.getNumistaPage().select("div[class=reference_input]");

            for (Element element : referenceInputs) {
                Element catalogueElement = element.selectFirst("option");
                Element numberElement = element.selectFirst("input");

                if (catalogueElement == null || numberElement == null)
                    continue;

                if (!numberElement.attr("value").isEmpty()) {
                    ReferenceToCatalogue referenceToCatalogue = new ReferenceToCatalogue(
                            catalogueElement.attr("value"),
                            catalogueElement.text(),
                            numberElement.attr("value"));

                    references.add(referenceToCatalogue);
                }
            }

            List<UUID> foundReferencesUUIDs = references.stream()
                    .map(reference -> {
                        logger.info("Parsing reference: " + reference.getNumber() + " " + reference.getCatalogueNid());
                        UUID uuid = catalogueReferenceService.findUuidByNumberAndCatalogueNid(reference.getNumber(),
                                reference.getCatalogueNid());

                        if (uuid == null) {
                            UUID catalogueUuid = catalogueService.findUuidByNid(reference.getCatalogueNid());
                            if (catalogueUuid == null) {
                                parseReferenceCataloguesByCode(reference.getCatalogueCode());
                                catalogueUuid = catalogueService.findUuidByNid(reference.getCatalogueNid());
                            }
                            assert catalogueUuid != null;

                            uuid = catalogueReferenceService.save(new CatalogueReference()).getUuid();
                            catalogueReferenceService.setNumber(uuid, reference.getNumber());
                            catalogueReferenceService.setCatalogue(uuid, catalogueUuid);
                        }
                        assert uuid != null;
                        return uuid;
                    })
                    .collect(Collectors.toList());

            if (nTypeService.equateCatalogueReferences(pageParser.getNTypeUuid(), foundReferencesUUIDs)) {
                result = ParsingResult.CHANGED;
            }

            return result;
        });

        this.partName = "ReferenceNumber";
    }

    public static final String CATALOGUE_SEARCH = "https://en.numista.com/catalogue/search_catalogues.php?q=";

    /*
     * References
     * Last update: 26 September 2024
     * 
     * The field specifies the alphanumeric code that identifies the coin type in a
     * reference catalogue. Up to ten references can be specified (if some reference
     * catalogues are missing, you can request their addition to the database). If
     * more references exist for a coin, the additional ones may be added in the
     * comments section.
     * 
     * Order of references
     * When possible, the same sequence of references should be used for all the
     * coin types of an issuer. Newer, more authoritative, and more exhaustive
     * standard references should appear first.
     * 
     * What if same entry on Numista is linked to multiple reference numbers in the
     * same catalogue?
     * If a coin type has more than one code in a reference catalogue, always add
     * the first one (or the most relevant). The same reference catalogue may be
     * added in a new line for recording multiple codes, provided you did not reach
     * the maximum number of references.
     * 
     * Missing coin in a catalogue
     * If only a small number of coins are missing from a standard monograph
     * (reference catalogue dedicated to a very specific topic) that is used
     * consistently for an issuer, then an en dash (“–”) can be used instead of a
     * reference code, to show that the coins are unlisted, for instance this
     * unlisted 20 Batzen coin in the Hofer catalogue. All other coins of the
     * Helvetic Republic have a Hofer reference.
     * 
     * Similar reference
     * If a coin type is absent from a catalog, the similarity to another type in
     * that catalog can be indicated by “var.” after the code.
     */

    public static boolean parseReferenceCataloguesByCode(String code) {
        code = Normalizer.normalize(code, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

        Document catalogueSearchDocument = loadPageByURL(CATALOGUE_SEARCH + code, true);
        Element body = catalogueSearchDocument.selectFirst("body");

        String jsonRefs = body.text().replace("\\r\\n", "").replace("<\\/em>", "");

        Gson gson = new Gson();
        ReferencedCatalogue[] referencedCatalogues = gson.fromJson(jsonRefs, ReferencedCatalogue[].class);

        for (ReferencedCatalogue referencedCatalogue : referencedCatalogues) {
            Catalogue catalogue = catalogueService.create(referencedCatalogue.id,
                    referencedCatalogue.text, referencedCatalogue.bibliography);
            if (catalogue == null)
                logger.error("New Catalogue with nid=" + referencedCatalogue.id
                        + " and code=" + referencedCatalogue.text + " can't be created.");
        }

        return true;
    }

}

@Data
@AllArgsConstructor
class ReferenceToCatalogue {
    String catalogueNid;
    String catalogueCode;
    String number;

}

@Data
class ReferencedCatalogue {
    String id;
    String text;
    String bibliography;
}
