package com.tatardancespace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TatarDanceSpaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TatarDanceSpaceApplication.class, args);
    }

}
