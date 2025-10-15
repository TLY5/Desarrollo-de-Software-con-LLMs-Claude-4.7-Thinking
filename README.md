# Sistema de Gestión de Biblioteca

## Descripción
Sistema monolítico para la gestión integral de una biblioteca universitaria, desarrollado como parte del curso de ISO2 en la UCLM.

## Características
- Gestión de libros y ejemplares
- Gestión de usuarios
- Sistema de préstamos y devoluciones
- Control de multas por retraso
- Búsqueda avanzada en el catálogo
- Generación de informes

## Requisitos Técnicos
- Java 11 o superior
- Maven 3.6 o superior
- MySQL 8.0 o superior

## Instalación

### 1. Configurar la base de datos
```sql
CREATE DATABASE biblioteca;
CREATE USER 'biblioteca_user'@'localhost' IDENTIFIED BY 'biblioteca_password';
GRANT ALL PRIVILEGES ON biblioteca.* TO 'biblioteca_user'@'localhost';