-- Esquema de base de datos para el Sistema de Gestión de Bibliotecas
-- Siguiendo el patrón una clase-una tabla

-- Creación de la base de datos
CREATE DATABASE IF NOT EXISTS biblioteca CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE biblioteca;

-- Tabla de categorías
CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT
) ENGINE=InnoDB;

-- Tabla de libros
CREATE TABLE IF NOT EXISTS libros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    editorial VARCHAR(100),
    anio_publicacion INT,
    isbn VARCHAR(20) UNIQUE,
    categoria_id BIGINT,
    descripcion TEXT,
    fecha_alta DATE NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Tabla de ejemplares
CREATE TABLE IF NOT EXISTS ejemplares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libro_id BIGINT NOT NULL,
    codigo_barras VARCHAR(50) NOT NULL UNIQUE,
    estado VARCHAR(20) NOT NULL,
    ubicacion VARCHAR(100),
    fecha_adquisicion DATE NOT NULL,
    FOREIGN KEY (libro_id) REFERENCES libros(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni_nif VARCHAR(10) NOT NULL UNIQUE,
    direccion VARCHAR(255),
    telefono VARCHAR(15),
    email VARCHAR(100),
    tipo_usuario VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_alta DATE NOT NULL
) ENGINE=InnoDB;

-- Tabla de préstamos
CREATE TABLE IF NOT EXISTS prestamos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    fecha_devolucion_prevista DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabla de detalle de préstamo
CREATE TABLE IF NOT EXISTS detalle_prestamos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id BIGINT NOT NULL,
    ejemplar_id BIGINT NOT NULL,
    fecha_devolucion_real DATE,
    estado VARCHAR(20) NOT NULL,
    FOREIGN KEY (prestamo_id) REFERENCES prestamos(id) ON DELETE CASCADE,
    FOREIGN KEY (ejemplar_id) REFERENCES ejemplares(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Tabla de multas
CREATE TABLE IF NOT EXISTS multas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    prestamo_id BIGINT,
    importe DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (prestamo_id) REFERENCES prestamos(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Índices para mejorar el rendimiento
CREATE INDEX idx_libros_titulo ON libros (titulo);
CREATE INDEX idx_libros_autor ON libros (autor);
CREATE INDEX idx_usuarios_nombre_apellidos ON usuarios (nombre, apellidos);
CREATE INDEX idx_prestamos_usuario ON prestamos (usuario_id);
CREATE INDEX idx_prestamos_estado ON prestamos (estado);
CREATE INDEX idx_ejemplares_estado ON ejemplares (estado);
CREATE INDEX idx_multas_usuario ON multas (usuario_id);