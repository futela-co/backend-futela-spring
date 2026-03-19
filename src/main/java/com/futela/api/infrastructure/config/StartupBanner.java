package com.futela.api.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class StartupBanner {

    private static final Logger log = LoggerFactory.getLogger(StartupBanner.class);

    private final Environment env;

    public StartupBanner(Environment env) {
        this.env = env;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        String port = env.getProperty("local.server.port", "8001");
        String profile = String.join(", ", env.getActiveProfiles().length > 0 ? env.getActiveProfiles() : new String[]{"default"});
        String dbUrl = env.getProperty("spring.datasource.url", "non configuré");
        String appName = env.getProperty("spring.application.name", "futela-api");

        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            host = "localhost";
        }

        log.info("""

        ╔══════════════════════════════════════════════════════════════════╗
        ║                                                                  ║
        ║   ███████╗██╗   ██╗████████╗███████╗██╗      █████╗             ║
        ║   ██╔════╝██║   ██║╚══██╔══╝██╔════╝██║     ██╔══██╗            ║
        ║   █████╗  ██║   ██║   ██║   █████╗  ██║     ███████║            ║
        ║   ██╔══╝  ██║   ██║   ██║   ██╔══╝  ██║     ██╔══██║            ║
        ║   ██║     ╚██████╔╝   ██║   ███████╗███████╗██║  ██║            ║
        ║   ╚═╝      ╚═════╝    ╚═╝   ╚══════╝╚══════╝╚═╝  ╚═╝            ║
        ║                                                                  ║
        ║   Plateforme immobiliere multi-tenant pour la RDC                ║
        ║   Spring Boot 3.4 | Java 21 | Architecture Hexagonale           ║
        ║                                                                  ║
        ╠══════════════════════════════════════════════════════════════════╣
        ║                                                                  ║
        ║   Application :  {}
        ║   Profil      :  {}
        ║   Port        :  {}
        ║   Host        :  {}
        ║   Database    :  {}
        ║                                                                  ║
        ╠══════════════════════════════════════════════════════════════════╣
        ║                                                                  ║
        ║   API Base     :  http://localhost:{}/api
        ║   Swagger UI   :  http://localhost:{}/swagger-ui.html
        ║   API Docs     :  http://localhost:{}/api-docs
        ║   Health       :  http://localhost:{}/actuator/health
        ║                                                                  ║
        ╠══════════════════════════════════════════════════════════════════╣
        ║                                                                  ║
        ║   Modules : Auth | Address | Property | Reservation | Rent       ║
        ║             Payment (FlexPay) | Messaging | Review               ║
        ║                                                                  ║
        ║   Endpoints    :  ~80 REST APIs                                  ║
        ║   Multi-tenant :  Hibernate Filter (company_id)                  ║
        ║   Schedulers   :  5 (invoices, reminders, overdue, FlexPay)      ║
        ║                                                                  ║
        ╚══════════════════════════════════════════════════════════════════╝
        """,
                appName, profile, port, host, truncate(dbUrl, 50),
                port, port, port, port
        );
    }

    private String truncate(String value, int max) {
        if (value == null) return "N/A";
        return value.length() > max ? value.substring(0, max - 3) + "..." : value;
    }
}
