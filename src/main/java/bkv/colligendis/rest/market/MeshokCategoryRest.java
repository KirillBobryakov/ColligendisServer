package bkv.colligendis.rest.market;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeshokCategoryRest {
    private Integer mid;
    private String name;
    private Integer parentMid;
    private Integer level;

}
