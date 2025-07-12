package bkv.colligendis.rest.catalogue.csi_statistics;

import lombok.Data;

@Data
public class CSISubject {
   private String name;
   private String numistaCode;

   public CSISubject(String name, String numistaCode) {
      this.name = name;
      this.numistaCode = numistaCode;
   }

}
