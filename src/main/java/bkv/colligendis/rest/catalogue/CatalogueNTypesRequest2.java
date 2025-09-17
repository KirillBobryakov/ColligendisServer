package bkv.colligendis.rest.catalogue;

import java.util.List;

import bkv.colligendis.rest.dto.ItemTypeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CatalogueNTypesRequest2 {
    private String countryNumistaCode;
    private String subjectNumistaCode;
    private String issuerCode;

    private List<ItemTypeDTO> itemTypes;

    private String currencyNid;
}
