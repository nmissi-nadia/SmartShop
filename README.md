# 🛒 SmartShop – API REST de Gestion Commerciale

### *Backend professionnel pour MicroTech Maroc*
<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Status-Production%20Ready-blue?style=flat" />
  <img src="https://img.shields.io/badge/License-MIT-purple" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/Database-MySQL-4479A1?logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Docs-Swagger-85EA2D?logo=swagger&logoColor=black" />
</p>

---

## 🌟 Présentation

SmartShop est une API REST complète pensée pour moderniser la gestion commerciale d’un distributeur informatique.
Elle gère les **clients**, **produits**, **commandes**, **paiements**, **statistiques**, **fidélité**, et propose une architecture **propre, scalable et documentée**.

L’objectif : offrir une solution fiable, sécurisée et industrialisable pour une entreprise B2B.

---

## 🚀 Fonctionnalités principales

### 🔐 Authentification (HTTP Session)

* Sans JWT, légère et efficace
* Login / logout
* Session persistante serveur

### 👥 Gestion des clients

* CRUD complet
* Statistiques automatiques
* Fidélité dynamique (BASIC → PLATINUM)
* Historique complet des commandes

### 🛍️ Gestion des produits

* CRUD + soft-delete intelligent
* Gestion de stock
* Prix HT, remises, TVA

### 📦 Commandes

* Gestion multi-produits
* Calcul : sous-total, remise, TVA, total TTC
* Vérification stock
* Statuts : PENDING, CONFIRMED, CANCELED, REJECTED

### 💰 Paiements multi-moyens

* Espèces
* Chèque
* Virement
* Paiements fractionnés
* Historique immuable (sécurité comptable)
* Statut : EN ATTENTE → ENCAISSÉ → REJETÉ
* Déduction automatique du reste à payer

---

## 🧱 Architecture

```
Controller → Service → Repository → Entity → DTO → Mapper
```

Conception orientée **SOLID**, testée et simple à maintenir.

---

## 🎨 Stack Technique

<p align="left">
  <img src="https://img.shields.io/badge/Java-17-orange?logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Hibernate-JPA-59666C?logo=hibernate&logoColor=white" />
  <img src="https://img.shields.io/badge/MapStruct-Mapper-blue?logo=mapstruct" />
  <img src="https://img.shields.io/badge/Swagger-API_Docs-green?logo=swagger" />
  <img src="https://img.shields.io/badge/MySQL-Database-4479A1?logo=mysql&logoColor=white" />
</p>

---

## 🧪 Tests & Qualité

* Tests unitaires (JUnit, Mockito)
* Tests d’intégration (JPA)
* Gestion centralisée des erreurs
* Validation avancée (Jakarta Validation)

Code conçu pour être **prédictible, testé et robuste**.

---

## 📚 Endpoints & Documentation

Swagger :
👉 `http://localhost:8081/swagger-ui/index.html`

Collection Postman fournie dans `/docs/postman`.

---

## 🗄️ Installation

### 1. Cloner

```bash
git clone https://github.com/nmissi-nadia/smartshop.git
cd smartshop
```

### 2. Configurer la base

Créer une base MySQL :

```sql
CREATE DATABASE smartshop CHARACTER SET utf8mb4;
```

Configurer `application.properties`.

### 3. Lancer

```bash
mvn spring-boot:run
```

API disponible sur
👉 `http://localhost:8081/swagger-ui.html`

---

## 📊 Diagramme UML

Disponible dans `Documentation\Conception`.

---

## 🧑‍💻 Auteur




<div align="center">
  <p>Développé avec ❤️ par Votre Nmissi Nadia - 2025</p>
  <p>
    <a href="https://github.com/nmissi-nadia"><img src="https://img.shields.io/badge/GitHub-Profil-black?logo=github" /></a> •
    <a href="https://www.linkedin.com/in/nadia-nmissi/">LinkedIn</a> •
    <a href="mailto:nmissinadia@gmail.com"><img src="https://img.shields.io/badge/Contact-Mail-blue?logo=gmail" /></a>
  </p>
</div>
```
