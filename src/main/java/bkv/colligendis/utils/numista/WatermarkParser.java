package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.NTypePart;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class WatermarkParser extends NumistaPartParser {

    public WatermarkParser() {
        super((page, nType) -> {
            ParseEvent result = ParseEvent.NOT_CHANGED;

            ParseEvent[] parseFunctions = new ParseEvent[] {
                    parseWatermarkDescription(page, nType),
                    parseWatermarkPicture(page, nType),
            };

            for (ParseEvent partResult : parseFunctions) {
                if (partResult == ParseEvent.ERROR) {
                    return ParseEvent.ERROR;
                } else if (partResult == ParseEvent.CHANGED) {
                    result = ParseEvent.CHANGED;
                }
            }

            return result;
        });

        this.partName = "WatermarkParser";
    }

    private static ParseEvent parseWatermarkDescription(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;

        String descriptionWatermark = getTagText(page.selectFirst("#description_watermark"));
        if (descriptionWatermark != null && !descriptionWatermark.isEmpty()) {
            if(nType.getWatermark() == null) nType.setWatermark(new NTypePart(PART_TYPE.WATERMARK));

            nType.getWatermark().setDescription(descriptionWatermark);
            result = ParseEvent.CHANGED;
        }
        return result;
    }

    private static ParseEvent parseWatermarkPicture(Document page, NType nType) {
        ParseEvent result = ParseEvent.NOT_CHANGED;
        Element legendWatermarkElement = page.selectFirst("fieldset:contains(Watermark)");
        if (legendWatermarkElement != null) {
            String watermarkPhoto = getAttribute(legendWatermarkElement.selectFirst("a[target=_blank]"), "href");
            if (watermarkPhoto != null && !watermarkPhoto.isEmpty() && !watermarkPhoto.equals("/vous/votre_compte.php#picture_license")) {
                if(nType.getWatermark() == null) nType.setWatermark(new NTypePart(PART_TYPE.WATERMARK));

                nType.getWatermark().setPicture(watermarkPhoto);
                result = ParseEvent.CHANGED;
            } else if(nType.getWatermark().getPicture() != null && !nType.getWatermark().getPicture().isEmpty()){
                nType.getWatermark().setPicture(null);
                result = ParseEvent.CHANGED;
            }
        }
        return result;
    }

}
