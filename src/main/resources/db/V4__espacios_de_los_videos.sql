-- =====================================================================
-- V4 · Espacios registrados en los videos del recorrido por los bloques A y B
--       y ubicación de cada programa (su facultad dentro del campus).
-- Idempotente: se puede ejecutar en cada despliegue (V2 recrea lugares y programas).
-- =====================================================================

-- Cada programa apunta al lugar donde queda su facultad
ALTER TABLE programa ADD COLUMN IF NOT EXISTS lugar_id INTEGER REFERENCES lugar(id) ON DELETE SET NULL;

DELETE FROM lugar WHERE nombre IN (
  'Bienestar Universitario', 'Gimnasio', 'Zona de descanso con puffs', 'Apoyo Psicosocial',
  'Coordinación Clínica', 'Centro de Administración Documental',
  'Salones 301 a 305', 'Facultad de Medicina', 'Coordinación del programa de Medicina',
  'Facultad de Ingeniería', 'Sala de profesores de Ingeniería', 'Laboratorios de Ingeniería Industrial', 'Salas de audiencia de Derecho',
  'Facultad de Enfermería', 'Facultad de Derecho',
  'Laboratorio de Innovación, Prototipado y Emprendimiento', 'Laboratorio de Operaciones', 'Auditorio Urcunina',
  'Laboratorio de Simulación Clínica', 'Papelería', 'Cajero BBVA', 'Facultad de Odontología', 'Laboratorio de Preclínica',
  'Coordinación Clínica de Odontología', 'Sala de profesores de Medicina', 'Centro de Investigación',
  'Laboratorio de Biología Molecular', 'Laboratorio de Realidad Mixta', 'Salas de Informática',
  'Programa de Derecho', 'Centro de Investigaciones Sociojurídicas', 'Open Language Center', 'Salón de Arte, Música y Cultura'
);

INSERT INTO lugar (nombre, tipo, edificio_id, piso, descripcion, horario, telefono, correo) VALUES
-- Bloque B · Piso 1 (entrada)
('Bienestar Universitario', 'OFICINA', (SELECT id FROM edificio WHERE codigo='B'), 1, 'Oficina de Bienestar Universitario, a mano derecha por el pasillo principal del Piso 1 del Bloque B.', NULL, NULL, NULL),
('Gimnasio', 'DEPORTIVO', (SELECT id FROM edificio WHERE codigo='B'), 1, 'Gimnasio de la universidad junto a Bienestar Universitario: caminadoras, elípticas y bicicletas estáticas.', NULL, NULL, NULL),
('Zona de descanso con puffs', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='B'), 1, 'Zona con puffs azules para descansar, en el pasillo de Bienestar del Piso 1 del Bloque B; cerca están las mesas de ping-pong.', NULL, NULL, NULL),
('Apoyo Psicosocial', 'OFICINA', (SELECT id FROM edificio WHERE codigo='B'), 1, 'Oficina de Apoyo Psicosocial y Desarrollo Social (incluye atención en violencias basadas en género), Piso 1 del Bloque B.', NULL, NULL, NULL),
-- Bloque B · Piso 0 (sótano 1)
('Coordinación Clínica', 'OFICINA', (SELECT id FROM edificio WHERE codigo='B'), -1, 'Coordinación Clínica de Odontología, en el Piso 0 del Bloque B junto al Laboratorio de Morfología.', NULL, NULL, NULL),
('Centro de Administración Documental', 'OFICINA', (SELECT id FROM edificio WHERE codigo='B'), -1, 'Centro de Administración Documental, en el Piso 0 del Bloque B.', NULL, NULL, NULL),
-- Bloque B · pisos 2 a 7
('Salones 301 a 305', 'SALON', (SELECT id FROM edificio WHERE codigo='B'), 2, 'Salones de clase con sillas universitarias, tablero y proyector, en el piso de encima de la entrada del Bloque B.', NULL, NULL, NULL),
('Facultad de Medicina', 'FACULTAD', (SELECT id FROM edificio WHERE codigo='B'), 3, 'Oficinas de la Facultad de Medicina (decanatura y atención), Piso 3 del Bloque B.', NULL, NULL, NULL),
('Coordinación del programa de Medicina', 'OFICINA', (SELECT id FROM edificio WHERE codigo='B'), 3, 'Coordinación del programa de Medicina, junto a la Facultad de Medicina en el Piso 3 del Bloque B.', NULL, NULL, NULL),
('Facultad de Ingeniería', 'FACULTAD', (SELECT id FROM edificio WHERE codigo='B'), 4, 'Facultad de Ingeniería (Ingeniería de Software e Ingeniería Industrial): decanatura y atención, Piso 4 del Bloque B. Sala de profesores en el 409.', NULL, NULL, NULL),
('Sala de profesores de Ingeniería', 'OFICINA', (SELECT id FROM edificio WHERE codigo='B'), 4, 'Sala de profesores de la Facultad de Ingeniería, salón 409 del Bloque B.', NULL, NULL, NULL),
('Laboratorios de Ingeniería Industrial', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='B'), 4, 'Laboratorios de Aplicaciones de la Física, de Aplicaciones de la Química y de Bioprocesos y Biotecnología, Piso 4 del Bloque B.', NULL, NULL, NULL),
('Salas de audiencia de Derecho', 'SALON', (SELECT id FROM edificio WHERE codigo='B'), 4, 'Salas de audiencia donde los estudiantes de Derecho practican juicios, Piso 4 del Bloque B.', NULL, NULL, NULL),
('Facultad de Enfermería', 'FACULTAD', (SELECT id FROM edificio WHERE codigo='B'), 5, 'Facultad y programa de Enfermería (501) con la sala de profesores de Enfermería, Piso 5 del Bloque B.', NULL, NULL, NULL),
('Facultad de Derecho', 'FACULTAD', (SELECT id FROM edificio WHERE codigo='B'), 5, 'Oficinas de la Facultad de Derecho, Piso 5 del Bloque B (desde este piso también se pasa al Bloque A).', NULL, NULL, NULL),
('Laboratorio de Innovación, Prototipado y Emprendimiento', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='B'), 6, 'Laboratorio de innovación con impresoras 3D y mesas de trabajo, salón 605 del Bloque B.', NULL, NULL, NULL),
('Laboratorio de Operaciones', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='B'), 6, 'Laboratorio de Operaciones con mesones de acero y lavaplatos, Piso 6 del Bloque B.', NULL, NULL, NULL),
('Auditorio Urcunina', 'SALON', (SELECT id FROM edificio WHERE codigo='B'), 7, 'Auditorio Urcunina (aula 611), muy grande y con dos puertas; en el mismo piso están los salones 610 y 608. Último piso del Bloque B.', NULL, NULL, NULL),
-- Bloque A
('Laboratorio de Simulación Clínica', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='A'), -1, 'Laboratorio de simulación clínica en el piso 0 (sótano) del Bloque A, junto al auditorio.', NULL, NULL, NULL),
('Papelería', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='A'), 1, 'Papelería y fotocopias junto a la cafetería, Piso 1 del Bloque A.', NULL, NULL, NULL),
('Cajero BBVA', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='A'), 1, 'Cajero automático BBVA en el Piso 1 del Bloque A, cerca de los baños.', NULL, NULL, NULL),
('Facultad de Odontología', 'FACULTAD', (SELECT id FROM edificio WHERE codigo='A'), 1, 'Facultad de Odontología al fondo del Piso 1 del Bloque A, con laboratorios, preclínica, coordinaciones y acreditación.', NULL, NULL, NULL),
('Laboratorio de Preclínica', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='A'), 1, 'Laboratorio de preclínica odontológica con cabezas de simulación (114), Piso 1 del Bloque A.', NULL, NULL, NULL),
('Coordinación Clínica de Odontología', 'OFICINA', (SELECT id FROM edificio WHERE codigo='A'), 1, 'Coordinación clínica y coordinación de preclínica de Odontología, Piso 1 del Bloque A.', NULL, NULL, NULL),
('Sala de profesores de Medicina', 'OFICINA', (SELECT id FROM edificio WHERE codigo='A'), 2, 'Sala de profesores de Medicina, Piso 2 del Bloque A (lado izquierdo, después de la biblioteca).', NULL, NULL, NULL),
('Centro de Investigación', 'OFICINA', (SELECT id FROM edificio WHERE codigo='A'), 2, 'Centro de Investigación, Piso 2 del Bloque A.', NULL, NULL, NULL),
('Laboratorio de Biología Molecular', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='A'), 2, 'Laboratorio de Biología Molecular, Piso 2 del Bloque A; en el mismo piso hay tres laboratorios multifuncionales.', NULL, NULL, NULL),
('Laboratorio de Realidad Mixta', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='A'), 2, 'Laboratorio de Realidad Mixta, Piso 2 del Bloque A.', NULL, NULL, NULL),
('Salas de Informática', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='A'), 3, 'Salas de informática 1 a 5 en el Piso 3 del Bloque A (la sala 6, salón 504, está en el Piso 5).', NULL, NULL, NULL),
('Programa de Derecho', 'OFICINA', (SELECT id FROM edificio WHERE codigo='A'), 4, 'Oficinas del programa de Derecho y salones, Piso 4 del Bloque A.', NULL, NULL, NULL),
('Centro de Investigaciones Sociojurídicas', 'OFICINA', (SELECT id FROM edificio WHERE codigo='A'), 4, 'Centro de Investigaciones Sociojurídicas, Piso 4 del Bloque A.', NULL, NULL, NULL),
('Open Language Center', 'OFICINA', (SELECT id FROM edificio WHERE codigo='A'), 5, 'Open Language Center: centro de inglés de la universidad, Piso 5 del Bloque A. En este piso están los salones 501 a 518.', NULL, NULL, NULL),
('Salón de Arte, Música y Cultura', 'SALON', (SELECT id FROM edificio WHERE codigo='A'), 6, 'Salón de Arte, Música y Cultura en la azotea del Bloque A, junto a las canchas, el punto ecológico y el cuarto técnico.', NULL, NULL, NULL);

-- Facultad de cada programa
UPDATE programa SET lugar_id = (SELECT id FROM lugar WHERE nombre = 'Facultad de Ingeniería')  WHERE facultad = 'Facultad de Ingeniería';
UPDATE programa SET lugar_id = (SELECT id FROM lugar WHERE nombre = 'Facultad de Medicina')    WHERE facultad = 'Facultad de Medicina';
UPDATE programa SET lugar_id = (SELECT id FROM lugar WHERE nombre = 'Facultad de Enfermería')  WHERE facultad = 'Facultad de Enfermería';
UPDATE programa SET lugar_id = (SELECT id FROM lugar WHERE nombre = 'Facultad de Derecho')     WHERE facultad = 'Facultad de Derecho';
UPDATE programa SET lugar_id = (SELECT id FROM lugar WHERE nombre = 'Facultad de Odontología') WHERE facultad = 'Facultad de Odontología';
