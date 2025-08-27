Ecommerce API - Backend
RESTful API para ecommerce desarrollada con Spring Boot
🚀 Features
* ✅ Autenticación JWT
* ✅ CRUD completo (Products, Orders, Users, Reviews)
* ✅ Paginación optimizada
* ✅ Sistema de favoritos
* ✅ Manejo profesional de órdenes
🛠️ Tech Stack
* Java 17
* Spring Boot 3.x
* Spring Security (JWT)
* Spring Data JPA
* MySQL
* Maven
📋 Setup
Prerequisites
* Java 17+
* Maven 3.6+
* MySQL 8.0+
Installation
1. **Clone el repositorio**

git clone https://github.com/lucasbonggio/ecommerce-api.git
cd ecommerce-api

2. **Crear base de datos MySQL**
```branch
CREATE DATABASE ecommerce_db;
```
3. **Configurar application.properties**
```branch
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=tu_password
```
4. **Ejecutar aplicación**
```branch
./mvnw spring-boot:run
```
5. **API disponible en:** `http://localhost:8080`
