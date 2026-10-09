-- =====================================================================
-- UCC Pasto 3D - Datos del campus Pasto (Calle 18 N.º 45-150, Torobajo)
--
-- Fuentes:
--   * Distribución de bloques, pisos y espacios: información aportada por
--     estudiantes del campus + vista satélite de Google Maps.
--   * Extensiones, facultades y Consultorio Jurídico: directorio oficial
--     https://ucc.edu.co/campus-pasto/Paginas/directorio-campus-pasto.aspx
--
-- Coordenadas del mapa 3D en metros: x hacia la Carrera 45 (oriente),
-- z hacia la Calle 18 (frente). Piso negativo = sótano.
-- Lo que aún no está verificado (ubicación exacta de oficinas, duración de
-- programas) se deja vacío en lugar de inventarlo.
-- =====================================================================

DELETE FROM programa;
DELETE FROM lugar;
DELETE FROM pregunta_frecuente;
DELETE FROM edificio;

-- ---------------------------------------------------------------- Edificios
INSERT INTO edificio (codigo, nombre, descripcion, pos_x, pos_z, ancho, profundidad, pisos, sotanos, color, tipo) VALUES
('ACC',   'Acceso principal', 'Pabellón de vidrio y estructura metálica con el logo de la universidad, sobre la Calle 18. Es la entrada peatonal al campus, con torniquetes y portería.', 20, 22, 16, 8, 1, 0, '#9fb0bd', 'ACCESO'),
('PLAZA', 'Plazoleta y sendero', 'Sendero peatonal con jardines y palmas que lleva desde el acceso principal hasta el Bloque A, pasando junto al Bloque B.', 3, 3, 20, 38, 0, 0, '#9cc58a', 'ZONA_VERDE'),
('B',     'Bloque B', 'Primer edificio a la derecha al entrar al campus. Tiene 7 pisos y 2 sótanos que se conectan con los sótanos del Bloque A. Fachada de ladrillo con bahías de vidrio.', 26, -1, 22, 30, 7, 2, '#b4553d', 'EDIFICIO'),
('A',     'Bloque A', 'Edificio del fondo del campus. Tiene 6 pisos y 2 sótanos conectados con el Bloque B. Allí están la cafetería, la biblioteca, el auditorio y, en el último piso, las canchas de voleibol y sintética.', -2, -30, 44, 24, 6, 2, '#a94f39', 'EDIFICIO');

-- ---------------------------------------------------------------- Lugares
INSERT INTO lugar (nombre, tipo, edificio_id, piso, descripcion, horario, telefono, correo) VALUES
('Portería y torniquetes', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='ACC'), 1, 'Ingreso peatonal al campus y orientación a visitantes.', NULL, '602 7370660 (conmutador)', NULL),
('Cafetería', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='A'), 1, 'Cafetería del campus, en el primer piso del Bloque A.', NULL, NULL, NULL),
('Biblioteca', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='A'), 2, 'Biblioteca y hemeroteca. Ocupa los pisos 2 y 3 del Bloque A.', NULL, 'Ext. 2075 · Hemeroteca ext. 2330', NULL),
('Auditorio', 'SERVICIO', (SELECT id FROM edificio WHERE codigo='A'), -1, 'Auditorio del campus, en el nivel subterráneo del Bloque A.', NULL, NULL, NULL),
('Cancha sintética', 'DEPORTIVO', (SELECT id FROM edificio WHERE codigo='A'), 6, 'Cancha de grama sintética en el último piso (azotea) del Bloque A.', NULL, NULL, NULL),
('Cancha de voleibol', 'DEPORTIVO', (SELECT id FROM edificio WHERE codigo='A'), 6, 'Cancha de voleibol en el último piso (azotea) del Bloque A.', NULL, NULL, NULL),
('Conexión subterránea con el Bloque B', 'CIRCULACION', (SELECT id FROM edificio WHERE codigo='A'), -1, 'Los sótanos del Bloque A se comunican con los del Bloque B.', NULL, NULL, NULL),
('Conexión subterránea con el Bloque A', 'CIRCULACION', (SELECT id FROM edificio WHERE codigo='B'), -1, 'Los sótanos del Bloque B se comunican con los del Bloque A.', NULL, NULL, NULL);

-- ---------------------------------------------------------------- Programas
-- Facultades del campus según el directorio oficial. Duración y bloque de clases: por verificar.
INSERT INTO programa (nombre, facultad, nivel, modalidad, duracion_semestres, titulo, descripcion, edificio_id) VALUES
('Ingeniería de Software', 'Facultad de Ingeniería', 'PREGRADO', 'PRESENCIAL', NULL, 'Ingeniero(a) de Software', 'Diseño, construcción y despliegue de soluciones de software. Facultad de Ingeniería: ext. 2272.', NULL),
('Ingeniería Industrial', 'Facultad de Ingeniería', 'PREGRADO', 'PRESENCIAL', NULL, 'Ingeniero(a) Industrial', 'Optimización de procesos productivos, logística y calidad. Facultad de Ingeniería: ext. 2272 · Decanatura ext. 2126.', NULL),
('Derecho', 'Facultad de Derecho', 'PREGRADO', 'PRESENCIAL', NULL, 'Abogado(a)', 'Formación jurídica con práctica en el Consultorio Jurídico (sede Centro). Facultad: ext. 2261 / 2262.', NULL),
('Medicina', 'Facultad de Medicina', 'PREGRADO', 'PRESENCIAL', NULL, 'Médico(a)', 'Formación médica con laboratorios de morfología, simulación clínica y bioquímica. Facultad: ext. 2191 / 2117.', NULL),
('Enfermería', 'Facultad de Enfermería', 'PREGRADO', 'PRESENCIAL', NULL, 'Enfermero(a)', 'Formación en cuidado de la salud de personas y comunidades. Facultad: ext. 2295 · Decanatura ext. 2291.', NULL),
('Odontología', 'Facultad de Odontología', 'PREGRADO', 'PRESENCIAL', NULL, 'Odontólogo(a)', 'Formación en salud oral con práctica en la Clínica Odontológica. Facultad: ext. 2263 · Decanatura ext. 2282.', NULL),
('Especialización en Medicina Interna', 'Facultad de Medicina', 'ESPECIALIZACION', 'PRESENCIAL', NULL, 'Especialista en Medicina Interna', 'Posgrado de la Facultad de Medicina. Información: ext. 2191 / 2117.', NULL),
('Especialización en Psiquiatría', 'Facultad de Medicina', 'ESPECIALIZACION', 'PRESENCIAL', NULL, 'Especialista en Psiquiatría', 'Posgrado de la Facultad de Medicina. Información: ext. 2191 / 2117.', NULL);

-- ---------------------------------------------------------------- Preguntas frecuentes
INSERT INTO pregunta_frecuente (categoria, pregunta, respuesta, palabras_clave) VALUES
('Contacto', '¿Dónde queda la universidad y cuál es el teléfono?', 'El campus Pasto queda en la Calle 18 N.º 45-150, barrio Torobajo. El conmutador es 602 7370660 y para inscripciones hay WhatsApp al 317 884 8948.', 'telefono,teléfono,contacto,llamar,numero,número,direccion,dirección,donde queda,ubicacion,ubicación,torobajo'),
('Admisiones', '¿Cómo me inscribo a un programa?', 'La inscripción se hace en línea en www.ucc.edu.co. Para orientación comunícate con Admisiones, Registro y Control Académico (ext. 2312) o por WhatsApp de inscripciones al 317 884 8948.', 'inscribo,inscripcion,inscripción,inscribirme,matricularme,ingresar,admision,admisión,admisiones'),
('Admisiones', '¿Qué documentos necesito para inscribirme?', 'Generalmente: documento de identidad, resultados de la prueba Saber 11, diploma o acta de grado de bachiller y una foto. Confirma la lista exacta con Admisiones (ext. 2312).', 'documentos,requisitos,icfes,saber 11,papeles'),
('Académico', '¿Dónde solicito un certificado de notas?', 'En Admisiones, Registro y Control Académico: ext. 2312 o 2313. También puedes revisar el portal estudiantil.', 'certificado,notas,constancia,registro,control academico'),
('Pagos', '¿Dónde pago la matrícula?', 'Puedes pagar en línea desde el portal de la universidad o en los bancos autorizados. Para dudas de pagos comunícate con Tesorería (ext. 2118). Crédito estudiantil e ICETEX: ext. 2120.', 'pago,pagar,matricula,matrícula,tesoreria,tesorería,financiacion,financiación,credito,crédito,icetex,descuento'),
('Servicios', '¿Cómo pido una cita en la Clínica Odontológica?', 'Puedes agendar tu cita en la Clínica Odontológica llamando al 602 7370660, extensiones 2390 o 2298. Coordinación de la clínica: ext. 2297.', 'odontologia,odontología,dientes,diente,cita,clinica,clínica,dentista,muela'),
('Servicios', '¿La universidad da asesoría jurídica gratuita?', 'Sí. El Consultorio Jurídico y Centro de Conciliación no está en el campus Torobajo: queda en la Calle 18 N.º 23-68, Centro. Teléfonos 602 7291081 y 602 7291082 (coordinación ext. 2220, auxiliar ext. 2269).', 'juridico,jurídico,abogado,asesoria,asesoría,conciliacion,conciliación,demanda,consultorio'),
('Campus', '¿Dónde queda la biblioteca?', 'La biblioteca está en los pisos 2 y 3 del Bloque A, el edificio del fondo del campus. Teléfono: ext. 2075.', 'biblioteca,libros,estudiar,hemeroteca'),
('Campus', '¿Dónde queda la cafetería?', 'La cafetería está en el primer piso del Bloque A.', 'cafeteria,cafetería,comer,almorzar,comida,almuerzo'),
('Campus', '¿Dónde queda el auditorio?', 'El auditorio está en el nivel subterráneo del Bloque A. Los sótanos del Bloque A y del Bloque B están conectados.', 'auditorio,evento,conferencia,sotano,sótano'),
('Campus', '¿Hay canchas para hacer deporte?', 'Sí. En el último piso del Bloque A hay una cancha sintética y una cancha de voleibol.', 'cancha,canchas,deporte,futbol,fútbol,voley,voleibol,sintetica,sintética'),
('Campus', '¿Cuántos edificios tiene el campus?', 'El campus tiene dos edificios: el Bloque B, el primero a la derecha al entrar (7 pisos y 2 sótanos), y el Bloque A, al fondo (6 pisos y 2 sótanos). Sus sótanos están conectados.', 'edificios,bloques,bloque,cuantos pisos,pisos,sotanos,sótanos'),
('Servicios', '¿Cómo contacto a Bienestar Institucional?', 'Bienestar Institucional: 602 7370660 ext. 2198.', 'bienestar,psicologo,psicólogo,deportes,cultura,apoyo'),
('Servicios', '¿Qué laboratorios tiene el campus?', 'El campus cuenta con laboratorios de Física y Bioquímica, Morfología Humana, Simulación Clínica y Estudio del Trabajo. Su ubicación exacta dentro de los bloques aún no está registrada en este mapa.', 'laboratorio,laboratorios,lab,simulacion,simulación,morfologia,morfología'),
('Académico', '¿Cómo contacto a mi facultad?', 'Extensiones (602 7370660): Ingeniería 2272, Derecho 2261, Medicina 2191 o 2117, Enfermería 2295, Odontología 2263.', 'facultad,decano,decanatura,programa,coordinador,jefe de programa'),
('Servicios', '¿Con quién hablo sobre intercambios o egresados?', 'Internacionalización y Egresados: ext. 2241 o 2125.', 'intercambio,internacional,internacionalizacion,internacionalización,egresado,egresados,movilidad');
