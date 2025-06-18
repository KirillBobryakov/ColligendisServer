package bkv.colligendis.utils.numista;

public class PrinterParser extends NumistaPartParser {

    public PrinterParser() {
        super((page, nType) -> {
            ParseEvent result = ParseEvent.NOT_CHANGED;

//            nType.getPrinters().clear();
//
//            Element printerTableElement = page.selectFirst("#mint_table");
//            if (printerTableElement != null) {
//                Elements selectElements = printerTableElement.select("select[class=printer_select]");
//                for (Element select : selectElements) {
//                    HashMap<String, String> mint = getAttributeWithTextSingleOption(select, "value");
//                    if (mint != null) {
//                        DebugUtil.printProperty("printer mint value", mint.get("value"), false, true, false);
//                        DebugUtil.printProperty("printer mint name", mint.get("text"), false, true, false);
//                        if (isValueAndTextNotNullAndNotEmpty(mint)) {
//                            nType.getPrinters().add(N4JUtil.getInstance().numistaService.printerService.findByNid(mint.get("value"), mint.get("text")));
//                        }
//                    }
//                }
//            }


            return result;
        });

        this.partName = "PrinterParser";
    }


}
