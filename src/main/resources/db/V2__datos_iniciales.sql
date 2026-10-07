-- =====================================================================
-- UCC Pasto 3D - Datos iniciales
-- IMPORTANTE: la distribución de los bloques es un modelo esquemático.
-- Ajustar pos_x / pos_z / ancho / profundidad al plano real del campus
-- y validar programas, horarios y contactos con Admisiones / Bienestar.
-- =====================================================================

DELETE FROM programa;
DELETE FROM lugar;
DELETE FROM pregunta_frecuente;
DELETE FROM edificio;

-- ---------------------------------------------------------------- Edificios
INSERT INTO edificio (codigo, nombre, descripcion, pos_x, pos_z, ancho, profundidad, pisos, color, tipo) VALUES
('PORT', 'Portería principal', 'Ingreso peatonal y vehicular al campus. Allí se registran los visitantes.', 0, 48, 8, 5, 1, '#90a4ae', 'EDIFICIO'),
('A',    'Bloque A - Administrativo', 'Admisiones, Registro y Control, Tesorería, Crédito y Dirección del campus.', -22, 26, 24, 12, 3, '#4f7cac', 'EDIFICIO'),
('B',    'Bloque B - Aulas', 'Aulas de clase de los programas de ingeniería, ciencias económicas y derecho.', 22, 26, 24, 12, 4, '#5c9ead', 'EDIFICIO'),
('C',    'Bloque C - Ciencias de la Salud', 'Laboratorios de Morfología Humana, Física y Bioquímica y Simulación Clínica.', -24, -4, 18, 16, 3, '#7aa974', 'EDIFICIO'),
('D',    'Bloque D - Clínica Odontológica', 'Clínica Odontológica donde los estudiantes atienden pacientes bajo supervisión docente.', -24, -28, 18, 14, 2, '#c98d4b', 'EDIFICIO'),
('E',    'Bloque E - Consultorio Jurídico', 'Consultorio Jurídico y Centro de Conciliación de la Facultad de Derecho.', 4, -6, 16, 12, 2, '#a86f9b', 'EDIFICIO'),
('BIB',  'Biblioteca', 'Biblioteca, salas de estudio y préstamo de equipos.', 26, 0, 16, 14, 2, '#d1b05a', 'EDIFICIO'),
('LAB',  'Bloque F - Laboratorios de Ingeniería', 'Salas de cómputo y Laboratorio de Estudio del Trabajo.', 26, -26, 16, 14, 2, '#6b8fc7', 'EDIFICIO'),
('CAF',  'Cafetería y Bienestar', 'Cafetería, Bienestar Universitario y espacios de descanso.', 2, 18, 12, 8, 1, '#e07a5f', 'EDIFICIO'),
('CAN',  'Zona deportiva', 'Canchas múltiples para actividades deportivas y de bienestar.', 0, -40, 30, 14, 0, '#8fbf7f', 'DEPORTIVO'),
('PAR',  'Parqueadero', 'Parqueadero de carros y motos.', 44, 40, 18, 14, 0, '#b0bec5', 'PARQUEADERO');

-- ---------------------------------------------------------------- Lugares
INSERT INTO lugar (nombre, tipo, edificio_id, piso, descripcion, horario, telefono, correo) VALUES
('Admisiones y Mercadeo', 'OFICINA', (SELECT id FROM edificio WHERE codigo='A'), 1, 'Información de inscripciones, requisitos y proceso de admisión.', 'Lunes a viernes 8:00-12:00 y 14:00-18:00', '602 7370660', NULL),
('Registro y Control Académico', 'OFICINA', (SELECT id FROM edificio WHERE codigo='A'), 1, 'Certificados, notas, matrícula académica y grados.', 'Lunes a viernes 8:00-12:00 y 14:00-18:00', '602 7370660', NULL),
('Tesorería y Crédito', 'OFICINA', (SELECT id FROM edificio WHERE codigo='A'), 2, 'Pagos de matrícula, financiación y descuentos.', 'Lunes a viernes 8:00-12:00 y 14:00-18:00', '602 7370660', NULL),
('Dirección de Campus', 'OFICINA', (SELECT id FROM edificio WHERE codigo='A'), 3, 'Dirección general del campus Pasto.', 'Lunes a viernes', '602 7370660', NULL),
('Laboratorio de Morfología Humana', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='C'), 1, 'Práctica de anatomía para programas de salud.', 'Según horario de clases', NULL, NULL),
('Laboratorio de Física y Bioquímica', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='C'), 2, 'Prácticas de física, química y bioquímica.', 'Según horario de clases', NULL, NULL),
('Laboratorio de Simulación Clínica', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='C'), 3, 'Simuladores para práctica clínica de medicina y odontología.', 'Según horario de clases', NULL, NULL),
('Clínica Odontológica', 'CLINICA', (SELECT id FROM edificio WHERE codigo='D'), 1, 'Atención odontológica a la comunidad con estudiantes supervisados.', 'Consultar disponibilidad de citas', '602 7370660 ext. 2292', NULL),
('Consultorio Jurídico y Centro de Conciliación', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='E'), 1, 'Asesoría jurídica gratuita a la comunidad y conciliaciones.', 'Lunes a viernes', '602 7370660 ext. 2269 / 2266', NULL),
('Salas de cómputo', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='LAB'), 1, 'Salas de cómputo para ingeniería y uso general.', 'Lunes a sábado', NULL, NULL),
('Laboratorio de Estudio del Trabajo', 'LABORATORIO', (SELECT id FROM edificio WHERE codigo='LAB'), 2, 'Prácticas de ingeniería industrial: tiempos, métodos y ergonomía.', 'Según horario de clases', NULL, NULL),
('Biblioteca', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='BIB'), 1, 'Préstamo de libros, bases de datos y salas de estudio.', 'Lunes a sábado', NULL, NULL),
('Bienestar Universitario', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='CAF'), 1, 'Deportes, cultura, apoyo psicológico y actividades para estudiantes.', 'Lunes a viernes', NULL, NULL),
('Cafetería', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='CAF'), 1, 'Alimentación y zona de descanso.', 'Lunes a sábado', NULL, NULL),
('Portería', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='PORT'), 1, 'Registro de visitantes e información general.', 'Todos los días', NULL, NULL);

-- ---------------------------------------------------------------- Programas
INSERT INTO programa (nombre, facultad, nivel, modalidad, duracion_semestres, titulo, descripcion, edificio_id) VALUES
('Ingeniería de Software', 'Facultad de Ingeniería', 'PREGRADO', 'PRESENCIAL', 9, 'Ingeniero(a) de Software', 'Forma profesionales para diseñar, construir y desplegar soluciones de software de calidad.', (SELECT id FROM edificio WHERE codigo='B')),
('Ingeniería Industrial', 'Facultad de Ingeniería', 'PREGRADO', 'PRESENCIAL', 9, 'Ingeniero(a) Industrial', 'Optimización de procesos productivos, logística y calidad.', (SELECT id FROM edificio WHERE codigo='B')),
('Derecho', 'Facultad de Derecho', 'PREGRADO', 'PRESENCIAL', 10, 'Abogado(a)', 'Formación jurídica con práctica en el Consultorio Jurídico.', (SELECT id FROM edificio WHERE codigo='E')),
('Medicina', 'Facultad de Medicina', 'PREGRADO', 'PRESENCIAL', 12, 'Médico(a)', 'Formación médica con laboratorios de simulación y morfología.', (SELECT id FROM edificio WHERE codigo='C')),
('Odontología', 'Facultad de Odontología', 'PREGRADO', 'PRESENCIAL', 10, 'Odontólogo(a)', 'Formación en salud oral con práctica en la Clínica Odontológica.', (SELECT id FROM edificio WHERE codigo='D')),
('Psicología', 'Facultad de Ciencias Sociales', 'PREGRADO', 'PRESENCIAL', 10, 'Psicólogo(a)', 'Formación en procesos psicológicos individuales, sociales y organizacionales.', (SELECT id FROM edificio WHERE codigo='B')),
('Contaduría Pública', 'Facultad de Ciencias Económicas, Administrativas y Contables', 'PREGRADO', 'PRESENCIAL', 9, 'Contador(a) Público(a)', 'Formación en contabilidad, finanzas, auditoría y tributaria.', (SELECT id FROM edificio WHERE codigo='B')),
('Administración de Empresas', 'Facultad de Ciencias Económicas, Administrativas y Contables', 'PREGRADO', 'PRESENCIAL', 9, 'Administrador(a) de Empresas', 'Gestión de organizaciones con enfoque en economía solidaria.', (SELECT id FROM edificio WHERE codigo='B'));

-- ---------------------------------------------------------------- Preguntas frecuentes
INSERT INTO pregunta_frecuente (categoria, pregunta, respuesta, palabras_clave) VALUES
('Admisiones', '¿Cómo me inscribo a un programa?', 'La inscripción se hace en línea desde el portal www.ucc.edu.co en la sección de inscripciones. Luego pagas el formulario, cargas los documentos y esperas la citación de Admisiones (Bloque A, piso 1).', 'inscribo,inscripcion,inscripción,matricularme,ingresar,admision,admisión'),
('Admisiones', '¿Qué documentos necesito para inscribirme?', 'Generalmente: documento de identidad, resultados de la prueba Saber 11 (ICFES), diploma o acta de grado de bachiller y una foto. Confirma la lista exacta con Admisiones.', 'documentos,requisitos,icfes,saber 11,papeles'),
('Pagos', '¿Dónde pago la matrícula?', 'Puedes pagar en línea desde el portal de la universidad o en los bancos autorizados. Para financiación o descuentos acércate a Tesorería y Crédito, Bloque A, piso 2.', 'pago,pagar,matricula,matrícula,tesoreria,tesorería,financiacion,credito,crédito,descuento'),
('Contacto', '¿Cuál es el teléfono de la universidad?', 'El conmutador del campus Pasto es 602 7370660. También hay atención por WhatsApp al +57 312 4689345.', 'telefono,teléfono,contacto,llamar,whatsapp,numero,número'),
('Servicios', '¿Cómo pido una cita en la Clínica Odontológica?', 'La Clínica Odontológica está en el Bloque D. Puedes pedir información al 602 7370660 extensión 2292.', 'odontologia,odontología,dientes,cita,clinica,clínica,dentista'),
('Servicios', '¿La universidad da asesoría jurídica gratuita?', 'Sí. El Consultorio Jurídico y Centro de Conciliación (Bloque E) atiende a la comunidad. Teléfono 602 7370660 extensiones 2269 o 2266.', 'juridico,jurídico,abogado,asesoria,asesoría,conciliacion,conciliación,demanda'),
('Académico', '¿Dónde solicito un certificado de notas?', 'En Registro y Control Académico, Bloque A, piso 1, o a través del portal estudiantil.', 'certificado,notas,constancia,registro'),
('Campus', '¿Dónde queda la biblioteca?', 'La biblioteca está al costado oriental del campus, junto al Bloque B. Puedes verla resaltada en el mapa 3D.', 'biblioteca,libros,estudiar'),
('Campus', '¿Hay parqueadero?', 'Sí, el campus tiene parqueadero para carros y motos junto a la portería principal.', 'parqueadero,parquear,carro,moto,estacionar');
