package bkv.colligendis;

import bkv.colligendis.services.NumistaServices;
import bkv.colligendis.services.ServiceUtils;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.NumistaEditPageUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
public class Application {




//    public final ServiceUtils serviceUtils;
    public final NumistaServices numistaServices;
    public Application(ServiceUtils serviceUtils, NumistaServices numistaServices) {
//        this.serviceUtils = serviceUtils;
        this.numistaServices = numistaServices;

//        NumistaUtil.InitInstance(serviceUtils);
        N4JUtil.InitInstance(numistaServices);

        System.out.println("ready");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

//        NumistaEditPageUtil numistaEditPageUtil = new NumistaEditPageUtil();


    }



//    @Bean
//    public HttpFirewall getHttpFirewall() {
//        StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
//        strictHttpFirewall.setAllowSemicolon(true);
//        strictHttpFirewall.setAllowUrlEncodedSlash(true);
//        return strictHttpFirewall;
//    }

}
