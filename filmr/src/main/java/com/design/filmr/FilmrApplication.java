package com.design.filmr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.design.filmr.*.mapper")
public class FilmrApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmrApplication.class, args);
        System.out.println("OK");
    }

}
