package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import org.jsoup.nodes.Element;

public class CategoryParser extends NumistaPartParser {

    public CategoryParser() {
        super((document, nType) -> {
            String categoryName = "";
            String nid = "";

            Element mainBreadcrumb = document.selectFirst("#main_breadcrumb");
            if (mainBreadcrumb != null) {
                Element categoryElement = mainBreadcrumb.selectFirst("a[href^=../index.php?ct=]");
                if (categoryElement != null) {
                    categoryName = categoryElement.attributes().get("href").replace("../index.php?ct=", "");
                    DebugUtil.printProperty("category", categoryName, true, true, true);
                }
                if (categoryName.isEmpty()) {
                    DebugUtil.showError(CategoryParser.class, "Category on page for type with nid:" + nid + " is empty.");
                    return false;
                }

                if (nType.getCategory() != null && nType.getCategory().getName().equals(categoryName)) {
                    return true;
                }

                Category categoryNode = N4JUtil.getInstance().numistaService.categoryService.findByName(categoryName);
                nType.setCategory(categoryNode);

                DebugUtil.showInfo(CategoryParser.class, "For NType with nid: " + nType.getNid() + " set new Category: " + nType.getCategory().getName());

                return true;
            }

            DebugUtil.showError(CategoryParser.class, "Can't find #main_breadcrumb on page nid: " + nType.getNid());
            return false;
        });
    }
}
