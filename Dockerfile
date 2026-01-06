# ===============================================
# Dockerfile - SmartLogi SDMS API
# Multi-stage build pour une image allégée
# ===============================================

# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copier les fichiers de dépendances et le code source
COPY pom.xml .
COPY src ./src

# Build de l'application (sans les tests pour accélérer le build)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=builder /app/target/sdms-0.0.1-SNAPSHOT.jar app.jar

# Port exposé
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
