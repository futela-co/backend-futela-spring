package com.futela.api.infrastructure.config;

import com.futela.api.infrastructure.security.CorsProperties;
import com.futela.api.infrastructure.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class})
public class WebConfiguration {
}
