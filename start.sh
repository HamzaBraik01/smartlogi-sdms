#!/bin/bash

# Couleurs
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "===================================="
echo "SmartLogi SDMS - Démarrage"
echo "===================================="
echo ""

# Vérifier si PostgreSQL est accessible
echo -e "${BLUE}[INFO]${NC} Vérification de PostgreSQL..."
if command -v psql &> /dev/null; then
    echo -e "${GREEN}[OK]${NC} PostgreSQL est accessible"
else
    echo -e "${YELLOW}[AVERTISSEMENT]${NC} PostgreSQL n'est pas dans le PATH"
fi

echo ""
echo -e "${BLUE}[INFO]${NC} Nettoyage et compilation du projet..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo ""
    echo -e "${RED}[ERREUR]${NC} La compilation a échoué"
    exit 1
fi

echo ""
echo "===================================="
echo -e "${GREEN}Compilation réussie !${NC}"
echo "===================================="
echo ""
echo "Démarrage de l'application..."
echo ""
echo "Les URLs Swagger seront disponibles à :"
echo "  - http://localhost:8080/v3/api-docs"
echo "  - http://localhost:8080/swagger-ui/index.html"
echo ""

./mvnw spring-boot:run

