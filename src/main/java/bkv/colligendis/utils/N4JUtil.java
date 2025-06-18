package bkv.colligendis.utils;

import bkv.colligendis.database.entity.numista.Category;
import bkv.colligendis.services.NumistaServices;

public class N4JUtil {


    //clear database
    // match (n) WHERE NOT n:NTYPE AND NOT n:COUNTRY AND NOT n:SUBJECT AND NOT n:ISSUER AND NOT n:USER return n

    private static N4JUtil instance;


    public final NumistaServices numistaService;


    private N4JUtil(NumistaServices numistaService) {
        this.numistaService = numistaService;
    }

    public static void InitInstance(NumistaServices numistaService) {
        instance = new N4JUtil(numistaService);
    }

    public static synchronized N4JUtil getInstance() {
        if (instance == null) {
            try {
                throw new Exception("N4JUtil's Instance was forgotten to be initialized");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }


    public void initN4JDB(){
        initCategories();
    }

    //Numista type's categories = coin, banknote, exonumia and unknown
    private void initCategories(){
        numistaService.categoryService.findByName(Category.COIN);
        numistaService.categoryService.findByName(Category.BANKNOTE);
        numistaService.categoryService.findByName(Category.EXONUMIA);
        numistaService.categoryService.findByName(Category.UNKNOWN);
    }


}
