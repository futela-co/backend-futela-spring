package com.futela.api.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.ServerSocket;

@Configuration
public class PortConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PortConfiguration.class);

    @Value("${server.port:8001}")
    private int configuredPort;

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> portCustomizer() {
        return factory -> {
            if (!isPortAvailable(configuredPort)) {
                int fallbackPort = findAvailablePort(configuredPort + 1, configuredPort + 100);
                log.warn("Port {} déjà utilisé, basculement sur le port {}", configuredPort, fallbackPort);
                factory.setPort(fallbackPort);
            } else {
                log.info("Démarrage sur le port {}", configuredPort);
            }
        };
    }

    private boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private int findAvailablePort(int from, int to) {
        for (int port = from; port <= to; port++) {
            if (isPortAvailable(port)) {
                return port;
            }
        }
        throw new IllegalStateException("Aucun port disponible entre " + from + " et " + to);
    }
}
