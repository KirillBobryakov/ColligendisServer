package bkv.colligendis.rest.catalogue.csi_statistics;

import lombok.Data;

@Data
public class CSIIssuer {
   private String name;
   private String code;

   public CSIIssuer(String name, String code) {
      this.name = name;
      this.code = code;
   }
}
