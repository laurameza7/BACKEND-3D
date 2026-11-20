-- =====================================================================
-- V5 · Ajustes después de revisar el recorrido 3D contra los videos
-- Idempotente: se puede ejecutar en cada despliegue (va después de V2 y V4).
-- =====================================================================

-- El paso subterráneo entre bloques sale del sótano 2 del Bloque B (nivel de la
-- Clínica Odontológica) y llega al Piso 0 del Bloque A (vestíbulo del auditorio).
UPDATE lugar
   SET piso = -2,
       descripcion = 'Desde el sótano 2 del Bloque B (nivel de la Clínica Odontológica) un pasillo lleva al Piso 0 del Bloque A, junto al auditorio.'
 WHERE nombre = 'Conexión subterránea con el Bloque A';

UPDATE lugar
   SET descripcion = 'Desde el Piso 0 del Bloque A (vestíbulo del auditorio) un pasillo lleva al sótano 2 del Bloque B, donde está la Clínica Odontológica.'
 WHERE nombre = 'Conexión subterránea con el Bloque B';

-- Consultas frecuentes del backend: programas por facultad (lugar) e historial por fecha
CREATE INDEX IF NOT EXISTS idx_programa_lugar       ON programa(lugar_id);
CREATE INDEX IF NOT EXISTS idx_conversacion_fecha   ON conversacion(usuario_id, creado_en DESC);
