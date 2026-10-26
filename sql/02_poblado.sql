-- Poblado inicial 

USE rrhh_iudigital;

INSERT INTO tipo_identificacion (nombre) VALUES
 ('Cédula de ciudadanía'), ('Cédula de extranjería'), ('Pasaporte'),
 ('Tarjeta de identidad'), ('Registro civil'), ('NIT');

INSERT INTO estado_civil (nombre) VALUES
 ('Soltero(a)'), ('Casado(a)'), ('Unión libre'), ('Separado(a)'), ('Divorciado(a)'), ('Viudo(a)');

INSERT INTO sexo (nombre) VALUES ('Masculino'), ('Femenino'), ('Otro');

INSERT INTO rol_familiar (nombre) VALUES
 ('Cónyuge'), ('Compañero(a) permanente'), ('Hijo(a)'), ('Padre'), ('Madre'), ('Hermano(a)'), ('Otro');

INSERT INTO nivel_estudio (nombre) VALUES
 ('Bachiller'), ('Técnico'), ('Tecnólogo'), ('Profesional'),
 ('Especialización'), ('Maestría'), ('Doctorado');

INSERT INTO universidad (nombre) VALUES
 ('Institución Universitaria Digital de Antioquia'),
 ('Universidad de Antioquia'),
 ('Universidad Nacional de Colombia'),
 ('Universidad EAFIT'),
 ('Universidad Pontificia Bolivariana'),
 ('Politécnico Colombiano Jaime Isaza Cadavid'),
 ('Institución Universitaria Pascual Bravo'),
 ('Universidad de los Andes');

INSERT INTO funcionario
 (tipo_identificacion_id, numero_identificacion, nombres, apellidos,
  estado_civil_id, sexo_id, direccion, telefono, fecha_nacimiento)
VALUES (1, '1037123456', 'Carlos Andrés', 'Ramírez Gómez', 2, 1,
        'Calle 50 # 45-10, Medellín', '3001234567', '1988-05-14');

INSERT INTO miembro_familiar
 (funcionario_id, tipo_identificacion_id, numero_identificacion, nombres, apellidos,
  fecha_nacimiento, sexo_id, rol_familiar_id)
VALUES
 (1, 1, '43765432',   'Luisa María', 'Torres Ruiz',    '1990-09-02', 2, 1),
 (1, 4, '1100987654', 'Samuel',      'Ramírez Torres', '2015-03-20', 1, 3);

INSERT INTO informacion_academica (funcionario_id, universidad_id, nivel_estudio_id, titulo)
VALUES
 (1, 2, 4, 'Ingeniero de Sistemas'),
 (1, 1, 5, 'Especialista en Desarrollo de Software Seguro');
