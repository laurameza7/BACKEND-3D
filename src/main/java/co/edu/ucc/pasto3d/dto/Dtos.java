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
                              Integer edificioId, String edificio,
                              /** Facultad del programa dentro del campus (para llevar al visitante allí). */
                              Integer lugarId, String lugar, String ubicacion) { }

    public record PreguntaFrecuenteDTO(Integer id, String categoria, String pregunta, String respuesta,
                                       String palabrasClave) { }

    /** programaId (opcional): el programa que el usuario está viendo, para que Coopi responda sobre él. */
    public record PreguntaDTO(
            @jakarta.validation.constraints.NotBlank
            @jakarta.validation.constraints.Size(max = 500) String pregunta,
            Integer programaId) { }

    /* ----- Usuarios ----- */
    public record RegistroDTO(
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 80) String nombre,
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 120) String correo,
            /** SHA-256 de la contraseña calculado en el navegador (nunca la contraseña). */
            @jakarta.validation.constraints.NotBlank String huella) { }

    public record IngresoDTO(
            @jakarta.validation.constraints.NotBlank String correo,
            @jakarta.validation.constraints.NotBlank String huella) { }

    public record SesionDTO(String token, Integer id, String nombre, String correo, String rol) { }

    /**
     * Acción que el frontend debe ejecutar después de mostrar la respuesta (patrón COMMAND):
     * SALUDO, VER_PROGRAMAS, IR_PROGRAMA, IR_LUGAR o IR_EDIFICIO.
     */
    public record AccionDTO(String tipo, Integer programaId, Integer lugarId, Integer edificioId,
                            Integer piso, String etiqueta) {
        public static AccionDTO de(String tipo) { return new AccionDTO(tipo, null, null, null, null, null); }
    }

    /** edificioId: si la respuesta menciona un bloque, el frontend lo resalta en el mapa 3D. */
    public record RespuestaDTO(String respuesta, String proveedor, Integer edificioId, AccionDTO accion) {
        public RespuestaDTO(String respuesta, String proveedor, Integer edificioId) {
            this(respuesta, proveedor, edificioId, null);
        }
    }

    public record MensajeHistorialDTO(String pregunta, String respuesta, String fecha) { }
}



