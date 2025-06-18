package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.entity.numista.CommemoratedEvent;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import lombok.NonNull;

public class CommemoratedEventParser extends NumistaPartParser {


    /*
    Specify the subject of the commemorative issue. Do not include dates. Format the subject according to the following examples:

        100th anniversary of the birth of Albert Einstein (only “Albert Einstein” in the title)
        100th anniversary of the Gotthard Railway (only “Gotthard Railway” in the title)
        150th anniversary of the death of Johann Heinrich Pestalozzi (only “Johann Heinrich Pestalozzi” in the title)
        500th anniversary of the Treaty of Stans (only “Treaty of Stans” in the title)
        600th anniversary of the Battle of Grunwald (only “Battle of Grunwald” in the title)
        Wedding of Prince Philip and Princess Mathilde (only “Wedding of Philip and Mathilde” in the title)
        Franklin Delano Roosevelt (just specify the subject if no particular event is commemorated)
     */

    public CommemoratedEventParser() {
        super((page, nType) -> {

            String evenement = getAttribute(page.selectFirst("#evenement"), "value");
            DebugUtil.printProperty("evenement", evenement, false, false, false);

            if (evenement != null && !evenement.isEmpty()) {

                if (nType.getCommemoratedEvent() != null) {
                    if (nType.getCommemoratedEvent().getName().equals(evenement)) {
                        DebugUtil.showInfo(CommemoratedEventParser.class, "The CommemoratedEvent of existing NType is equal with CommemoratedEvent on the page.");
                        return ParseEvent.NOT_CHANGED;
                    } else {
                        nType.setCommemoratedEvent(N4JUtil.getInstance().numistaService.commemoratedEventService.findByNameOrCreate(evenement));
                        DebugUtil.showWarning(CommemoratedEventParser.class, "The CommemoratedEvent of existing NType is not equal with CommemoratedEvent on the page.");
                        return ParseEvent.CHANGED;
                    }
                }

                nType.setCommemoratedEvent(N4JUtil.getInstance().numistaService.commemoratedEventService.findByNameOrCreate(evenement));
                return ParseEvent.CHANGED;

            }

            return ParseEvent.NOT_CHANGED;
        });
    }
}
