-- =====================================================================
-- V5 · Adjustments after checking the 3D walkthrough against the videos
-- Idempotent: it can run on every deployment (it runs after V2 and V4).
-- =====================================================================

-- The underground passage between blocks starts at basement 2 of Block B (Dental
-- Clinic level) and arrives at Floor 0 of Block A (auditorium lobby).
UPDATE place
   SET floor = -2,
       description = 'Desde el sótano 2 del Bloque B (nivel de la Clínica Odontológica) un pasillo lleva al Piso 0 del Bloque A, junto al auditorio.'
 WHERE name = 'Conexión subterránea con el Bloque A';

UPDATE place
   SET description = 'Desde el Piso 0 del Bloque A (vestíbulo del auditorio) un pasillo lleva al sótano 2 del Bloque B, donde está la Clínica Odontológica.'
 WHERE name = 'Conexión subterránea con el Bloque B';

-- Frequent backend queries: programs by faculty (place) and history by date
CREATE INDEX IF NOT EXISTS idx_program_place     ON program(place_id);
CREATE INDEX IF NOT EXISTS idx_conversation_date ON conversation(user_id, created_at DESC);
