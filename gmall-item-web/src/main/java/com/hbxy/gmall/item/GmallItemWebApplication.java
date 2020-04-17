package com.hbxy.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.hbxy.gmall")
@SpringBootApplication
public class GmallItemWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallItemWebApplication.class, args);
    }

}
