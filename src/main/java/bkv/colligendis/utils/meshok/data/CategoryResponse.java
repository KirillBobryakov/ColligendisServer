package bkv.colligendis.utils.meshok.data;

import java.util.List;

import lombok.Data;

@Data
public class CategoryResponse {

    String correlationId;
    List<Category> result;

}
