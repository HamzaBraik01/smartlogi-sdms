# ===============================================
# Dockerfile - SmartLogi SDMS API
# Build simple pour une image allégée
# ===============================================

FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

# Copier les fichiers de dépendances et le code source
COPY pom.xml .
COPY src ./src

# Build de l'application (sans les tests pour accélérer le build)
RUN mvn clean package -DskipTests

# Port exposé
EXPOSE 8080

# Commande de démarrage
CMD ["java", "-jar", "target/smartLogi-v2-0.2.0-SNAPSHOT.war"]
