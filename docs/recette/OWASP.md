# AquaSensus — checklist OWASP (ISS-061)

Revue du 2026-08-26. Cible : déploiement ONG/mairie, pas d'opérateur SMS réel, pas de KYC lourd.

| Contrôle | Mesure dans le code | Preuve |
| --- | --- | --- |
| A01 contrôle d'accès | RBAC Spring, périmètre comité, `/internal/**` rôle INTERNE | `SecuriteRbacEtEntetesTest`, `AuthEtPerimetreTest` |
| A02 secrets | Variables d'environnement, `.env` gitignoré, garde profil `prod` | `.env.example`, `GardeSecretsProduction` |
| A03 injections | Requêtes paramétrées JDBC/JPA, Flyway SQL versionné | `TableauBordService`, migrations |
| A04 conception | JWT stateless, CSRF non applicable (pas de cookie de session) | `ConfigurationSecurite` |
| A05 mauvaise config | CORS listée, actuator limité, traces prod WARN | `application-prod.yml` |
| A06 composants | Spring Boot 3.4, Java 21, images Docker épinglées | `pom.xml`, `compose.yml` |
| A07 auth | Verrouillage 5 échecs, message générique, BCrypt 12 | `AuthEtPerimetreTest` |
| A08 intégrité | Idempotence `X-Client-Request-Id` | signalements |
| A09 journalisation | Corrélation, pas de mot de passe dans les logs | pattern `%X{correlationId}` |
| A10 SSRF | Service data n'est pas routé par Nginx | `infra/nginx` |
| Fichiers | Pièces bornées dès L2 ; pas d'upload libre v1 carte/KPI | conception |
| RGPD-like | Téléphone haché + suffixe, pas de volume, seed fictif | RG-10, ISS-065 |
| En-têtes | `X-Content-Type-Options`, `X-Frame-Options`, Referrer-Policy, HSTS | test health |

**Hors périmètre volontaire (bible) :** accord opérateur, capteurs IoT, KYC bancaire.
