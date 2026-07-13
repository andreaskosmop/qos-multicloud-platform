# ── Στάδιο 1: Build ──────────────────────────────────────────────
# Multi-stage build (§2.3.2): το JDK/Maven χρειάζονται μόνο για compilation,
# όχι στην τελική εικόνα — μειώνει σημαντικά το μέγεθός της.
# Χρησιμοποιείται επίσημο image με ενσωματωμένο Maven (πιο αξιόπιστο από mvnw).
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Terraform CLI ενσωματωμένο στο image (όχι volume mapping — §5.1.2 σημείωση)
RUN apk add --no-cache curl unzip \
    && curl -fsSL https://releases.hashicorp.com/terraform/1.7.0/terraform_1.7.0_linux_amd64.zip -o terraform.zip \
    && unzip terraform.zip -d /usr/local/bin \
    && rm terraform.zip

# Αντιγραφή pom.xml πρώτα (layer caching — §2.3.2: layered builds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Αντιγραφή πηγαίου κώδικα και build
COPY src src
RUN mvn clean package -DskipTests -B

# ── Στάδιο 2: Runtime ────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Terraform CLI χρειάζεται και στο runtime image (εκτελείται από TerraformService)
RUN apk add --no-cache curl unzip \
    && curl -fsSL https://releases.hashicorp.com/terraform/1.7.0/terraform_1.7.0_linux_amd64.zip -o terraform.zip \
    && unzip terraform.zip -d /usr/local/bin \
    && rm terraform.zip \
    && apk del curl unzip

# Δημιουργία μη-root χρήστη για ασφάλεια
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

# Φάκελος terraform workspaces (mount point — §5.1.2)
VOLUME /var/cloudplatform/workspaces

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
