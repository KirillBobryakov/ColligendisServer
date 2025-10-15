package bkv.colligendis.utils.numista.parser;

import java.util.UUID;

import org.jsoup.nodes.Document;

import bkv.colligendis.database.entity.numista.CommemoratedEvent;
import bkv.colligendis.database.service.numista.CommemoratedEventService;
import bkv.colligendis.database.service.numista.NTypeService;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.NumistaPartParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommemoratedEventParsing extends PartParser {
    private static final Logger logger = LogManager.getLogger(CommemoratedEventParsing.class);

    public CommemoratedEventParsing() {
        super((pageParser) -> {
            ParsingResult result = ParsingResult.NOT_CHANGED;

            String evenement = NumistaPartParser.getAttribute(pageParser.getNumistaPage().selectFirst("#evenement"),
                    "value");

            if (evenement == null || evenement.isEmpty()) {
                return ParsingResult.NOT_CHANGED;
            }

            UUID foundCommemoratedEventUuid = commemoratedEventService.findUuidByName(evenement);

            if (foundCommemoratedEventUuid == null) {
                foundCommemoratedEventUuid = commemoratedEventService.save(new CommemoratedEvent(evenement))
                        .getUuid();
            }

            if (!nTypeService.hasRelationshipToCommemoratedEvent(pageParser.getNTypeUuid(),
                    foundCommemoratedEventUuid)) {
                nTypeService.setCommemoratedEvent(pageParser.getNTypeUuid(), foundCommemoratedEventUuid);
                result = ParsingResult.CHANGED;
            }

            return result;
        });

        this.partName = "CommemoratedEvent";
    }

    /*
     * Specify the subject of the commemorative issue. Do not include dates. Format
     * the subject according to the following examples:
     * 
     * 100th anniversary of the birth of Albert Einstein (only “Albert Einstein” in
     * the title)
     * 100th anniversary of the Gotthard Railway (only “Gotthard Railway” in the
     * title)
     * 150th anniversary of the death of Johann Heinrich Pestalozzi (only “Johann
     * Heinrich Pestalozzi” in the title)
     * 500th anniversary of the Treaty of Stans (only “Treaty of Stans” in the
     * title)
     * 600th anniversary of the Battle of Grunwald (only “Battle of Grunwald” in the
     * title)
     * Wedding of Prince Philip and Princess Mathilde (only “Wedding of Philip and
     * Mathilde” in the title)
     * Franklin Delano Roosevelt (just specify the subject if no particular event is
     * commemorated)
     */

}
