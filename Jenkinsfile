pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    environment {
        // Configuration de l'application
        APP_NAME = 'smartlogi-sdms'
        APP_VERSION = '0.0.1-SNAPSHOT'

        // Configuration Docker
        DOCKER_IMAGE = "smartlogi/${APP_NAME}"
        DOCKER_TAG = "${BUILD_NUMBER}"

        // Configuration SonarQube
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_PROJECT_KEY = 'smartlogi-sdms'

        // Configuration de la base de données pour les tests
        POSTGRES_DB = 'smartlogi_management_test'
        POSTGRES_USER = 'postgres'
        POSTGRES_PASSWORD = credentials('postgres-password')

        // Token SonarQube
        SONAR_TOKEN = credentials('sonar-token')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('🔍 Checkout') {
            steps {
                echo '📥 Récupération du code source...'
                checkout scm
            }
        }

        stage('📋 Validation') {
            steps {
                echo '✅ Validation du projet Maven...'
                bat 'mvn validate'
            }
        }

        stage('🔨 Compilation') {
            steps {
                echo '🔨 Compilation du projet...'
                bat 'mvn clean compile -DskipTests'
            }
        }

        stage('🧪 Tests Unitaires') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                bat 'mvn test'
            }
            post {
                always {
                    // Publication des rapports de tests
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('📊 Analyse de Qualité') {
            parallel {
                stage('JaCoCo Coverage') {
                    steps {
                        echo '📊 Génération du rapport de couverture JaCoCo...'
                        bat 'mvn jacoco:report'
                    }
                    post {
                        always {
                            // Publication du rapport JaCoCo
                            jacoco(
                                execPattern: '**/target/jacoco.exec',
                                classPattern: '**/target/classes',
                                sourcePattern: '**/src/main/java',
                                exclusionPattern: '**/test/**'
                            )
                        }
                    }
                }

                stage('SonarQube Analysis') {
                    steps {
                        echo '🔍 Analyse SonarQube...'
                        withSonarQubeEnv('SonarQube') {
                            bat """
                                mvn sonar:sonar ^
                                    -Dsonar.projectKey=${SONAR_PROJECT_KEY} ^
                                    -Dsonar.host.url=${SONAR_HOST_URL} ^
                                    -Dsonar.token=${SONAR_TOKEN}
                            """
                        }
                    }
                }
            }
        }

        stage('⏳ Quality Gate') {
            steps {
                echo '⏳ Attente du résultat Quality Gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('📦 Package') {
            steps {
                echo '📦 Création du package...'
                bat 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('🐳 Build Docker Image') {
            steps {
                echo '🐳 Construction de l\'image Docker...'
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }

        stage('🚀 Push Docker Image') {
            when {
                branch 'main'
            }
            steps {
                echo '🚀 Push de l\'image vers le registry...'
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }

        stage('🚢 Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                echo '🚢 Déploiement sur l\'environnement de staging...'
                script {
                    bat '''
                        docker-compose -f docker-compose.yml down || true
                        docker-compose -f docker-compose.yml up -d
                    '''
                }
            }
        }

        stage('🏭 Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                echo '🏭 Déploiement sur l\'environnement de production...'
                input message: 'Déployer en production ?', ok: 'Déployer'
                script {
                    bat '''
                        docker-compose -f docker-compose.yml down || true
                        docker-compose -f docker-compose.yml up -d
                    '''
                }
            }
        }
    }

    post {
        always {
            echo '🧹 Nettoyage du workspace...'
            cleanWs()
        }
        success {
            echo '✅ Pipeline exécuté avec succès!'

        }
        failure {
            echo '❌ Le pipeline a échoué!'

        }
        unstable {
            echo '⚠️ Le pipeline est instable!'
        }
    }
}

