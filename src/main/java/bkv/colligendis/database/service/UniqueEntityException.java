package bkv.colligendis.database.service;

import lombok.Data;

import java.util.Map;

@Data
public class UniqueEntityException extends Exception{

    public final Map<String, String> params;
    public UniqueEntityException(Map<String, String> params){
        super("More then 1 Unique Entity in Graph");
        this.params = params;
    }
}
