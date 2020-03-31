package com.erzin.demo;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;

@SpringBootApplication
public class DemoApplication {


    @Autowired
    private CounterVerticle counterVerticle;


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @PostConstruct
    public void deployVerticle() {
        Vertx.vertx().deployVerticle(counterVerticle);
    }

//    @PreDestroy
//    public void destroyApp() {
//        System.out.println("closing" + CounterVerticle.getClickAmount());
//
//        try {
//            Resource resource = new ClassPathResource("clickAmount");
//            File file = resource.getFile();
//            FileWriter fileWriter = new FileWriter(file);
//            fileWriter.write("hello worlds");
//            fileWriter.close();
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//    }
}
