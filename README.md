# SmartShop
SmartShop est une application web de gestion commerciale destinée à MicroTech Maroc

# 🛍️ SmartShop - Plateforme E-commerce Complète

## 📋 Table des matières
- [Présentation](#-présentation)
- [Fonctionnalités](#-fonctionnalités)
- [Technologies](#-technologies)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Démarrage](#-démarrage)
- [API Documentation](#-api-documentation)
- [Tests](#-tests)
- [Déploiement](#-déploiement)
- [Structure du Projet](#-structure-du-projet)
- [Contribution](#-contribution)


## 🌟 Présentation
SmartShop est une solution complète de gestion de boutique en ligne offrant une expérience d'achat fluide et sécurisée. La plateforme intègre des fonctionnalités avancées de gestion des produits, des commandes, des paiements et des clients.

## ✨ Fonctionnalités

### 👥 Gestion des Utilisateurs
- Inscription et authentification sécurisée
- Profils utilisateurs personnalisables
- Système de rôles (ADMIN, CLIENT)
- Gestion des sessions

### 🏪 Gestion du Catalogue
- CRUD des produits
- Catégorisation des articles
- Gestion des stocks en temps réel
- Système d'évaluation et d'avis

### 🛒 Gestion des Commandes
- Panier d'achat
- Suivi des commandes en temps réel
- Historique des achats
- Facturation électronique

### 💳 Paiement
- Intégration avec plusieurs passerelles de paiement
- Gestion des remboursements
- Suivi des transactions

## 🛠 Technologies

### Backend
- **Java 17**
- **Spring Boot 3.1.0**
- Spring Data JPA
- Hibernate
- Springdoc OpenAPI 3.0 (Documentation API)
- Liquibase (Gestion des migrations)
- JUnit 5 & Mockito (Tests)

### Base de Données
- PostgreSQL 13+
- Configuration H2 pour les tests

### Outils
- Maven
- Git
- IntelliJ IDEA (Recommandé)

## 📋 Prérequis

### Développement
- JDK 17+
- Maven 3.6+
- PostgreSQL 13+
- Git

### Production
- Serveur d'application (Tomcat 10+)
- Base de données PostgreSQL
- Au moins 2GB de RAM

## 🚀 Installation

1. **Cloner le dépôt**
   ```bash
   git clone https://github.com/nmissi-nadia/smartshop.git
   cd smartshop/shop
   ```

2. **Configurer la base de données**
   ```sql
   CREATE DATABASE smartshop;
   CREATE USER smartshop_user WITH ENCRYPTED PASSWORD 'votre_mot_de_passe';
   GRANT ALL PRIVILEGES ON DATABASE smartshop TO smartshop_user;
   ```

3. **Configurer l'application**
   Copier le fichier de configuration :
   ```bash
   cp src/main/resources/application-example.properties src/main/resources/application.properties
   ```
   Puis modifier les paramètres selon votre environnement.

4. **Construire le projet**
   ```bash
   mvn clean install
   ```

## ⚙️ Configuration

### Fichier [application.properties](cci:7://file:///c:/Users/youco/IdeaProjects/SmartShop/shop/src/main/resources/application.properties:0:0-0:0)
```properties
# Serveur
server.port=8080
server.servlet.session.timeout=30m

# Base de données
spring.datasource.url=jdbc:postgresql://localhost:5432/smartshop
spring.datasource.username=smartshop_user
spring.datasource.password=votre_mot_de_passe
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Liquibase
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
```

### Variables d'environnement
| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `DB_URL` | URL de la base de données | `jdbc:postgresql://localhost:5432/smartshop` |
| `DB_USERNAME` | Utilisateur de la base de données | `smartshop_user` |
| `DB_PASSWORD` | Mot de passe de la base de données | - |
| `JWT_SECRET` | Clé secrète pour JWT | `votre_cle_secrete_tres_longue_et_securisee` |

## 🚀 Démarrage

### Mode développement
```bash
mvn spring-boot:run
```

### Production
```bash
mvn clean package -DskipTests
java -jar target/shop-0.0.1-SNAPSHOT.jar
```

### Avec Docker
```bash
docker-compose up --build
```

## 📚 API Documentation

### Accès à la documentation
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8081/v3/api-docs`

### Exemple de requête
```bash
# Authentification
curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@example.com","password":"password123"}'
```

## 🧪 Tests

### Exécuter tous les tests
```bash
mvn test
```

### Exécuter une classe de test spécifique
```bash
mvn test -Dtest=ClientServiceTest
```

### Couverture de code
```bash
mvn jacoco:report
```
Le rapport sera disponible dans `target/site/jacoco/index.html`

## 🏗 Structure du Projet

```
smartshop/
├── shop/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smart/shop/
│   │   │   │   ├── config/           # Configurations Spring
│   │   │   │   ├── controller/       # Contrôleurs REST
│   │   │   │   ├── dto/              # Objets de transfert de données
│   │   │   │   ├── entity/           # Entités JPA
│   │   │   │   ├── exception/        # Gestion des exceptions
│   │   │   │   ├── repository/       # Repositories Spring Data JPA
│   │   │   │   ├── security/         # Configuration de sécurité
│   │   │   │   ├── service/          # Couche métier
│   │   │   │   └── ShopApplication.java
│   │   │   └── resources/
│   │   │       ├── db/               # Scripts de migration
│   │   │       ├── static/           # Fichiers statiques
│   │   │       └── application.properties
│   │   └── test/                     # Tests unitaires et d'intégration
│   └── pom.xml
└── README.md
```

## 🤝 Contribution

1. Forkez le projet
2. Créez votre branche (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Poussez vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

### Standards de code
- Suivez les conventions de nommage Java
- Documentez les méthodes complexes
- Écrivez des tests unitaires pour les nouvelles fonctionnalités
- Vérifiez le code avec Checkstyle


---

<div align="center">
  <p>Développé avec ❤️ par Votre Nmissi Nadia - 2025</p>
  <p>
    <a href="https://github.com/nmissi-nadia">GitHub</a> •
    <a href="https://linkedin.com/in/nmissi-nadia">LinkedIn</a> •
    <a href="mailto:nmissinadia@gmail.com">Contact</a>
  </p>
</div>
```
