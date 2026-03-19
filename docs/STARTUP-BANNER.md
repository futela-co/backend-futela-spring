# Startup Banner & Port Fallback

> Au démarrage de l'application, un résumé complet s'affiche dans les logs.

---

## Banner de démarrage

Lorsque l'application démarre, le banner suivant apparaît dans les logs :

```
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
║   Application :  futela-api                                      ║
║   Profil      :  dev                                             ║
║   Port        :  8002  (8001 déjà utilisé, basculement auto)    ║
║   Host        :  localhost                                       ║
║   Database    :  jdbc:postgresql://localhost:5432/futela_spring   ║
║                                                                  ║
╠══════════════════════════════════════════════════════════════════╣
║                                                                  ║
║   API Base     :  http://localhost:8002/api                       ║
║   Swagger UI   :  http://localhost:8002/swagger-ui.html          ║
║   API Docs     :  http://localhost:8002/api-docs                 ║
║   Health       :  http://localhost:8002/actuator/health          ║
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
```

### Informations affichées

| Section | Contenu |
|---------|---------|
| **Header** | Logo ASCII Futela + description du projet |
| **Application** | Nom de l'app (`futela-api`) |
| **Profil** | Profil Spring actif (`dev`, `prod`, `default`) |
| **Port** | Port réel utilisé (avec mention si fallback) |
| **Host** | Nom de la machine |
| **Database** | URL JDBC de la base de données |
| **URLs** | Liens cliquables vers l'API, Swagger, API Docs, Health check |
| **Modules** | Liste des 8 modules métier actifs |
| **Endpoints** | Nombre total d'endpoints REST |
| **Schedulers** | Nombre et types de tâches planifiées |

---

## Port Fallback automatique

### Le problème

Si le port configuré (par défaut `8001`) est déjà utilisé par un autre processus (ex: le backend Symfony), Spring Boot crashe avec :

```
Web server failed to start. Port 8001 was already in use.
```

### La solution

`PortConfiguration` détecte automatiquement si le port est pris et bascule sur le prochain port disponible :

```
Port 8001 → occupé (Symfony)
Port 8002 → libre ✓ → démarrage sur 8002
```

### Comment ça fonctionne

```java
@Configuration
public class PortConfiguration {

    @Value("${server.port:8001}")
    private int configuredPort;

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> portCustomizer() {
        return factory -> {
            if (!isPortAvailable(configuredPort)) {
                int fallbackPort = findAvailablePort(configuredPort + 1, configuredPort + 100);
                log.warn("Port {} déjà utilisé, basculement sur le port {}", configuredPort, fallbackPort);
                factory.setPort(fallbackPort);
            }
        };
    }
}
```

### Comportement

| Situation | Résultat | Log |
|-----------|----------|-----|
| Port 8001 libre | Démarre sur 8001 | `INFO Démarrage sur le port 8001` |
| Port 8001 pris | Démarre sur 8002 | `WARN Port 8001 déjà utilisé, basculement sur le port 8002` |
| Ports 8001-8005 pris | Démarre sur 8006 | `WARN Port 8001 déjà utilisé, basculement sur le port 8006` |
| Ports 8001-8100 tous pris | Erreur | `IllegalStateException: Aucun port disponible entre 8002 et 8101` |

### Forcer un port spécifique

```bash
# Via variable d'environnement
SERVER_PORT=9090 mvn spring-boot:run

# Via argument JVM
mvn spring-boot:run -Dserver.port=9090

# Via application.yml
server:
  port: 9090
```

---

## Fichiers source

| Fichier | Rôle |
|---------|------|
| `infrastructure/config/StartupBanner.java` | Affiche le banner au démarrage via `@EventListener(ApplicationReadyEvent.class)` |
| `infrastructure/config/PortConfiguration.java` | Détecte le port libre et configure le `WebServerFactory` |
| `application.yml` | Port par défaut : `${SERVER_PORT:8001}` |

---

## Exemple de log complet au démarrage

```
2026-03-19 12:05:00 INFO  --- [main] c.f.a.i.c.PortConfiguration : Démarrage sur le port 8001
2026-03-19 12:05:02 INFO  --- [main] o.s.b.w.e.t.TomcatWebServer : Tomcat started on port 8001
2026-03-19 12:05:02 INFO  --- [main] c.f.a.FutelaApplication     : Started FutelaApplication in 2.1s
2026-03-19 12:05:02 INFO  --- [main] c.f.a.i.c.StartupBanner     :

        ╔══════════════════════════════════════════════════════════════════╗
        ║   ███████╗██╗   ██╗████████╗███████╗██╗      █████╗             ║
        ║   ...                                                            ║
        ║   Application :  futela-api                                      ║
        ║   Port        :  8001                                            ║
        ║   API Base     :  http://localhost:8001/api                       ║
        ║   Swagger UI   :  http://localhost:8001/swagger-ui.html          ║
        ╚══════════════════════════════════════════════════════════════════╝

2026-03-19 12:05:02 INFO  --- [main] c.f.a.i.c.DataSeederConfiguration : [DEV] Données de seed disponibles
```

Ou avec fallback :

```
2026-03-19 12:05:00 WARN  --- [main] c.f.a.i.c.PortConfiguration : Port 8001 déjà utilisé, basculement sur le port 8002
2026-03-19 12:05:02 INFO  --- [main] o.s.b.w.e.t.TomcatWebServer : Tomcat started on port 8002
2026-03-19 12:05:02 INFO  --- [main] c.f.a.i.c.StartupBanner     :

        ╔══════════════════════════════════════════════════════════════════╗
        ║   ...                                                            ║
        ║   Port        :  8002                                            ║
        ║   API Base     :  http://localhost:8002/api                       ║
        ╚══════════════════════════════════════════════════════════════════╝
```
