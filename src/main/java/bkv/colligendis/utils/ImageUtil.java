package bkv.colligendis.utils;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.NTypePart;
import bkv.colligendis.utils.numista.PART_TYPE;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageUtil {

    public static final String IMAGE_LOCATION_ROOT_PATH = "/Users/kirillbobryakov/IdeaProjects/ColligendisImageStore/Root/numista/";



    public static boolean saveNumistaImage(NType nType, PART_TYPE partType){

        if (nType == null ) return false;

        NTypePart nTypePart = null;
        switch (partType) {
            case OBVERSE:
                nTypePart = nType.getObverse();
                break;
            case REVERSE:
                nTypePart = nType.getReverse();
                break;
            case EDGE:
                nTypePart = nType.getEdge();
                break;
            case WATERMARK:
                nTypePart = nType.getWatermark();
                break;
        }

        if(nTypePart == null || nTypePart.getPicture() == null || nTypePart.getPicture().isEmpty()) return false;



        String localPath = IMAGE_LOCATION_ROOT_PATH;


        // CollectibleType in path
        CollectibleType nTypeCollectibleType = nType.getCollectibleType();
        while (nTypeCollectibleType.getCollectibleTypeParent() != null){
            nTypeCollectibleType = nTypeCollectibleType.getCollectibleTypeParent();
        }

        localPath += nTypeCollectibleType.getName() + "/";

        // Issuer in path
        //todo changing Issuer must be relocate images
        if(nType.getIssuer() != null && nType.getIssuer().getCode() != null) {
            localPath += nType.getIssuer().getCode() + "/";
        }

        // Nid + PartType in path
        localPath += nType.getNid() + "_" + partType.toString().toLowerCase() + "_origin.jpg";


        if (Files.exists(Paths.get(localPath))) return true;


        String link = nTypePart.getPicture().replace("..", "https://en.numista.com/catalogue");

        try {
            FileUtils.copyURLToFile(new URL(link), new File(localPath));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }


    public static void loadAndSaveImageToRoot(String link, String path){
        try {
            FileUtils.copyURLToFile(new URL(link), new File(IMAGE_LOCATION_ROOT_PATH + path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




}
