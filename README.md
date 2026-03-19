# Futela API — Backend Spring Boot

> Plateforme immobilière multi-tenant pour la RDC.
> Location, vente, réservation, paiement mobile (FlexPay), messagerie temps réel.

---

## Stack technique

| Technologie | Version | Rôle |
|---|---|---|
| Java | 21 LTS | Langage (Virtual Threads) |
| Spring Boot | 3.4.4 | Framework principal |
| Spring Security | 6.x | JWT + RBAC |
| Spring Data JPA | 3.x | Accès données (Hibernate 6) |
| PostgreSQL | 16+ | Base de données |
| Flyway | 10+ | Migrations de schéma |
| Redis | 7+ | Cache applicatif |
| MapStruct | 1.6 | Mapping DTO compile-time |
| Lombok | 1.18 | Réduction boilerplate |
| jjwt | 0.12 | JWT tokens |
| SpringDoc OpenAPI | 2.8 | Swagger UI |
| Cloudinary | 2.x | Stockage photos |
| FlexPay | — | Paiement mobile money (Mpesa, Airtel, Orange) |

---

## Architecture

Architecture **hexagonale (Ports & Adapters)** avec séparation stricte :

```
com.futela.api
├── domain/          # Records Java purs (0 dépendances framework)
│   ├── model/       # Modèles métier immutables
│   ├── enums/       # 22 enums métier
│   ├── port/in/     # Interfaces use cases
│   ├── port/out/    # Interfaces repositories (ports sortants)
│   ├── event/       # Événements métier
│   └── exception/   # Exceptions métier
├── application/     # Orchestration
│   ├── usecase/     # Services @Transactional
│   ├── dto/         # Request/Response records
│   ├── mapper/      # Mappers réponse
│   └── service/     # Services transverses
├── infrastructure/  # Technique
│   ├── persistence/ # JPA entities, repos, adapters, mappers
│   ├── security/    # JWT, Spring Security
│   ├── config/      # Configurations Spring
│   ├── integration/ # FlexPay, Cloudinary, Email, SMS
│   ├── event/       # Event listeners
│   └── scheduler/   # Tâches planifiées (@Scheduled)
└── presentation/    # REST API
    ├── controller/  # Controllers REST
    ├── advice/      # Exception handlers
    └── filter/      # Request filters
```

---

## Modules métier

| Module | Description | Entités |
|---|---|---|
| **Core** | Company (tenant root), PlatformSettings | 2 |
| **Auth/User** | JWT, sessions, Google OAuth, profils | 3 |
| **Address** | Hiérarchie géo RDC (Country→Province→City→Town→District) | 6 |
| **Category** | Catégorisation des propriétés | 1 |
| **Property** | 5 types STI (Apartment, House, Land, Car, EventHall) + Photos + Favoris | 4 |
| **Reservation** | Réservations courte durée + Visites | 2 |
| **Review** | Avis et notes avec modération | 1 |
| **Rent** | Baux, factures, paiements, échéanciers, rappels | 5 |
| **Payment** | Transactions FlexPay, devises, webhook | 3 |
| **Messaging** | Conversations, messages, notifications, contacts | 4 |

---

## Prérequis

- **Java 21** (JDK)
- **Maven 3.9+**
- **PostgreSQL 16+**
- **Redis 7+** (optionnel, pour le cache)

---

## Installation locale

### 1. Cloner le projet

```bash
git clone https://github.com/futela-co/backend-futela-spring.git
cd backend-futela-spring
```

### 2. Créer la base de données

```bash
psql -U postgres -c "CREATE DATABASE futela_spring;"
```

### 3. Configurer les variables d'environnement

Créer un fichier `.env.local` (non versionné) :

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=futela_spring
DB_USERNAME=postgres
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your-super-secret-key-minimum-256-bits-change-in-production

# FlexPay (laisser vide pour le mode mock)
FLEXPAY_BASE_URL=
FLEXPAY_MERCHANT=
FLEXPAY_API_KEY=
FLEXPAY_CALLBACK_URL=

# Cloudinary (laisser vide si pas de photos)
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=

# CORS
CORS_ORIGINS=http://localhost:3006,http://localhost:3007

# Redis (optionnel)
REDIS_HOST=localhost
REDIS_PORT=6379
```

### 4. Lancer les migrations Flyway

```bash
mvn flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/futela_spring \
  -Dflyway.user=postgres \
  -Dflyway.password=your_password
```

Les 9 migrations créent toutes les tables et insèrent les données de seed (géographie RDC, devises, fixtures).

### 5. Compiler et tester

```bash
# Compiler
mvn clean compile

# Lancer les tests (305 tests)
mvn clean test

# Lancer l'application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 6. Vérifier

- **API** : http://localhost:8001/api/countries
- **Swagger UI** : http://localhost:8001/swagger-ui.html
- **API Docs** : http://localhost:8001/api-docs

### Comptes de test (fixtures)

| Email | Mot de passe | Rôle |
|---|---|---|
| `admin@futela.com` | `password123` | SUPER_ADMIN |
| `admin@futela-immo.com` | `password123` | ADMIN |
| `owner1@futela.com` | `password123` | OWNER |
| `tenant1@futela.com` | `password123` | TENANT |

---

## Endpoints API principaux

### Authentification
```
POST /api/auth/login              # Connexion
POST /api/auth/register           # Inscription
POST /api/auth/refresh            # Rafraîchir le token
POST /api/auth/logout             # Déconnexion
GET  /api/auth/me                 # Profil connecté
```

### Propriétés
```
GET  /api/properties              # Recherche (public)
GET  /api/properties/{id}         # Détail
POST /api/properties              # Créer (auth)
POST /api/properties/{id}/publish # Publier
```

### Réservations
```
POST /api/reservations            # Réserver
POST /api/reservations/{id}/confirm
POST /api/reservations/{id}/cancel
```

### Loyers
```
POST /api/leases                  # Créer un bail
GET  /api/leases/landlord         # Mes baux (propriétaire)
GET  /api/landlord/dashboard      # Dashboard KPIs
POST /api/rent-invoices/{id}/pay  # Payer une facture
```

### Paiements (FlexPay)
```
POST /api/payments/initiate       # Initier paiement mobile
POST /api/webhooks/flexpay        # Callback FlexPay
GET  /api/currencies              # Devises disponibles
```

### Messagerie
```
POST /api/conversations           # Nouvelle conversation
POST /api/conversations/{id}/messages  # Envoyer message
GET  /api/notifications           # Mes notifications
```

> Voir Swagger UI pour la liste complète des ~80 endpoints.

---

## Multi-Tenancy

Chaque agence immobilière (Company) a ses données isolées :

- **Hibernate Filter** filtre automatiquement par `company_id`
- **TenantContextFilter** extrait le tenant du JWT token
- **Soft Delete Filter** exclut les entités supprimées (`deleted_at IS NULL`)

---

## Tâches planifiées (Schedulers)

| Scheduler | Fréquence | Description |
|---|---|---|
| `RentInvoiceScheduler` | 1er du mois | Génère les factures pour les baux actifs |
| `RentReminderScheduler` | Quotidien 8h | Envoie les rappels de paiement |
| `OverduePaymentScheduler` | Quotidien 1h | Marque les factures en retard |
| `FlexPaySyncScheduler` | Toutes les 5 min | Vérifie les transactions en attente |
| `CleanupPendingPaymentsScheduler` | Quotidien 2h | Annule les transactions > 24h |

---

## Déploiement sur VPS (Production)

### Prérequis serveur

- Ubuntu 22.04+ ou Debian 12+
- 2 Go RAM minimum (4 Go recommandé)
- Java 21, PostgreSQL 16, Redis 7, Nginx

### 1. Installer les dépendances

```bash
# Java 21
sudo apt update
sudo apt install -y openjdk-21-jdk

# PostgreSQL 16
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
sudo apt update
sudo apt install -y postgresql-16

# Redis
sudo apt install -y redis-server

# Nginx
sudo apt install -y nginx

# Maven (pour build)
sudo apt install -y maven
```

### 2. Configurer PostgreSQL

```bash
sudo -u postgres psql -c "CREATE USER futela WITH PASSWORD 'your_strong_password';"
sudo -u postgres psql -c "CREATE DATABASE futela_spring OWNER futela;"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE futela_spring TO futela;"
```

### 3. Cloner et builder

```bash
cd /opt
sudo git clone https://github.com/futela-co/backend-futela-spring.git futela-api
cd futela-api

# Build le JAR (sans tests pour la prod)
sudo mvn clean package -DskipTests
```

Le JAR est généré dans `target/futela-api-1.0.0-SNAPSHOT.jar`.

### 4. Créer le fichier de configuration production

```bash
sudo nano /opt/futela-api/application-prod.yml
```

```yaml
server:
  port: 8001

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/futela_spring
    username: futela
    password: your_strong_password
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

  data:
    redis:
      host: localhost
      port: 6379

  flyway:
    enabled: true

jwt:
  secret: your-production-jwt-secret-minimum-256-bits-very-long-and-random

cors:
  allowed-origins: https://futela.com,https://admin.futela.com

flexpay:
  base-url: https://backend.flexpay.cd/api/rest/v1
  merchant: your_merchant_id
  api-key: your_api_key
  callback-url: https://api.futela.com/api/webhooks/flexpay

cloudinary:
  cloud-name: your_cloud_name
  api-key: your_api_key
  api-secret: your_api_secret

logging:
  level:
    com.futela.api: INFO
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
```

### 5. Créer le service systemd

```bash
sudo nano /etc/systemd/system/futela-api.service
```

```ini
[Unit]
Description=Futela API - Spring Boot
Documentation=https://github.com/futela-co/backend-futela-spring
After=network.target postgresql.service redis.service

[Service]
Type=simple
User=www-data
Group=www-data
WorkingDirectory=/opt/futela-api

ExecStart=/usr/bin/java \
  -Xms512m -Xmx1024m \
  -XX:+UseZGC \
  -Dspring.profiles.active=prod \
  -Dspring.config.additional-location=file:/opt/futela-api/application-prod.yml \
  -jar target/futela-api-1.0.0-SNAPSHOT.jar

Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=futela-api

# Security hardening
NoNewPrivileges=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=/opt/futela-api/logs

[Install]
WantedBy=multi-user.target
```

```bash
# Permissions
sudo chown -R www-data:www-data /opt/futela-api

# Activer et démarrer
sudo systemctl daemon-reload
sudo systemctl enable futela-api
sudo systemctl start futela-api

# Vérifier
sudo systemctl status futela-api
sudo journalctl -u futela-api -f
```

### 6. Configurer Nginx (reverse proxy + SSL)

```bash
sudo nano /etc/nginx/sites-available/futela-api
```

```nginx
server {
    listen 80;
    server_name api.futela.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.futela.com;

    # SSL (Certbot/Let's Encrypt)
    ssl_certificate /etc/letsencrypt/live/api.futela.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.futela.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Security headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Max upload size (photos)
    client_max_body_size 10M;

    # Proxy to Spring Boot
    location / {
        proxy_pass http://127.0.0.1:8001;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Health check (Spring Actuator)
    location /actuator/health {
        proxy_pass http://127.0.0.1:8001/actuator/health;
        access_log off;
    }

    # Block Swagger in production
    location /swagger-ui {
        deny all;
        return 404;
    }
}
```

```bash
# Activer le site
sudo ln -s /etc/nginx/sites-available/futela-api /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 6b. Alternative : Caddy (reverse proxy + SSL automatique)

Caddy est plus simple que Nginx — SSL automatique sans Certbot.

```bash
# Installer Caddy
sudo apt install -y debian-keyring debian-archive-keyring apt-transport-https curl
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | sudo tee /etc/apt/sources.list.d/caddy-stable.list
sudo apt update
sudo apt install -y caddy
```

Créer le Caddyfile :

```bash
sudo nano /etc/caddy/Caddyfile
```

```caddyfile
api.futela.com {
    # SSL automatique (Let's Encrypt) - rien à configurer !

    # Security headers
    header {
        X-Frame-Options DENY
        X-Content-Type-Options nosniff
        X-XSS-Protection "1; mode=block"
        Strict-Transport-Security "max-age=31536000; includeSubDomains"
        -Server
    }

    # Max upload size (photos)
    request_body {
        max_size 10MB
    }

    # Block Swagger in production
    respond /swagger-ui* 404

    # Proxy to Spring Boot
    reverse_proxy localhost:8001 {
        header_up X-Real-IP {remote_host}
        header_up X-Forwarded-Proto {scheme}

        # Health check
        health_uri /actuator/health
        health_interval 30s
        health_timeout 5s
    }
}
```

```bash
# Démarrer Caddy
sudo systemctl enable caddy
sudo systemctl start caddy

# Vérifier
sudo systemctl status caddy
curl https://api.futela.com/actuator/health
```

> Caddy gère le SSL automatiquement — pas besoin de Certbot ni de renouvellement manuel.

### 7. SSL avec Let's Encrypt (si Nginx)

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d api.futela.com
```

### 8. Appliquer les migrations en production

```bash
cd /opt/futela-api
sudo -u www-data mvn flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/futela_spring \
  -Dflyway.user=futela \
  -Dflyway.password=your_strong_password
```

### 9. Configurer le firewall

```bash
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw deny 8001/tcp   # Bloquer accès direct Spring Boot
sudo ufw deny 5432/tcp   # Bloquer accès direct PostgreSQL
sudo ufw deny 6379/tcp   # Bloquer accès direct Redis
sudo ufw enable
```

### 10. Monitoring

```bash
# Health check
curl https://api.futela.com/actuator/health

# Logs en temps réel
sudo journalctl -u futela-api -f

# Métriques (si Actuator activé)
curl http://localhost:8001/actuator/metrics
```

---

## Mise à jour en production

```bash
cd /opt/futela-api

# Pull les changements
sudo git pull origin main

# Rebuild
sudo mvn clean package -DskipTests

# Appliquer les nouvelles migrations
sudo -u www-data mvn flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/futela_spring \
  -Dflyway.user=futela \
  -Dflyway.password=your_strong_password

# Redémarrer
sudo systemctl restart futela-api

# Vérifier
sudo systemctl status futela-api
curl https://api.futela.com/actuator/health
```

---

## CI/CD (GitHub Actions)

Créer `.github/workflows/deploy.yml` pour automatiser :

```yaml
name: Deploy to VPS

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Build
        run: mvn clean package -DskipTests

      - name: Test
        run: mvn test

      - name: Deploy to VPS
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.VPS_HOST }}
          username: ${{ secrets.VPS_USER }}
          key: ${{ secrets.VPS_SSH_KEY }}
          script: |
            cd /opt/futela-api
            git pull origin main
            mvn clean package -DskipTests
            mvn flyway:migrate \
              -Dflyway.url=jdbc:postgresql://localhost:5432/futela_spring \
              -Dflyway.user=futela \
              -Dflyway.password=${{ secrets.DB_PASSWORD }}
            sudo systemctl restart futela-api
```

---

## Docker (Alternative)

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/futela-api-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-XX:+UseZGC", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
services:
  api:
    build: .
    ports:
      - "8001:8001"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: db
      DB_NAME: futela_spring
      DB_USERNAME: futela
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      REDIS_HOST: redis
    depends_on:
      - db
      - redis

  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: futela_spring
      POSTGRES_USER: futela
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  pgdata:
```

```bash
# Build et lancer
docker compose up -d --build

# Logs
docker compose logs -f api
```

---

## Commandes utiles

```bash
# Compiler
mvn clean compile

# Tester (305 tests)
mvn clean test

# Lancer (dev)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build JAR production
mvn clean package -DskipTests

# Flyway - état des migrations
mvn flyway:info -Dflyway.url=jdbc:postgresql://localhost:5432/futela_spring -Dflyway.user=postgres -Dflyway.password=password

# Flyway - appliquer migrations
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/futela_spring -Dflyway.user=postgres -Dflyway.password=password
```

---

## Structure des migrations Flyway

| Version | Description | Tables |
|---|---|---|
| V001 | Core + Auth | companies, users, device_sessions, refresh_tokens, platform_settings |
| V002 | Address + Category | countries, provinces, cities, towns, districts, addresses, categories + seed RDC |
| V003 | Property (STI) | properties, photos, listings |
| V004 | Reservation + Review | reservations, visits, reviews |
| V005 | Rent | leases, rent_invoices, rent_payments, payment_schedules, rent_reminders |
| V006 | Payment | transactions, currencies, payment_methods + seed devises |
| V007 | Messaging | conversations, conversation_participants, messages, notifications, contacts |
| V008 | Data migration | Migration des données depuis Symfony |
| V009 | Fixtures | Données de test (profil dev) |

---

## Documentation

| Document | Description |
|---|---|
| [MIGRATION-GUIDE.md](docs/MIGRATION-GUIDE.md) | Guide complet Symfony → Spring Boot (réutilisable) |
| [FLYWAY-COMPATIBILITY.md](docs/FLYWAY-COMPATIBILITY.md) | Mapping tables/colonnes Doctrine ↔ Flyway |
| [MIGRATION-SUMMARY.md](docs/MIGRATION-SUMMARY.md) | Résumé de la migration avec métriques |
| [Swagger UI](http://localhost:8001/swagger-ui.html) | Documentation API interactive |

---

## Licence

Propriétaire — Futela Co. Tous droits réservés.
