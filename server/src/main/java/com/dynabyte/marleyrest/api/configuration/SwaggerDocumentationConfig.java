package com.dynabyte.marleyrest.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration file for Swagger Documentation. Automatically generates api documentation for all endpoints as well as
 * transfer objects, i.e. request or response objects
 */
@Configuration
public class SwaggerDocumentationConfig {

    private static final Contact DEFAULT_CONTACT = new Contact("Dynabyte", "https://dynabyte.com", "daniel.hughes@dynabyte.com");
    public static final ApiInfo DEFAULT_API_INFO = new ApiInfo(
            "Marley Java API", "Communication hub for Project Marley", "1.0",
            "N/A", DEFAULT_CONTACT,
            "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES =
            new HashSet<>(Collections.singletonList("application/json"));


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(DEFAULT_API_INFO)
                .produces(DEFAULT_PRODUCES_AND_CONSUMES)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMES)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dynabyte.marleyrest"))
                .paths(PathSelectors.any())
                .build();
    }
}
