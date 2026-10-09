package co.edu.ucc.pasto3d.dto;

/** Patrón DTO: objetos que viajan entre backend y frontend sin exponer las entidades JPA. */
public final class Dtos {
    private Dtos() { }

    public record EdificioDTO(Integer id, String codigo, String nombre, String descripcion,
                              double x, double z, double ancho, double profundidad,
                              int pisos, int sotanos, String color, String tipo) { }

    public record LugarDTO(Integer id, String nombre, String tipo, Integer edificioId, String edificio,
                           Integer piso, String descripcion, String horario, String telefono, String correo) { }

    public record ProgramaDTO(Integer id, String nombre, String facultad, String nivel, String modalidad,
                              Integer duracionSemestres, String titulo, String descripcion,
                              Integer edificioId, String edificio) { }

    public record PreguntaFrecuenteDTO(Integer id, String categoria, String pregunta, String respuesta,
                                       String palabrasClave) { }

    public record PreguntaDTO(
            @jakarta.validation.constraints.NotBlank
            @jakarta.validation.constraints.Size(max = 500) String pregunta) { }

    /** edificioId: si la respuesta menciona un bloque, el frontend lo resalta en el mapa 3D. */
    public record RespuestaDTO(String respuesta, String proveedor, Integer edificioId) { }
}
