DROP DATABASE IF EXISTS rrhh_iudigital;
CREATE DATABASE rrhh_iudigital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE rrhh_iudigital;

--  CATÁLOGOS --
CREATE TABLE tipo_identificacion (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE estado_civil (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(40) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE sexo (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE rol_familiar (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(40) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE nivel_estudio (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE universidad (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE
) ENGINE=InnoDB;


CREATE TABLE funcionario (
    id                     INT AUTO_INCREMENT PRIMARY KEY,
    tipo_identificacion_id INT          NOT NULL,
    numero_identificacion  VARCHAR(20)  NOT NULL,
    nombres                VARCHAR(80)  NOT NULL,
    apellidos              VARCHAR(80)  NOT NULL,
    estado_civil_id        INT          NOT NULL,
    sexo_id                INT          NOT NULL,
    direccion              VARCHAR(150) NOT NULL,
    telefono               VARCHAR(20)  NOT NULL,
    fecha_nacimiento       DATE         NOT NULL,
    CONSTRAINT uq_funcionario_identificacion
        UNIQUE (tipo_identificacion_id, numero_identificacion),
    CONSTRAINT fk_funcionario_tipo_id  FOREIGN KEY (tipo_identificacion_id) REFERENCES tipo_identificacion(id),
    CONSTRAINT fk_funcionario_estado   FOREIGN KEY (estado_civil_id)        REFERENCES estado_civil(id),
    CONSTRAINT fk_funcionario_sexo     FOREIGN KEY (sexo_id)                REFERENCES sexo(id)
) ENGINE=InnoDB;

-- GRUPO FAMILIAR (1:N) --
CREATE TABLE miembro_familiar (
    id                     INT AUTO_INCREMENT PRIMARY KEY,
    funcionario_id         INT         NOT NULL,
    tipo_identificacion_id INT         NOT NULL,
    numero_identificacion  VARCHAR(20) NOT NULL,
    nombres                VARCHAR(80) NOT NULL,
    apellidos              VARCHAR(80) NOT NULL,
    fecha_nacimiento       DATE        NOT NULL,
    sexo_id                INT         NOT NULL,
    rol_familiar_id        INT         NOT NULL,
    CONSTRAINT uq_miembro UNIQUE (funcionario_id, tipo_identificacion_id, numero_identificacion),
    CONSTRAINT fk_miembro_funcionario FOREIGN KEY (funcionario_id)         REFERENCES funcionario(id) ON DELETE CASCADE,
    CONSTRAINT fk_miembro_tipo_id     FOREIGN KEY (tipo_identificacion_id) REFERENCES tipo_identificacion(id),
    CONSTRAINT fk_miembro_sexo        FOREIGN KEY (sexo_id)                REFERENCES sexo(id),
    CONSTRAINT fk_miembro_rol         FOREIGN KEY (rol_familiar_id)        REFERENCES rol_familiar(id)
) ENGINE=InnoDB;

--  INFORMACIÓN ACADÉMICA (1:N) --
CREATE TABLE informacion_academica (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    funcionario_id   INT          NOT NULL,
    universidad_id   INT          NOT NULL,
    nivel_estudio_id INT          NOT NULL,
    titulo           VARCHAR(150) NOT NULL,
    CONSTRAINT fk_academica_funcionario FOREIGN KEY (funcionario_id)   REFERENCES funcionario(id) ON DELETE CASCADE,
    CONSTRAINT fk_academica_universidad FOREIGN KEY (universidad_id)   REFERENCES universidad(id),
    CONSTRAINT fk_academica_nivel       FOREIGN KEY (nivel_estudio_id) REFERENCES nivel_estudio(id)
) ENGINE=InnoDB;
