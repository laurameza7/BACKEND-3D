package co.edu.ucc.pasto3d.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class SaludController {

    @GetMapping("/api/salud")
    public Map<String, Object> salud() {
        return Map.of(
                "mensaje", "Hello World desde el backend de UCC Pasto 3D",
                "estado", "ok",
                "fecha", Instant.now().toString());
    }
}


