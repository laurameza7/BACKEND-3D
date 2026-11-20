-- =====================================================================
-- UCC Pasto 3D - Data of the Pasto campus (Calle 18 N.º 45-150, Torobajo)
--
-- Sources:
--   * Layout of blocks, floors, spaces and signs: photos and walkthroughs
--     recorded by students on campus + Google Maps satellite view.
--   * Programs and durations: academic offer poster at the entrance.
--   * Extensions, faculties and Legal Clinic: official directory
--     https://ucc.edu.co/campus-pasto/Paginas/directorio-campus-pasto.aspx
--
-- 3D map coordinates in meters: x towards Carrera 45 (east),
-- z towards Calle 18 (front). Negative floor = basement.
-- In Block B the university calls the entrance "Piso 1" and the first
-- basement "Piso 0". Anything not verified is left empty instead of invented.
-- Names, descriptions and answers stay in Spanish: they are shown to users.
-- =====================================================================

DELETE FROM program;
DELETE FROM place;
DELETE FROM faq;
DELETE FROM building;

-- ---------------------------------------------------------------- Buildings and areas
INSERT INTO building (code, name, description, pos_x, pos_z, width, depth, floors, basements, color, type) VALUES
('ACC',    'Acceso principal', 'Pabellón de entrada sobre la Calle 18, con cubierta inclinada de vidrio y estructura metálica y el aviso de la Universidad Cooperativa de Colombia. Allí están la portería con torniquetes (se ingresa con el carné TIP UCC), la cooperativa Comuna, el café Bonne Vie, corresponsales bancarios y un minimarket.', 6, 25, 26, 11, 1, 0, '#9fb0bd', 'ENTRANCE'),
('SEND',   'Sendero de los cipreses', 'Sendero peatonal que va desde el acceso principal hasta el Bloque A. Está bordeado de cipreses, jardines con muros de piedra y palmas; el Bloque B queda a la derecha.', 6, 1, 14, 36, 0, 0, '#9cc58a', 'GREEN_AREA'),
('BICI',   'Estación UrkuBici', 'Estación de bicicletas compartidas UrkuBici, con cubierta de paneles solares, a la izquierda del sendero frente al Bloque A.', -9, -11, 12, 8, 0, 0, '#3b4652', 'BIKE_STATION'),
('MESAS',  'Plazoleta de mesas', 'Plazoleta con mesas al aire libre detrás del Bloque B, junto a una sala de juegos (ping-pong) y un punto de encuentro de evacuación.', 39, -28, 12, 16, 0, 0, '#a8a29a', 'PLAZA'),
('B',      'Bloque B', 'Primer edificio a la derecha al entrar al campus, de ladrillo con bahías de vidrio. Tiene 7 pisos y 2 sótanos conectados con el Bloque A. Se entra por rampas desde el sendero al Piso 1, donde están Admisiones, Registro y Control Académico, Tesorería, Contabilidad, ICETEX y AMI. En el Piso 0 (sótano 1) están los lockers y el Laboratorio de Morfología, y en el sótano 2 la Clínica Odontológica.', 26, -1, 22, 30, 7, 2, '#b4553d', 'BUILDING'),
('A',      'Bloque A', 'Edificio del fondo del campus, al final del sendero de los cipreses, con el logo de la universidad en lo alto. Tiene 6 pisos y 2 sótanos conectados con el Bloque B. Allí están la cafetería, la biblioteca, el auditorio y, en el último piso, las canchas de voleibol y sintética.', -2, -30, 44, 24, 6, 2, '#a94f39', 'BUILDING');

-- ---------------------------------------------------------------- Places
INSERT INTO place (name, type, building_id, floor, description, schedule, phone, email) VALUES
-- Main entrance
('Portería y torniquetes', 'SERVICE', (SELECT id FROM building WHERE code='ACC'), 1, 'Recepción y torniquetes de ingreso. Para entrar y salir se usa el carné TIP UCC; los visitantes se registran en la recepción.', NULL, '602 7370660 (conmutador)', NULL),
('Cooperativa Comuna', 'SERVICE', (SELECT id FROM building WHERE code='ACC'), 1, 'Oficina de la cooperativa Comuna: crédito estudiantil, CrediAportes, crédito de libranza, de libre inversión y de contingencia.', NULL, '602 7332030', NULL),
('Café Bonne Vie', 'SERVICE', (SELECT id FROM building WHERE code='ACC'), 1, 'Café con terraza de mesas junto a la entrada.', NULL, NULL, NULL),
('Corresponsales bancarios y Mini Market', 'SERVICE', (SELECT id FROM building WHERE code='ACC'), 1, 'Corresponsales bancarios (Bancolombia, Nequi) y minimarket en el local junto al café.', NULL, NULL, NULL),
-- Outdoor areas
('Bicicletas compartidas UrkuBici', 'SERVICE', (SELECT id FROM building WHERE code='BICI'), 1, 'Préstamo de bicicletas compartidas. La cubierta tiene paneles solares.', NULL, NULL, NULL),
('Sala de juegos', 'SERVICE', (SELECT id FROM building WHERE code='MESAS'), 1, 'Salón con mesa de ping-pong junto a la plazoleta de mesas.', NULL, NULL, NULL),
('Punto de encuentro (evacuación)', 'SAFETY', (SELECT id FROM building WHERE code='MESAS'), 1, 'Punto de encuentro señalizado (PE) para emergencias.', NULL, NULL, NULL),
-- Block B · Floor 1 (entrance)
('Admisiones, Registro y Control Académico (DARC)', 'OFFICE', (SELECT id FROM building WHERE code='B'), 1, 'Inscripciones, matrícula académica, certificados y notas. A la derecha al entrar al Bloque B.', NULL, 'Ext. 2312 · 2313 · 2314', NULL),
('Tesorería', 'OFFICE', (SELECT id FROM building WHERE code='B'), 1, 'Pagos de matrícula y otros pagos. Señalizada a la izquierda al entrar al Bloque B.', NULL, 'Ext. 2118', NULL),
('Contabilidad', 'OFFICE', (SELECT id FROM building WHERE code='B'), 1, 'Oficina de contabilidad, señalizada a la izquierda al entrar al Bloque B.', NULL, 'Ext. 2131', NULL),
('ICETEX y crédito estudiantil', 'OFFICE', (SELECT id FROM building WHERE code='B'), 1, 'Atención de créditos ICETEX, señalizada a la izquierda al entrar al Bloque B.', NULL, 'Ext. 2120', NULL),
('AMI (Atención Más Integral)', 'OFFICE', (SELECT id FROM building WHERE code='B'), 1, 'Punto de atención integral al estudiante, a la entrada del Bloque B.', NULL, NULL, NULL),
('Sala de espera del Piso 1', 'SERVICE', (SELECT id FROM building WHERE code='B'), 1, 'Sillas de espera junto a las escaleras que bajan al Piso 0 y a la Clínica Odontológica.', NULL, NULL, NULL),
-- Block B · Floor 0 (basement 1)
('Lockers de estudiantes', 'SERVICE', (SELECT id FROM building WHERE code='B'), -1, 'Casilleros para estudiantes en el Piso 0 (sótano 1) del Bloque B. Se baja por las escaleras desde el Piso 1.', NULL, NULL, NULL),
('Laboratorio de Morfología', 'LABORATORY', (SELECT id FROM building WHERE code='B'), -1, 'Laboratorio de Morfología Humana, en el Piso 0 (sótano 1) del Bloque B, con su zona de casilleros.', 'Según horario de clases', NULL, NULL),
('Conexión subterránea con el Bloque A', 'PASSAGE', (SELECT id FROM building WHERE code='B'), -1, 'Los sótanos del Bloque B se comunican con los del Bloque A.', NULL, NULL, NULL),
-- Block B · basement 2 (Dental Clinic)
('Clínica Odontológica (pregrado)', 'CLINIC', (SELECT id FROM building WHERE code='B'), -2, 'Clínica donde los estudiantes de Odontología atienden pacientes bajo supervisión docente. Se baja por la escalera señalizada desde el Piso 1 del Bloque B.', NULL, 'Citas: ext. 2390 / 2298 · Coordinación: ext. 2297', NULL),
('Clínica Odontológica de Posgrado', 'CLINIC', (SELECT id FROM building WHERE code='B'), -2, 'Clínica de los posgrados de Odontología (Endodoncia, Ortodoncia, Periodoncia).', NULL, 'Ext. 2297', NULL),
('Sala de espera de la Clínica Odontológica', 'SERVICE', (SELECT id FROM building WHERE code='B'), -2, 'Sala de espera de pacientes con zona de juegos para niños.', NULL, NULL, NULL),
('Atención al Usuario de la clínica', 'SERVICE', (SELECT id FROM building WHERE code='B'), -2, 'Ventanilla de atención a pacientes y área de almacenamiento de insumos de estudiantes.', NULL, 'Ext. 2390 / 2298', NULL),
('Dental Deals', 'SERVICE', (SELECT id FROM building WHERE code='B'), -2, 'Venta de artículos y equipos odontológicos para estudiantes, junto a Atención al Usuario.', NULL, NULL, NULL),
('Archivo de Historias Clínicas', 'OFFICE', (SELECT id FROM building WHERE code='B'), -2, 'Archivo de historias clínicas de los pacientes de la Clínica Odontológica.', NULL, NULL, NULL),
('Baños de la clínica', 'SERVICE', (SELECT id FROM building WHERE code='B'), -2, 'Baños de hombres y mujeres junto a Atención al Usuario.', NULL, NULL, NULL),
-- Block A
('Cafetería', 'SERVICE', (SELECT id FROM building WHERE code='A'), 1, 'Cafetería del campus, en el primer piso del Bloque A.', NULL, NULL, NULL),
('Biblioteca', 'SERVICE', (SELECT id FROM building WHERE code='A'), 2, 'Biblioteca y hemeroteca. Ocupa los pisos 2 y 3 del Bloque A.', NULL, 'Ext. 2075 · Hemeroteca ext. 2330', NULL),
('Auditorio', 'SERVICE', (SELECT id FROM building WHERE code='A'), -1, 'Auditorio del campus, en el nivel subterráneo del Bloque A.', NULL, NULL, NULL),
('Cancha sintética', 'SPORTS', (SELECT id FROM building WHERE code='A'), 6, 'Cancha de grama sintética en el último piso (azotea) del Bloque A, protegida con malla.', NULL, NULL, NULL),
('Cancha de voleibol', 'SPORTS', (SELECT id FROM building WHERE code='A'), 6, 'Cancha de voleibol en el último piso (azotea) del Bloque A.', NULL, NULL, NULL),
('Conexión subterránea con el Bloque B', 'PASSAGE', (SELECT id FROM building WHERE code='A'), -1, 'Los sótanos del Bloque A se comunican con los del Bloque B.', NULL, NULL, NULL);

-- ---------------------------------------------------------------- Programs (entrance poster)
INSERT INTO program (name, faculty, level, modality, duration_semesters, degree_title, description, building_id) VALUES
('Derecho', 'Facultad de Derecho', 'UNDERGRADUATE', 'IN_PERSON', 9, 'Abogado(a)', 'Formación jurídica con práctica en el Consultorio Jurídico (sede Centro). Facultad: ext. 2261 / 2262.', NULL),
('Enfermería', 'Facultad de Enfermería', 'UNDERGRADUATE', 'IN_PERSON', 8, 'Enfermero(a)', 'Formación en cuidado de la salud de personas y comunidades. Facultad: ext. 2295 · Decanatura ext. 2291.', NULL),
('Ingeniería Industrial', 'Facultad de Ingeniería', 'UNDERGRADUATE', 'IN_PERSON', 9, 'Ingeniero(a) Industrial', 'Optimización de procesos productivos, logística y calidad. Facultad: ext. 2272 · Decanatura ext. 2126.', NULL),
('Ingeniería de Software', 'Facultad de Ingeniería', 'UNDERGRADUATE', 'IN_PERSON', 8, 'Ingeniero(a) de Software', 'Diseño, construcción y despliegue de soluciones de software. Facultad de Ingeniería: ext. 2272.', NULL),
('Medicina', 'Facultad de Medicina', 'UNDERGRADUATE', 'IN_PERSON', 12, 'Médico(a)', 'Formación médica con laboratorios de morfología, simulación clínica y bioquímica. Facultad: ext. 2191 / 2117.', NULL),
('Odontología', 'Facultad de Odontología', 'UNDERGRADUATE', 'IN_PERSON', 10, 'Odontólogo(a)', 'Programa con reacreditación en alta calidad y acreditación internacional en alta calidad. Práctica en la Clínica Odontológica (sótano 2 del Bloque B). Facultad: ext. 2263 · Decanatura ext. 2282.', (SELECT id FROM building WHERE code='B')),
('Técnico Laboral por Competencias en Auxiliar en Enfermería', 'Facultad de Enfermería', 'TECHNICAL', 'IN_PERSON', 2, 'Técnico Laboral en Auxiliar en Enfermería', 'Programa técnico laboral por competencias.', NULL),
-- Specializations
('Especialización en Derecho Médico', 'Facultad de Derecho', 'SPECIALIZATION', 'IN_PERSON', 2, 'Especialista en Derecho Médico', 'Posgrado de la Facultad de Derecho.', NULL),
('Especialización en Familia y Género', 'Facultad de Derecho', 'SPECIALIZATION', 'HYBRID', 2, 'Especialista en Familia y Género', 'Posgrado en modalidad híbrida.', NULL),
('Especialización en Propiedad Intelectual', 'Facultad de Derecho', 'SPECIALIZATION', 'IN_PERSON', 2, 'Especialista en Propiedad Intelectual', 'Posgrado de la Facultad de Derecho.', NULL),
('Especialización en Producción y Comercio del Café', 'Posgrados UCC', 'SPECIALIZATION', 'HYBRID', 2, 'Especialista en Producción y Comercio del Café', 'Posgrado en modalidad híbrida.', NULL),
('Especialización en Dirección de la Producción con Sistemas Integrados de Gestión', 'Posgrados UCC', 'SPECIALIZATION', 'HYBRID', 2, 'Especialista en Dirección de la Producción con Sistemas Integrados de Gestión', 'Posgrado en modalidad híbrida.', NULL),
('Especialización en Ginecología y Obstetricia', 'Facultad de Medicina', 'SPECIALIZATION', 'IN_PERSON', 6, 'Especialista en Ginecología y Obstetricia', 'Especialización médico-quirúrgica. Información: ext. 2191 / 2117.', NULL),
('Especialización en Medicina Interna', 'Facultad de Medicina', 'SPECIALIZATION', 'IN_PERSON', 6, 'Especialista en Medicina Interna', 'Especialización médica. Información: ext. 2191 / 2117.', NULL),
('Especialización en Pediatría', 'Facultad de Medicina', 'SPECIALIZATION', 'IN_PERSON', 6, 'Especialista en Pediatría', 'Especialización médica. Información: ext. 2191 / 2117.', NULL),
('Especialización en Psiquiatría', 'Facultad de Medicina', 'SPECIALIZATION', 'IN_PERSON', 6, 'Especialista en Psiquiatría', 'Especialización médica. Información: ext. 2191 / 2117.', NULL),
('Especialización en Endodoncia', 'Facultad de Odontología', 'SPECIALIZATION', 'IN_PERSON', 4, 'Especialista en Endodoncia', 'Práctica en la Clínica Odontológica de Posgrado (sótano 2 del Bloque B).', (SELECT id FROM building WHERE code='B')),
('Especialización en Ortodoncia', 'Facultad de Odontología', 'SPECIALIZATION', 'IN_PERSON', 6, 'Especialista en Ortodoncia', 'Práctica en la Clínica Odontológica de Posgrado (sótano 2 del Bloque B).', (SELECT id FROM building WHERE code='B')),
('Especialización en Periodoncia', 'Facultad de Odontología', 'SPECIALIZATION', 'IN_PERSON', 4, 'Especialista en Periodoncia', 'Práctica en la Clínica Odontológica de Posgrado (sótano 2 del Bloque B).', (SELECT id FROM building WHERE code='B'));

-- ---------------------------------------------------------------- Frequently asked questions
INSERT INTO faq (category, question, answer, keywords) VALUES
('Contacto', '¿Dónde queda la universidad y cuál es el teléfono?', 'El campus Pasto queda en la Calle 18 N.º 45-150, sector Torobajo. El conmutador es 602 7370660 (opción 1) y para inscripciones hay WhatsApp al 317 884 8948 y al 311 476 8542.', 'telefono,teléfono,contacto,llamar,numero,número,direccion,dirección,donde queda,ubicacion,ubicación,torobajo,whatsapp'),
('Campus', '¿Cómo entro a la universidad?', 'Se entra por el pabellón de vidrio de la Calle 18. En la portería hay torniquetes: estudiantes y personal pasan con el carné TIP UCC y los visitantes se registran en la recepción. Desde allí el sendero de los cipreses lleva al Bloque B (a la derecha) y al Bloque A (al fondo).', 'entrar,entrada,ingreso,ingresar,porteria,portería,torniquete,carne,carné,tip,visitante'),
('Admisiones', '¿Cómo me inscribo a un programa?', 'La inscripción se hace en línea en www.ucc.edu.co. Para orientación ve a Admisiones, Registro y Control Académico en el Piso 1 del Bloque B (a la derecha al entrar), ext. 2312, o escribe al WhatsApp 317 884 8948.', 'inscribo,inscripcion,inscripción,inscribirme,matricularme,ingresar,admision,admisión,admisiones'),
('Admisiones', '¿Qué documentos necesito para inscribirme?', 'Generalmente: documento de identidad, resultados de la prueba Saber 11, diploma o acta de grado de bachiller y una foto. Confirma la lista exacta con Admisiones (Piso 1 del Bloque B, ext. 2312).', 'documentos,requisitos,icfes,saber 11,papeles'),
('Académico', '¿Dónde solicito un certificado de notas?', 'En Admisiones, Registro y Control Académico (DARC), Piso 1 del Bloque B, ext. 2312 o 2313. También puedes revisar el portal estudiantil.', 'certificado,notas,constancia,registro,control academico,darc'),
('Pagos', '¿Dónde pago la matrícula?', 'Puedes pagar en línea desde el portal de la universidad o en los bancos autorizados. Para dudas ve a Tesorería, en el Piso 1 del Bloque B (ext. 2118). ICETEX: ext. 2120. La cooperativa Comuna, en la entrada, ofrece crédito estudiantil (602 7332030).', 'pago,pagar,matricula,matrícula,tesoreria,tesorería,financiacion,financiación,credito,crédito,icetex,descuento,comuna'),
('Servicios', '¿Dónde queda la Clínica Odontológica y cómo pido una cita?', 'La Clínica Odontológica está en el sótano 2 del Bloque B: entra al Bloque B y baja por la escalera señalizada "Clínica Odontológica". Para citas llama al 602 7370660 ext. 2390 o 2298 (coordinación ext. 2297).', 'odontologia,odontología,dientes,diente,cita,clinica,clínica,dentista,muela,odontologica,odontológica'),
('Servicios', '¿Dónde están los lockers?', 'Los lockers de estudiantes están en el Piso 0 (sótano 1) del Bloque B. Se baja por las escaleras desde el Piso 1.', 'locker,lockers,casillero,casilleros,guardar'),
('Servicios', '¿La universidad da asesoría jurídica gratuita?', 'Sí. El Consultorio Jurídico y Centro de Conciliación no está en el campus Torobajo: queda en la Calle 18 N.º 23-68, Centro. Teléfonos 602 7291081 y 602 7291082 (coordinación ext. 2220, auxiliar ext. 2269).', 'juridico,jurídico,abogado,asesoria,asesoría,conciliacion,conciliación,demanda,consultorio'),
('Campus', '¿Dónde queda la biblioteca?', 'La biblioteca está en los pisos 2 y 3 del Bloque A, el edificio del fondo del campus al final del sendero de los cipreses. Teléfono: ext. 2075.', 'biblioteca,libros,estudiar,hemeroteca'),
('Campus', '¿Dónde puedo comer?', 'La cafetería está en el primer piso del Bloque A. En la entrada también están el café Bonne Vie con terraza y un minimarket.', 'cafeteria,cafetería,comer,almorzar,comida,almuerzo,cafe,café,minimarket'),
('Campus', '¿Dónde queda el auditorio?', 'El auditorio está en el nivel subterráneo del Bloque A. Los sótanos del Bloque A y del Bloque B están conectados.', 'auditorio,evento,conferencia,sotano,sótano'),
('Campus', '¿Hay canchas para hacer deporte?', 'Sí. En el último piso del Bloque A hay una cancha sintética y una cancha de voleibol. Junto a la plazoleta de mesas detrás del Bloque B hay una sala de juegos con ping-pong.', 'cancha,canchas,deporte,futbol,fútbol,voley,voleibol,sintetica,sintética,ping pong,juegos'),
('Campus', '¿Hay bicicletas en la universidad?', 'Sí. La estación UrkuBici de bicicletas compartidas está a la izquierda del sendero de los cipreses, frente al Bloque A. Su cubierta tiene paneles solares.', 'bicicleta,bicicletas,bici,urkubici,ciclas'),
('Campus', '¿Cuántos edificios tiene el campus?', 'El campus tiene dos edificios: el Bloque B, el primero a la derecha al entrar (7 pisos y 2 sótanos), y el Bloque A, al fondo del sendero (6 pisos y 2 sótanos). Sus sótanos están conectados.', 'edificios,bloques,bloque,cuantos pisos,pisos,sotanos,sótanos'),
('Servicios', '¿Cómo contacto a Bienestar Institucional?', 'Bienestar Institucional: 602 7370660 ext. 2198.', 'bienestar,psicologo,psicólogo,deportes,cultura,apoyo'),
('Servicios', '¿Qué laboratorios tiene el campus?', 'El Laboratorio de Morfología está en el Piso 0 (sótano 1) del Bloque B. El campus también tiene laboratorios de Física y Bioquímica, Simulación Clínica y Estudio del Trabajo, cuya ubicación aún no está registrada en este mapa.', 'laboratorio,laboratorios,lab,simulacion,simulación,morfologia,morfología'),
('Académico', '¿Qué posgrados hay?', 'Especializaciones: Derecho Médico, Familia y Género, Propiedad Intelectual, Producción y Comercio del Café, Dirección de la Producción con Sistemas Integrados de Gestión, Ginecología y Obstetricia, Medicina Interna, Pediatría, Psiquiatría, Endodoncia, Ortodoncia y Periodoncia.', 'posgrado,posgrados,postgrado,especializacion,especialización,especializaciones,maestria,maestría'),
('Académico', '¿Cómo contacto a mi facultad?', 'Extensiones (602 7370660): Ingeniería 2272, Derecho 2261, Medicina 2191 o 2117, Enfermería 2295, Odontología 2263.', 'facultad,decano,decanatura,programa,coordinador,jefe de programa'),
('Servicios', '¿Con quién hablo sobre intercambios o egresados?', 'Internacionalización y Egresados: ext. 2241 o 2125.', 'intercambio,internacional,internacionalizacion,internacionalización,egresado,egresados,movilidad');
