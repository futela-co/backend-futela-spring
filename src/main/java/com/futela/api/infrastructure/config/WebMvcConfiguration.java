package com.futela.api.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures Spring MVC to accept application/merge-patch+json content type
 * for PATCH requests (API Platform / Symfony compatibility).
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * Custom MediaType for merge-patch+json (used by API Platform / Symfony PATCH)
     */
    public static final MediaType APPLICATION_MERGE_PATCH_JSON =
            new MediaType("application", "merge-patch+json");

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }
}
