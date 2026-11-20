package co.edu.ucc.pasto3d.controller;

import co.edu.ucc.pasto3d.ai.AssistantMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    /** "/api/salud" is the first path of this endpoint: it is kept so the Render health check keeps working. */
    @GetMapping({"/api/health", "/api/salud"})
    public Map<String, Object> health() {
        return Map.of(
                "message", "Hello World from the UCC Pasto 3D backend",
                "status", "ok",
                "date", Instant.now().toString(),
                "coopiMetrics", AssistantMetrics.instance().summary());   // SINGLETON
    }
}


