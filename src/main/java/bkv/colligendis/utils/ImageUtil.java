package bkv.colligendis.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageUtil {

    public static final String IMAGE_LOCATION_ROOT_PATH = "/Users/kirillbobryakov/IdeaProjects/ColligendisImageStore/Root/";



    public static void loadAndSaveImageToRoot(String link, String path){
        try {
            FileUtils.copyURLToFile(new URL(link), new File(IMAGE_LOCATION_ROOT_PATH + path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




}
