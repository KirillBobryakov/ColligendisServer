package bkv.colligendis.utils.numista.issuer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumistaIssuerResultItem {
    private String id;
    private String text;
    private int level;

    @JsonProperty("short")
    private int shortValue; // Renamed from "short" as "short" is a Java keyword
    private String flag_type;
    private String flag_code;
    private int section;
    private int period;
    private boolean disabled;
    private boolean hide;
}
