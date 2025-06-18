package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.jsoup.nodes.Element;

public class CategoryParser extends NumistaPartParser {

    public CategoryParser() {
        super((page, nType) -> {
            String categoryName = "";
            String nid = "";

            Element mainBreadcrumb = page.selectFirst("#main_breadcrumb");
            if (mainBreadcrumb != null) {
                Element categoryElement = mainBreadcrumb.selectFirst("a[href^=../index.php?ct=]");
                if (categoryElement != null) {
                    categoryName = categoryElement.attributes().get("href").replace("../index.php?ct=", "");
                    DebugUtil.printProperty("category", categoryName, true, true, true);
                }
                if (categoryName.isEmpty()) {
                    DebugUtil.showError(CategoryParser.class, "The Category on page for type with nid:" + nid + " is empty.");
                    return ParseEvent.ERROR;
                }

                if (nType.getCategory() != null && nType.getCategory().getName().equals(categoryName)) {
                    DebugUtil.showInfo(CategoryParser.class, "The Category on page is equal to Category in nType");
                    return ParseEvent.NOT_CHANGED;
                }


                Category categoryNode = N4JUtil.getInstance().numistaService.categoryService.findByName(categoryName);
                if (nType.getCategory() == null) {
                    DebugUtil.showInfo(CategoryParser.class, "For NType with nid: " + nType.getNid() + " set a Category: " + categoryNode.getName());
                } else {
                    DebugUtil.showInfo(CategoryParser.class, "For NType with nid: " + nType.getNid() + " change a Category from (" + nType.getCategory().getName() + ") to (" + categoryNode.getName() + ")");
                }

                nType.setCategory(categoryNode);

                return ParseEvent.CHANGED;
            }

            DebugUtil.showError(CategoryParser.class, "Can't find #main_breadcrumb on page nid: " + nType.getNid());
            return ParseEvent.ERROR;
        });
    }
}
