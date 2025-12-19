# ===============================================
# Dockerfile - SmartLogi SDMS API
# Build multi-stage pour une image optimisée
# ===============================================

# -------- Stage 1: Build --------
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copier les fichiers de dépendances pour le cache Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build de l'application (sans les tests pour accélérer le build)
RUN mvn clean package -DskipTests -B

# -------- Stage 2: Runtime --------
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="SmartLogi <dev@smartlogi.com>"
LABEL version="1.0.0"
LABEL description="SmartLogi Delivery Management System API"

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=builder /app/target/*.jar app.jar

# Créer le répertoire de logs
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

# Passer à l'utilisateur non-root
USER appuser

# Port exposé
EXPOSE 8080

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=100" \
    SPRING_PROFILES_ACTIVE="prod" \
    TZ="Europe/Paris"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Commande de démarrage
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

