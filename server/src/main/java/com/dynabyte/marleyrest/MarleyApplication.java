package com.dynabyte.marleyrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

/**
 * Runs the rest api spring boot application
 */
@SpringBootApplication
public class MarleyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarleyApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/"), req ->
                ServerResponse.temporaryRedirect(URI.create("/documentation/swagger-ui/index.html#/marley-rest-controller")).build());
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
