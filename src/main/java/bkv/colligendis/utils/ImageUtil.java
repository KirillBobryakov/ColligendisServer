package bkv.colligendis.utils;

import bkv.colligendis.database.entity.meshok.MeshokCategory;
import bkv.colligendis.database.entity.meshok.MeshokLot;
import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.NTypePart;
import bkv.colligendis.utils.meshok.data.Thumbnail;
import bkv.colligendis.utils.numista.PART_TYPE;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageUtil {

    public static final String IMAGE_LOCATION_ROOT_PATH = "/Users/kirillbobryakov/IdeaProjects/ColligendisImageStore/Root/numista/";

    public static final String IMAGE_LOCATION_ROOT_PATH_MESHOK = "/Users/kirillbobryakov/IdeaProjects/ColligendisImageStore/Root/meshok/";

    public static boolean saveMeshokImage(MeshokLot lot) {

        if (lot == null)
            return false;

        if (lot.getCategory() == null)
            return false;

        for (int i = 0; i < lot.getPictures().size(); i++) {
            String localPath = getMeshokLotLocalImagePath(lot, i);

            if (localPath == null)
                continue;

            if (Files.exists(Paths.get(localPath)))
                continue;

            String link = "https://meshok.ru" + lot.getPictures().get(i);

            int maxRetries = 3;
            int retries = 0;
            boolean success = false;
            while (retries < maxRetries && !success) {
                try {
                    FileUtils.copyURLToFile(new URL(link), new File(localPath));
                    success = true;
                    break;
                } catch (IOException e) {
                    retries++;
                    try {
                        Thread.sleep(new Random().nextInt(100, 200));
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            if (!success) {
                System.out.println("Failed to save image: " + localPath);
            } else {
                System.out.println("Saved image: " + localPath);
            }
        }

        return true;
    }

    public static boolean saveNumistaImage(NType nType, PART_TYPE partType) {

        if (nType == null)
            return false;

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

        if (nTypePart == null || nTypePart.getPicture() == null || nTypePart.getPicture().isEmpty())
            return false;

        String localPath = ImageUtil.getNTypeLocalImagePath(nType.getNid(), partType, IMAGE_SIZE.ORIGIN);

        if (localPath == null)
            return false;

        // Check if file exists
        if (Files.exists(Paths.get(localPath)))
            return true;

        String link = nTypePart.getPicture().replace("..", "https://en.numista.com/catalogue");

        try {
            FileUtils.copyURLToFile(new URL(link), new File(localPath));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static String getMeshokLotLocalImagePath(MeshokLot lot, int imageNumber) {

        String localPath = IMAGE_LOCATION_ROOT_PATH_MESHOK;

        MeshokCategory category = N4JUtil.getInstance().meshokService.meshokCategoryService
                .findByMid(lot.getCategory().getMid());

        String meshokCategoryStringPath = category.getName().replace(" ", "_").toLowerCase();
        while (category.getParent() != null) {
            category = category.getParent();
            meshokCategoryStringPath = category.getName().replace(" ", "_").toLowerCase() + "/"
                    + meshokCategoryStringPath;
        }

        localPath += meshokCategoryStringPath + "/" + lot.getMid() + "." + imageNumber + ".jpg";
        return localPath;
    }

    public static String getNTypeLocalImagePath(String nid, PART_TYPE partType, IMAGE_SIZE imageSize) {

        String localPath = IMAGE_LOCATION_ROOT_PATH;

        // CollectibleType in path
        CollectibleType nTypeCollectibleType = N4JUtil.getInstance().numistaService.collectibleTypeService
                .findByNTypeNid(nid);
        if (nTypeCollectibleType == null)
            return null;

        String collectibleTypeStringPath = nTypeCollectibleType.getName().replace(" ", "_").toLowerCase();
        while (nTypeCollectibleType.getCollectibleTypeParent() != null) {
            nTypeCollectibleType = nTypeCollectibleType.getCollectibleTypeParent();
            collectibleTypeStringPath = nTypeCollectibleType.getName().replace(" ", "_").toLowerCase() + "/"
                    + collectibleTypeStringPath;
        }

        localPath += collectibleTypeStringPath + "/";

        Issuer nTypeIssuer = N4JUtil.getInstance().numistaService.issuerService.findByNTypeNid(nid);
        // Issuer in path
        // todo changing Issuer must be relocate images
        if (nTypeIssuer != null && nTypeIssuer.getCode() != null) {
            localPath += nTypeIssuer.getCode() + "/";
        }

        // Nid + PartType in path

        if (imageSize == IMAGE_SIZE.SMALL) {
            localPath += nid + "_" + partType.toString().toLowerCase() + "_small.jpg";
        } else if (imageSize == IMAGE_SIZE.MEDIUM) {
            localPath += nid + "_" + partType.toString().toLowerCase() + "_medium.jpg";
        } else {
            localPath += nid + "_" + partType.toString().toLowerCase() + "_origin.jpg";
        }

        return localPath;
    }

    public static Resource getNTypeLocalImage(String nid, PART_TYPE partType, IMAGE_SIZE imageSize) {

        String localPath = ImageUtil.getNTypeLocalImagePath(nid, partType, imageSize);
        if (localPath == null)
            return null;

        Path path = Paths.get(localPath);
        // Check if file exists
        if (!Files.exists(path)) {
            if (imageSize == IMAGE_SIZE.SMALL || imageSize == IMAGE_SIZE.MEDIUM) {
                String localPathForOrigin = ImageUtil.getNTypeLocalImagePath(nid, partType, IMAGE_SIZE.ORIGIN);
                Path pathForOrigin = Paths.get(localPathForOrigin);
                if (Files.exists(pathForOrigin)) {
                    try {
                        Thumbnails.of(pathForOrigin.toFile()).height(150).toFile(localPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (!Files.exists(path)) {
                throw new RuntimeException("Image not found: " + localPath);
            }
            return new FileSystemResource(path.toFile());
        }

        // Create resource from file
        return new FileSystemResource(path.toFile());
    }

    public static Resource getMeshokLotLocalImage(MeshokLot lot, int imageNumber) {

        String localPath = ImageUtil.getMeshokLotLocalImagePath(lot, imageNumber);
        if (localPath == null)
            return null;

        Path path = Paths.get(localPath);
        // Check if file exists
        if (!Files.exists(path)) {
            return null;
        }

        // Create resource from file
        return new FileSystemResource(path.toFile());
    }
}
