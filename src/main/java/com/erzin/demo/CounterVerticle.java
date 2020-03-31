package com.erzin.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Component
@Getter
@Setter
public class CounterVerticle extends AbstractVerticle {

    private static Integer CLICK_AMOUNT = Integer.MIN_VALUE;

    @Autowired
    ResourceLoader resourceLoader;


    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Vertx vertx = Vertx.factory.vertx();
        Router router = Router.router(vertx);

        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("X-PINGARUNER");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        /*
         * these methods aren't necessary for this sample,
         * but you may need them for your projects
         */
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);

        router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));


        router.post("/clickRegister").handler(this::clickRegister);
        router.get("/clickAmount").handler(this::clickAmount);
        vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
            if (result.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail("it failed");
            }
        });
    }

    private void clickRegister(RoutingContext routingContext) {
        CLICK_AMOUNT++;
        try {
            Resource resource = new ClassPathResource("clickAmount");
            InputStream input = resource.getInputStream();
            File file = resource.getFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append(getClickAmount().toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(CLICK_AMOUNT));
    }

    private void clickAmount(RoutingContext routingContext) {
        if (this.CLICK_AMOUNT == Integer.MIN_VALUE) {
            Resource resource = new ClassPathResource("clickAmount");
            try {
                InputStream input = resource.getInputStream();
                File file = resource.getFile();
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    Integer data = Integer.valueOf(myReader.nextLine());
                    CLICK_AMOUNT = data;
                }
                input.close();
            } catch (IOException e) {
                System.out.println("file not found");
            }
        }

        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(CLICK_AMOUNT));
    }

    public static Integer getClickAmount() {
        return CLICK_AMOUNT;
    }

    private void writeValueToFile() throws IOException {
        Resource resource = new ClassPathResource("clickAmount");
        File file = resource.getFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(getClickAmount());
        writer.close();
    }
}
