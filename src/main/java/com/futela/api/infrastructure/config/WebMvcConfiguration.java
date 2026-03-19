package com.futela.api.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
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

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new CaseInsensitiveEnumConverterFactory());
    }

    /**
     * Allows case-insensitive enum deserialization for @RequestParam values.
     * E.g., "house" will match PropertyType.HOUSE.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class CaseInsensitiveEnumConverterFactory implements ConverterFactory<String, Enum> {
        @Override
        public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
            return source -> {
                try {
                    return (T) Enum.valueOf(targetType, source.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return (T) Enum.valueOf(targetType, source);
                }
            };
        }
    }
}
