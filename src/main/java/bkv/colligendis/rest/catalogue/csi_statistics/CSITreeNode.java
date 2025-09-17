package bkv.colligendis.rest.catalogue.csi_statistics;

import java.util.List;

import lombok.Data;

@Data
public class CSITreeNode {

    String type;
    String name;
    String code;
    List<String> ruAlternativeNames;
    List<CSITreeNode> children;

}
