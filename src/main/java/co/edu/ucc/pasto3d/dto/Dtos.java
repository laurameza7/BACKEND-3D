package co.edu.ucc.pasto3d.dto;

/** DTO pattern: objects that travel between backend and frontend without exposing the JPA entities. */
public final class Dtos {
    private Dtos() { }

    public record BuildingDTO(Integer id, String code, String name, String description,
                              double x, double z, double width, double depth,
                              int floors, int basements, String color, String type) { }

    public record PlaceDTO(Integer id, String name, String type, Integer buildingId, String building,
                           Integer floor, String description, String schedule, String phone, String email) { }

    public record ProgramDTO(Integer id, String name, String faculty, String level, String modality,
                              Integer durationSemesters, String degreeTitle, String description,
                              Integer buildingId, String building,
                              /** Faculty of the program inside the campus (to take the visitor there). */
                              Integer placeId, String place, String location) { }

    public record FaqDTO(Integer id, String category, String question, String answer,
                                       String keywords) { }

    /** programId (optional): the program the user is looking at, so Coopi answers about it. */
    public record QuestionDTO(
            @jakarta.validation.constraints.NotBlank
            @jakarta.validation.constraints.Size(max = 500) String question,
            Integer programId) { }

    /* ----- Users ----- */
    public record RegisterDTO(
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 80) String name,
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 120) String email,
            /** SHA-256 of the password computed in the browser (never the password itself). */
            @jakarta.validation.constraints.NotBlank String fingerprint) { }

    public record LoginDTO(
            @jakarta.validation.constraints.NotBlank String email,
            @jakarta.validation.constraints.NotBlank String fingerprint) { }

    public record SessionDTO(String token, Integer id, String name, String email, String role) { }

    /**
     * Action the frontend must run after showing the answer (COMMAND pattern):
     * GREETING, SHOW_PROGRAMS, GO_TO_PROGRAM, GO_TO_PLACE or GO_TO_BUILDING.
     */
    public record ActionDTO(String type, Integer programId, Integer placeId, Integer buildingId,
                            Integer floor, String label) {
        public static ActionDTO of(String type) { return new ActionDTO(type, null, null, null, null, null); }
    }

    /** buildingId: if the answer mentions a block, the frontend highlights it on the 3D map. */
    public record AnswerDTO(String answer, String provider, Integer buildingId, ActionDTO action) {
        public AnswerDTO(String answer, String provider, Integer buildingId) {
            this(answer, provider, buildingId, null);
        }
    }

    public record HistoryMessageDTO(String question, String answer, String date) { }
}



