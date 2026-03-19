package com.futela.api.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Configures Spring to accept application/merge-patch+json as a JSON content type.
 * This is required for Symfony API Platform compatibility, where the frontend
 * sends PATCH requests with Content-Type: application/merge-patch+json.
 */
@Configuration
public class MergePatchJsonConfiguration implements WebMvcConfigurer {

    private static final MediaType APPLICATION_MERGE_PATCH_JSON =
            new MediaType("application", "merge-patch+json");

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                var supportedMediaTypes = new ArrayList<>(jacksonConverter.getSupportedMediaTypes());
                supportedMediaTypes.add(APPLICATION_MERGE_PATCH_JSON);
                jacksonConverter.setSupportedMediaTypes(supportedMediaTypes);
            }
        }
    }
}
