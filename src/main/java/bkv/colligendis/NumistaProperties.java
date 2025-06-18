package bkv.colligendis;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Scope("singleton")
@Data
public class NumistaParserProperties {

    @Value("${spring.colligendis.numista.parser.showPageAfterLoad}")
    private boolean showPageAfterLoad;

    @Value("${spring.colligendis.numista.parser.withTimeMetrics}")
    private boolean withTimeMetrics;

    @Value("${spring.colligendis.numista.userAgent}")
    private String userAgent;

    @Value("${spring.colligendis.numista.cookie}")
    private String cookie;



}
