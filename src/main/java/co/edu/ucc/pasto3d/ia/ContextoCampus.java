package co.edu.ucc.pasto3d.ia;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import java.util.List;

/** Información del campus que el asistente usa como conocimiento (viene de la base de datos). */
public record ContextoCampus(List<EdificioDTO> edificios,
                             List<LugarDTO> lugares,
                             List<ProgramaDTO> programas,
                             List<PreguntaFrecuenteDTO> preguntas) { }
