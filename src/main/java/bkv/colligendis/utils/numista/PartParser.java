package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.NType;
import org.jsoup.nodes.Document;

public interface PartParser {
    ParseEvent parse(Document page, NType nType);

}
