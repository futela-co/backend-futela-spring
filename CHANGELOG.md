# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- [Me] Add dashboard, profile, and booking endpoints with ~15 new /api/me/* routes ([7964d73](https://github.com/futela-co/backend-futela-spring/commit/7964d73))
- [Landlord] Add tenants listing and yearly income endpoints ([af3a4b3](https://github.com/futela-co/backend-futela-spring/commit/af3a4b3))
- [Review] Add update, owner response, and moderation endpoints ([8d223f4](https://github.com/futela-co/backend-futela-spring/commit/8d223f4))
- [Admin] Add admin dashboard, global transactions list, category delete, and property soft-delete/restore ([23fa494](https://github.com/futela-co/backend-futela-spring/commit/23fa494))

### Fixed
- [Config] Add case-insensitive enum deserialization for frontend compatibility ([192c231](https://github.com/futela-co/backend-futela-spring/commit/192c231))
- [Security] Replace @RequestParam user IDs with SecurityService across all controllers ([4bd46df](https://github.com/futela-co/backend-futela-spring/commit/4bd46df))

### Changed
- [Entity] Migrate JSONB fields from Map to JsonNode for safe deserialization ([0542949](https://github.com/futela-co/backend-futela-spring/commit/0542949))
- [Persistence] Update repositories, adapters and mappers for new endpoints and JsonNode support ([9003dcd](https://github.com/futela-co/backend-futela-spring/commit/9003dcd))
