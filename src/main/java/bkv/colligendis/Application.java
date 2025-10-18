package bkv.colligendis;

import bkv.colligendis.services.MeshokServices;
import bkv.colligendis.services.NumistaServices;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.numista.parser.PageParser;

import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
// @EnableTransactionManagement
public class Application {

    public final NumistaServices numistaServices;
    public final MeshokServices meshokServices;

    public Application(NumistaServices numistaServices, MeshokServices meshokServices) {

        this.numistaServices = numistaServices;
        this.meshokServices = meshokServices;
        N4JUtil.InitInstance(numistaServices, meshokServices);

        // N4JUtil.getInstance().numistaService.initData();

        // PageParser.parse.accept(Stream.of("209129"));

        // EditPageParser.parse.accept(Stream.of("209129"));

        PageParser.parse.accept(Stream.of("28972", "209130", "20930", "268884",
                "14640", "210635"));
        // PageParser.parse.accept(Stream.of("268884"));

        // https://en.numista.com/catalogue/index.php?e=germany&r=&st=148&cat=y&im1=&im2=&ru=&ie=&ca=3&no=&v=&a=&dg=&i=&b=&m=&f=&t=&t2=&w=&mt=&u=&g=&c=&wi=&sw=
        // https://en.numista.com/catalogue/index.php?e=germany&r=&st=148&cat=y&im1=&im2=&ru=&ie=&ca=3&no=&v=&a=&dg=&i=&b=&m=&f=&t=&t2=&w=&mt=&u=&g=&c=&wi=&sw=&q=200
        // https://en.numista.com/catalogue/index.php?e=germany&r=&st=147&cat=y&im1=&im2=&ru=&ie=&ca=3&no=&v=&a=&dg=&i=&b=&m=&f=&t=&t2=&w=&mt=&u=&g=&c=&wi=&sw=&p=2

        // List<MeshokLot> lots =
        // N4JUtil.getInstance().meshokService.meshokLotService.findAllLimitedWithCategory(100);
        // for (MeshokLot lot : lots) {
        // ImageUtil.saveMeshokImage(lot);
        // System.out.println();
        // }

    }

    public static void main(String[] args) {
        // Clear console on application start
        System.out.print("\033[H\033[2J");
        System.out.flush();

        SpringApplication.run(Application.class, args);
    }

}
